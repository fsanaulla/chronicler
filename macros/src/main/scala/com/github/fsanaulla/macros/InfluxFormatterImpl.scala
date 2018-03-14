package com.github.fsanaulla.macros

import com.github.fsanaulla.macros.annotations.{field, tag}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.02.18
  */
private[macros] object InfluxFormatterImpl {

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def writer_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._

    /** Predicate for finding fields of instance marked with '@tag' annotation */
    def isTag(m: MethodSymbol): Boolean =
      m.annotations.exists(_.tree.tpe =:= typeOf[tag])

    /** Predicate for finding fields of instance marked with '@field' annotation */
    def isField(m: MethodSymbol): Boolean =
      m.annotations.exists(_.tree.tpe =:= typeOf[field])

    val tpe = c.weakTypeOf[T]

    val methods: List[MethodSymbol] = tpe.decls.toList collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }

    // If `methods` comes up empty we raise a compilation error:
    if (methods.lengthCompare(1) < 0) {
      c.abort(c.enclosingPosition, "Type parameter must be a case class with more then 1 fields")
    }

    val tags = methods collect {
      case m: MethodSymbol if isTag(m) =>
        q"${m.name.decodedName.toString} -> obj.${m.name}"
    }

    val fields = methods collect {
      case m: MethodSymbol if isField(m) =>
        q"${m.name.decodedName.toString} -> obj.${m.name}"
    }

    q"""
       new InfluxWriter[$tpe] {
          def write(obj: $tpe): String = {
            val tags: Map[String, Any] = Map(..$tags)
            val fields: Map[String, Any] = Map(..$fields)

            val preparedTags = tags map { case (k, v) => k + "=" + v } mkString(",")
            val preparedFields = fields map { case (k, v) => k + "=" + v } mkString(" ")

            preparedTags + " " + preparedFields trim
          }
       }
      """
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def reader_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._

    val tpe = c.weakTypeOf[T]

    val methods = tpe.decls.toList collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        m.name.decodedName.toString -> m.returnType.dealias
    }

    if (methods.lengthCompare(1) < 0) {
      c.abort(c.enclosingPosition, "Type parameter must be a case class with more then 1 fields")
    }

    val bool = typeOf[Boolean].dealias
    val int = typeOf[Int].dealias
    val long = typeOf[Long].dealias
    val double = typeOf[Double].dealias
    val string = typeOf[String].dealias

    val params = methods
      .map(_._1)
      .sorted
      .map(v => TermName(v))
      .map(v => q"$v = $v")

    val jsParams = methods
      .sortBy(_._1) // influx return results in alphabetical order
      .map { case (k, v) => TermName(k) -> v }
      .map {
        case (name, `bool`) => q"JsBoolean($name)"
        case (name, `string`) => q"JsString($name)"
        case (name, `int`) => q"JsNumber($name)"
        case (name, `long`) => q"JsNumber($name)"
        case (name, `double`) => q"JsNumber($name)"
        case (_, other) => c.abort(c.enclosingPosition, s"Unknown type $other")
      }

    jsParams.foreach(println)

    val failureMsg = s"Can't deserialize $tpe object"

    val result = q"""
       new InfluxReader[$tpe] {
          def read(js: JsArray): $tpe = js.elements.tail match {
            case Vector(..$jsParams) => new $tpe(..$params)
            case _ => throw DeserializationException($failureMsg)
          }
       }
      """
    println(result)

    result
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def format_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    ???
  }
}
