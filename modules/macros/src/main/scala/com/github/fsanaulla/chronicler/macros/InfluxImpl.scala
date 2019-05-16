/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.macros

import com.github.fsanaulla.chronicler.core.implicits.RichString
import com.github.fsanaulla.chronicler.macros.annotations._
import com.github.fsanaulla.chronicler.macros.annotations.reader.{epoch, utc}
import com.github.fsanaulla.chronicler.macros.annotations.writer.escape

import scala.reflect.macros.blackbox

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.02.18
  */
private[macros] final class InfluxImpl(val c: blackbox.Context) {
  import c.universe._

  type FieldInfo = (String, c.universe.Type)

  /** return type dealias */
  private def getType[A: TypeTag]: c.universe.Type = typeOf[A].dealias

  private[this] val bool: c.universe.Type = getType[Boolean]
  private[this] val int: c.universe.Type = getType[Int]
  private[this] val long: c.universe.Type = getType[Long]
  private[this] val double: c.universe.Type = getType[Double]
  private[this] val string: c.universe.Type = getType[String]
  private[this] val optString: c.universe.Type = getType[Option[String]]

  private[this] val timestamp: c.universe.Type = getType[timestamp]

  private[this] val timestampTypes = Seq(getType[Long], getType[String])
  private[this] val tagsTypes =
    Seq(getType[Option[String]], getType[String])
  private[this] val fieldTypes =
    Seq(getType[Boolean], getType[Int], getType[Double], getType[String], getType[Float])

  private def illegalArgExc(name: String): c.universe.Tree = {
    val msg = s"Tag value can 't be empty string for tag: $name"
    q"new IllegalArgumentException($msg)"
  }

  /** Check if this method valid timestamp */
  private def isTimestamp(m: MethodSymbol, isReader: Boolean): Boolean = {
    // check if it has @timestamp annotation
    if (m.annotations.exists(_.tree.tpe =:= timestamp)) {

      // if it's reader it will have Long/String supported time, otherwise only Long
      if (isReader) {
        if (timestampTypes.exists(_ =:= m.returnType)) true
        else {
          // reader supports only string and long
          compileError(s"""@timestamp field: "${m.name}" has unsupported type ${m.returnType}. Timestamp must be Long/String for InfluxReader""")
        }
      } else {
        if (m.returnType =:= long) true
        else {
          // writer supports only long
          compileError(s"""@timestamp field: "${m.name}" has unsupported type ${m.returnType}. Timestamp must be Long for InfluxWriter""")
        }
      }
    } else false
  }

  private def getFieldInfo(lst: List[MethodSymbol]): List[FieldInfo] =
    lst.map(m => m.name.decodedName.toString -> m.returnType.dealias)

  private def getMethods(tpe: c.universe.Type): List[MethodSymbol] =
    tpe.decls.toList.collect { case m: MethodSymbol if m.isCaseAccessor => m }

  private def compileError(msg: String): Nothing =
    c.abort(c.enclosingPosition, msg)

  /**
    * Generate read method for specified type
    *
    * @param tpe  - for which type
    * @return     - AST that will be expanded to read method
    */
  private def createReadMethod(tpe: c.universe.Type): Tree = {

    def successCase(tp: c.universe.Type,
                    patterns: List[Tree],
                    constructors: List[Tree]): c.universe.Tree = {
      val successPat  = pq"Array(..$patterns)"
      val successBody = q"scala.util.Right(new $tp(..$constructors))"
      cq"$successPat => $successBody"
    }

    def failureCase(tp: c.universe.Type): c.universe.Tree = {
      // failure case clause component
      val failurePat  = pq"_"
      val failureMsg  = s"Can't deserialize $tp object."
      val failureBody = q"scala.util.Left(new com.github.fsanaulla.chronicler.core.model.ParsingException($failureMsg))"
      cq"$failurePat => $failureBody"
    }

    // check if marked with @timestampEpoch annotation and have Long type
    def isEpoch(annotations: List[c.universe.Annotation], tp: c.universe.Type): Boolean =
      annotations.exists(_.tree.tpe =:= getType[epoch]) && tp =:= long

    def isUtc(annotations: List[c.universe.Annotation], tp: c.universe.Type): Boolean =
      annotations.exists(_.tree.tpe =:= getType[utc]) && tp =:= string

    def simpleRead(timestampCtor: Tree,
                   timestampPat: Tree,
                   patterns: List[Tree],
                   constructors: List[Tree]): c.universe.Tree = {
      val sCase = successCase(tpe, timestampPat :: patterns, timestampCtor :: constructors)
      val fCase = failureCase(tpe)
      val cases = sCase :: fCase :: Nil

      q"""def read(js: jawn.ast.JArray): com.github.fsanaulla.chronicler.core.alias.ErrorOr[$tpe] = js.vs match { case ..$cases }"""
    }

    def timestampPattern(timestamp: TermName): Tree =
      pq"$timestamp: jawn.ast.JValue"

    val (timeField, othFields) = getMethods(tpe).partition(isTimestamp(_, isReader = true))

    if (othFields.lengthCompare(1) < 0)
      compileError("Type parameter must be a case class with more then 1 fields.")

    val fields = getFieldInfo(othFields)

    val constructorParams = fields
      .sortBy(_._1)
      .map { case (k, v) => TermName(k) -> v }
      .map {
        case (k, `bool`)      => q"$k = $k.asBoolean"
        case (k, `string`)    => q"$k = $k.asString"
        case (k, `int`)       => q"$k = $k.asInt"
        case (k, `long`)      => q"$k = $k.asLong"
        case (k, `double`)    => q"$k = $k.asDouble"
        case (k, `optString`) => q"$k = $k.getString"
        case (_, other)       => compileError(s"Unsupported type $other")
      }

    val patternParams: List[Tree] = fields
      .map(_._1)
      .sorted // influx return results in alphabetical order
      .map(k => pq"${TermName(k)}: jawn.ast.JValue")

    timeField.headOption match {
      // marked as @timestamp and @epoch
      case Some(m) if isEpoch(m.annotations, m.returnType) =>
        val timestamp = TermName(m.name.decodedName.toString)
        val timeConstructor = q"$timestamp = $timestamp.asLong"
        simpleRead(timeConstructor, timestampPattern(timestamp), patternParams, constructorParams)

      // marked as @timestamp and @utc
      case Some(m) if isUtc(m.annotations, m.returnType) =>
        val timestamp = TermName(m.name.decodedName.toString)
        val timeConstructor = q"$timestamp = $timestamp.asString"
        simpleRead(timeConstructor, timestampPattern(timestamp), patternParams, constructorParams)

      // marked as @timestamp
      case Some(m) =>
        val timestamp = TermName(m.name.decodedName.toString)
        val constructorTime =
          if (m.returnType =:= long) q"$timestamp = toEpoch($timestamp)"
          else q"$timestamp = toUTC($timestamp)"

        val patternTime: Tree = pq"$timestamp: jawn.ast.JValue"
        val sCase = successCase(tpe, patternTime :: patternParams, constructorTime :: constructorParams)
        val fCase = failureCase(tpe)
        val cases = sCase :: fCase :: Nil
          q"""
             def read(js: jawn.ast.JArray): com.github.fsanaulla.chronicler.core.alias.ErrorOr[$tpe] = {
               @inline def toEpoch(jv: jawn.ast.JValue): Long = {
                  jv.getString.fold(jv.asLong) { str =>
                     val i = java.time.Instant.parse(str)
                     i.getEpochSecond * 1000000000 + i.getNano
                 }
               }
               @inline def toUTC(jv: jawn.ast.JValue): String = {
                  jv.getLong.fold(jv.asString) { l =>
                     java.time.Instant.ofEpochMilli(l / 1000000).plusNanos(l % 1000000).toString
                  }
               }
              js.vs match { case ..$cases }
             }
        """

      // mo timestamp
      case _ =>
        val sCase = successCase(tpe, patternParams, constructorParams)
        val fCase = failureCase(tpe)
        val cases = sCase :: fCase :: Nil
        q"def read(js: jawn.ast.JArray): com.github.fsanaulla.chronicler.core.alias.ErrorOr[$tpe] = js.vs.tail match { case ..$cases }"
    }
  }

  /**
    * Create write method for specified type
    *
    * @param tpe - specified type
    * @return    - AST that will be expanded to write method
    */
  private def createWriteMethod(tpe: c.Type): Tree = {
    /// ADT
    sealed trait Unquotable {
      def key: Name
      def value: Tree
      /** Unquote class into Tree */
      def unquoted(head: Boolean): Tree
      final def name: String = key.decodedName.toString.escapeKey
    }
    sealed trait Field extends Unquotable

    final class Tag(val key: Name,
                    val value: Tree,
                    optional: Boolean,
                    escapable: Boolean) extends Unquotable {
      def escaped(value: Tree): c.universe.Tree =
        q"com.github.fsanaulla.chronicler.core.regex.tagPattern.matcher($value).replaceAll(com.github.fsanaulla.chronicler.core.regex.replace)"

      def unquoted(head: Boolean): Tree = optional -> escapable match {
        case (true, true) =>
          q"""
              for (v <- $value) {
                if (v.isEmpty) return Left(${illegalArgExc(name)})
                else sb.append(${if (head) q"""$name + "=" + v""" else q""""," + $name + "=" + com.github.fsanaulla.chronicler.core.regex.tagPattern.matcher(v).replaceAll(com.github.fsanaulla.chronicler.core.regex.replace)"""})
              }
            """
        case (true, false) =>
          q"""
              for (v <- $value) {
                if (v.isEmpty) return Left(${illegalArgExc(name)})
                else sb.append(${if (head) q"""$name + "=" + v""" else q""""," + $name + "=" + v"""})
              }
            """
        case (false, true) =>
          q"""if ($value.isEmpty) return Left(${illegalArgExc(name)})
              else sb.append(${if (head) q"""$name + "=" + ${escaped(value)}""" else q""""," + $name + "=" + ${escaped(value)}"""})
            """
        case _ =>
          q"""if ($value.isEmpty) return Left(${illegalArgExc(name)})
              else sb.append(${if (head) q"""$name + "=" + $value""" else q""""," + $name + "=" + $value"""})
            """
      }
    }

    final class IntField(val key: Name, val value: Tree) extends Field {
      override def unquoted(head: Boolean): c.universe.Tree = {
        val str = if (head)
          q"""new String($name + "=" + $value + "i")"""
        else
          q"""new String("," + $name + "=" + $value + "i")"""

        q"sb.append($str)"
      }
    }

    final class StringField(val key: Name, val value: Tree) extends Field {
      override def unquoted(head: Boolean): c.universe.Tree = {
        val str = if (head)
          q"""new String($name + "=" + "\"" + $value + "\"")"""
        else
          q"""new String("," + $name + "=" + "\"" + $value + "\"")"""

        q"sb.append($str)"
      }
    }

    final class OtherField(val key: Name, val value: Tree) extends Field {
      override def unquoted(head: Boolean): c.universe.Tree = {
        val str = if (head)
          q"""new String("," + $name + "=" + $value)"""
        else
          q"""new String("," + $name + "=" + $value)"""

        q"sb.append($str)"
      }
    }

    def isOption(tpe: c.universe.Type): Boolean =
      tpe.typeConstructor =:= typeOf[Option[_]].typeConstructor
    def isString(tpe: c.universe.Type): Boolean = tpe =:= string
    def isInt(tpe: c.universe.Type): Boolean = tpe =:= int

    /** Is it valid tag type */
    def isTagType(tpe: c.universe.Type): Boolean =
      tagsTypes.exists(t => t =:= tpe)

    /** Is it valid field type */
    def isFieldType(tpe: c.universe.Type): Boolean =
      fieldTypes.exists(t => t =:= tpe)

    /** Predicate for finding fields of instance marked with '@tag' annotation */
    def isTag(m: MethodSymbol): Boolean = {
      if (m.annotations.exists(_.tree.tpe =:= typeOf[tag])) {
        if (isTagType(m.returnType)) true
        else compileError(s"@tag ${m.name} has unsupported type ${m.returnType}. Tag must have String or Optional[String]")
      } else false
    }

    /** Predicate for finding fields of instance marked with '@field' annotation */
    def isField(m: MethodSymbol): Boolean = {
      if (m.annotations.exists(_.tree.tpe =:= typeOf[field])) {
        if (isFieldType(m.returnType)) true
        else compileError(s"Unsupported type for @field ${m.name}: ${m.returnType}")
      } else false
    }

    def isEscaped(m: MethodSymbol): Boolean =
      m.annotations.exists(_.tree.tpe =:= typeOf[escape])

    /** Check method for one of @tag, @field annotations */
    def isAnnotated(m: MethodSymbol): Boolean = isTag(m) || isField(m)

    def unquote(lst: List[Unquotable]): Tree = {
      lst.tail.foldLeft(q"${lst.head.unquoted(true)}") { (acc, e) =>
        q"""
         $acc
         ${e.unquoted(false)}
        """
      }
    }

    val (timeField, othField) = getMethods(tpe).partition(isTimestamp(_, isReader = false))

    if (othField.lengthCompare(1) < 0)
      compileError(
        s"""
           |Error: annotated fields were not found
           |
           |While generating InfluxWriter[$tpe] error was found.
           |Class: '$tpe' -  must be a case class with at least 2 annotated fields.
           |For more info: https://github.com/fsanaulla/chronicler/blob/master/docs/macros.md.
           |
           |""".stripMargin)

    if (timeField.size > 1)
      compileError(
        s"""
           |Error: too much '@timestamp' annotations were found
           |
           |While generating InfluxWriter[$tpe] error was found.
           |Class: '$tpe' -  must be a case class with only one '@timestamp' field.
           |For more info: https://github.com/fsanaulla/chronicler/blob/master/docs/macros.md.
           |
           |""".stripMargin)

    val (tagMethods, fieldMethods) = othField
      .filter(isAnnotated)
      .span {
        case m: MethodSymbol if isTag(m) => true
        case _ => false
      }

    val tags: List[Tag] = tagMethods map {
      case m: MethodSymbol if !isOption(m.returnType) =>
        new Tag(m.name.decodedName, q"obj.${m.name}", optional = false, isEscaped(m))
      case m: MethodSymbol =>
        new Tag(m.name.decodedName, q"obj.${m.name}", optional = true, isEscaped(m))
    }

    val fields: List[Field] = fieldMethods map {
      case m: MethodSymbol if isString(m.returnType) =>
        new StringField(m.name, q"obj.${m.name}")
      case m: MethodSymbol if isInt(m.returnType) =>
        new IntField(m.name, q"obj.${m.name}")
      case m: MethodSymbol =>
        new OtherField(m.name, q"obj.${m.name}")
    }

    val tagString = unquote(tags)
    val fieldString = unquote(fields)
    val timestampString = timeField.headOption.fold(q"") { m =>
      q"""
         sb.append(" " + obj.${m.name})
       """
    }

    q"""def write(obj: $tpe): com.github.fsanaulla.chronicler.core.alias.ErrorOr[String] = {
          val sb = StringBuilder.newBuilder
          $tagString
          sb.append(" ")
          $fieldString

          $timestampString

          scala.util.Right(sb.toString)
      }"""
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def writer[T: c.WeakTypeTag]: Tree = {
    val tpe = c.weakTypeOf[T]
    q"new com.github.fsanaulla.chronicler.core.model.InfluxWriter[$tpe] { ${createWriteMethod(tpe)} }"
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def reader[T: c.WeakTypeTag]: Tree = {
    val tpe = c.weakTypeOf[T]
    q"new com.github.fsanaulla.chronicler.core.model.InfluxReader[$tpe] { ${createReadMethod(tpe)} }"
  }
}
