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
  def getType[A: TypeTag]: c.universe.Type = typeOf[A].dealias

  val bool: c.universe.Type = getType[Boolean]
  val int: c.universe.Type = getType[Int]
  val long: c.universe.Type = getType[Long]
  val double: c.universe.Type = getType[Double]
  val string: c.universe.Type = getType[String]
  val optString: c.universe.Type = getType[Option[String]]

  private[this] val timestampTypes = Seq(getType[Long], getType[String])
  private[this] val tagsTypes =
    Seq(getType[Option[String]], getType[String])
  private[this] val fieldTypes =
    Seq(getType[Boolean], getType[Int], getType[Double], getType[String], getType[Float])
  private[this] val annotationTypes =
    Seq(getType[timestamp], getType[timestampEpoch], getType[timestampUTC])

  /** Check if this method valid timestamp */
  def isTimestamp(m: MethodSymbol): Boolean = {
    if (m.annotations.count(isTimestampAnnotation) > 1)
      error("Only one timestamp annotation can be used.")
    else if (m.annotations.exists(isTimestampAnnotation)) {
      if (isTimestampType(m.returnType)) true
      else error(s"@timestamp/timestampEpoch/timestampUTC ${m.name} has unsupported type ${m.returnType}. Timestamp must be Long")
    } else false
  }

  def isTimestampAnnotation(m: c.universe.Annotation): Boolean =
    annotationTypes.exists(_ =:= m.tree.tpe)

  def isTimestampType(t: c.universe.Type): Boolean =
    timestampTypes.exists(_ =:= t)

  def getFieldInfo(lst: List[MethodSymbol]): List[FieldInfo] =
    lst.map(m => m.name.decodedName.toString -> m.returnType.dealias)

  def getMethods(tpe: c.universe.Type): List[MethodSymbol] =
    tpe.decls.toList.collect { case m: MethodSymbol if m.isCaseAccessor => m }

  def error(msg: String): Nothing = c.abort(c.enclosingPosition, msg)

  /**
    * Generate read method for specified type
    *
    * @param tpe  - for which type
    * @return     - AST that will be expanded to read method
    */
  def createReadMethod(tpe: c.universe.Type): Tree = {

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
    def isEpochTime(annotations: List[c.universe.Annotation], tp: c.universe.Type): Boolean =
      annotations.exists(_.tree.tpe =:= getType[timestampEpoch]) && tp =:= long

    def isUTCTime(annotations: List[c.universe.Annotation], tp: c.universe.Type): Boolean =
      annotations.exists(_.tree.tpe =:= getType[timestampUTC]) && tp =:= string

    def isGenericTime(annotations: List[c.universe.Annotation], tp: c.universe.Type): Boolean =
      annotations.exists(_.tree.tpe =:= getType[timestamp]) && (tp =:= long || tp =:= string)

    def simpleRead(name: String,
                   tp: c.universe.Type,
                   patterns: List[Tree],
                   constructors: List[Tree]): c.universe.Tree = {
      val timestamp = TermName(name)
      val constructorTime: Tree =
        if (tp =:= long) q"$timestamp = $timestamp.asLong"
        else q"$timestamp = $timestamp.asString"
      val patternTime: Tree = pq"$timestamp: jawn.ast.JValue"

      val sCase = successCase(tpe, patternTime :: patterns, constructorTime :: constructors)
      val fCase = failureCase(tpe)
      val cases = sCase :: fCase :: Nil

      q"""def read(js: jawn.ast.JArray): com.github.fsanaulla.chronicler.core.alias.ErrorOr[$tpe] = js.vs match { case ..$cases }"""
    }

    val (timeField, othFields) = getMethods(tpe).partition(isTimestamp)

    if (othFields.lengthCompare(1) < 0)
      error("Type parameter must be a case class with more then 1 fields.")

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
        case (_, other)       => error(s"Unsupported type $other")
      }

    val patternParams: List[Tree] = fields
      .map(_._1)
      .sorted // influx return results in alphabetical order
      .map(k => pq"${TermName(k)}: jawn.ast.JValue")

    timeField.headOption match {
      case Some(m) if isEpochTime(m.annotations, m.returnType) =>
        simpleRead(m.name.decodedName.toString, m.returnType, patternParams, constructorParams)
      case Some(m) if isUTCTime(m.annotations, m.returnType) =>
        simpleRead(m.name.decodedName.toString, m.returnType, patternParams, constructorParams)
      case Some(m) if isGenericTime(m.annotations, m.returnType) =>
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
  def createWriteMethod(tpe: c.Type): Tree = {

    /** Is it Option container*/
    def isOption(tpe: c.universe.Type): Boolean =
      tpe.typeConstructor =:= typeOf[Option[_]].typeConstructor

    /** Is it valid tag type */
    def isSupportedTagType(tpe: c.universe.Type): Boolean =
      tagsTypes.exists(t => t =:= tpe)

    /** Is it valid field type */
    def isSupportedFieldType(tpe: c.universe.Type): Boolean =
      fieldTypes.exists(t => t =:= tpe)

    /** Predicate for finding fields of instance marked with '@tag' annotation */
    def isTag(m: MethodSymbol): Boolean = {
      if (m.annotations.exists(_.tree.tpe =:= typeOf[tag])) {
        if (isSupportedTagType(m.returnType)) true
        else error(s"@tag ${m.name} has unsupported type ${m.returnType}. Tag must have String or Optional[String]")
      } else false
    }

    /** Predicate for finding fields of instance marked with '@field' annotation */
    def isField(m: MethodSymbol): Boolean = {
      if (m.annotations.exists(_.tree.tpe =:= typeOf[field])) {
        if (isSupportedFieldType(m.returnType)) true
        else error(s"Unsupported type for @field ${m.name}: ${m.returnType}")
      } else false
    }

    /** Check method for one of @tag, @field annotations */
    def isMarked(m: MethodSymbol): Boolean = isTag(m) || isField(m)

    val (timeField, othField) = getMethods(tpe).partition(isTimestamp)

    if (othField.lengthCompare(1) < 0)
      error("Type parameter must be a case class with more then 1 fields")

    val (tagsMethods, fieldsMethods) = othField
      .filter(isMarked)
      .span {
        case m: MethodSymbol if isTag(m) => true
        case _ => false
      }

    val optTags: Seq[Tree] = tagsMethods collect {
      case m: MethodSymbol if isOption(m.returnType) =>
        q"${m.name.decodedName.toString.escapeFull} -> obj.${m.name}"
    }

    val nonOptTags: Seq[Tree] = tagsMethods collect {
      case m: MethodSymbol if !isOption(m.returnType) =>
        q"${m.name.decodedName.toString.escapeFull} -> obj.${m.name}"
    }

    val fields: Seq[Tree] = fieldsMethods map {
      m: MethodSymbol =>
        q"${m.name.decodedName.toString.escapeFull} -> obj.${m.name}"
    }

    def write(tpe: Type,
              fields: Seq[Tree],
              nonOptTags: Seq[Tree],
              optTags: Seq[Tree],
              optTime: Option[Tree]): c.universe.Tree = {

      q"""def write(obj: $tpe): String = {
                import com.github.fsanaulla.chronicler.core.implicits.RichString
                val fields: String =  Seq[(String, Any)](..$fields) map {
                  case (k, v: String) => k + "=" + "\"" + v + "\""
                  case (k, v: Int)    => k + "=" + v + "i"
                  case (k, v)         => k + "=" + v
                } mkString(",")

                val nonOptTags: String = Seq[(String, String)](..$nonOptTags) map {
                  case (k, v) if v.nonEmpty => k + "=" + v.escapeFull
                  case (k, _) => throw new IllegalArgumentException("Tag " + k + " can't be an empty string")
                } mkString(",")

                val optTags: String = Seq[(String, Option[String])](..$optTags) collect {
                  case (k, Some(v)) => k + "=" + v.escapeFull
                } mkString(",")

                val combTags: String = if (optTags.isEmpty) nonOptTags else nonOptTags + "," + optTags

                ${optTime.fold(q"""combTags + " " + fields trim""")(t => q"""combTags + " " + fields + " " + $t trim""")}
          }"""
    }

    timeField
      .headOption
      .map(m => q"obj.${m.name}") match {
        case None => write(tpe, fields, nonOptTags, optTags, None)
        case some => write(tpe, fields, nonOptTags, optTags, some)
      }
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def writer_impl[T: c.WeakTypeTag]: Tree = {
    val tpe = c.weakTypeOf[T]
    q"new com.github.fsanaulla.chronicler.core.model.InfluxWriter[$tpe] { ${createWriteMethod(tpe)} }"
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def reader_impl[T: c.WeakTypeTag]: Tree = {
    val tpe = c.weakTypeOf[T]
    q"new com.github.fsanaulla.chronicler.core.model.InfluxReader[$tpe] { ${createReadMethod(tpe)} }"
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def format_impl[T: c.WeakTypeTag]: Tree = {
    val tpe = c.weakTypeOf[T]

    q"""new com.github.fsanaulla.chronicler.core.model.InfluxFormatter[$tpe] {
          ${createWriteMethod(tpe)}
          ${createReadMethod(tpe)}
     }"""
  }
}
