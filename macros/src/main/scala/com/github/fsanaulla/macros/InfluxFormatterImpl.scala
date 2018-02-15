package com.github.fsanaulla.macros

import com.github.fsanaulla.macros.annotations.{field, tag}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.02.18
  */
object InfluxFormatterImpl {
  def writer_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._

    /** Predicate for finding fields of instance marked with '@tag' annotation */
    def isTag(m: MethodSymbol): Boolean =
      m.accessed.annotations.exists(_.tree.tpe =:= typeOf[tag])

    /** Predicate for finding fields of instance marked with '@field' annotation */
    def isField(m: MethodSymbol): Boolean =
      m.accessed.annotations.exists(_.tree.tpe =:= typeOf[field])

    // getting type information
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

    println(s"tags: ${tags.size} ||| fields: ${fields.size}")

    q"""
       new InfluxWriter[$tpe] {
          def write(obj: $tpe): String = {
            val tags: Map[String, Any] = Map(..$tags)
            val fields: Map[String, Any] = Map(..$fields)

            val preparedTags = tags map { case (k, v) => k + "=" + v } mkString(",")
            val preparedFields = fields map { case (k, v) => k + "=" + v } mkString(" ")

            preparedTags + " " + preparedFields
          }
       }
      """
  }
}
