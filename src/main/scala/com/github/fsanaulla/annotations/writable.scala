package com.github.fsanaulla.annotations

import scala.meta._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.09.17
  */
class writable extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {

    val q"..$mods class $tName (..$params) extends $template" = defn

    // filtering by @tag annotation
    val tagsParam = for {
      param <- params
      modifier <- param.mods
      newParam <- modifier match {
        case mod"@tag" => Some(param)
        case _ => None
      }
    } yield newParam.copy(mods = Nil)

    // filtering by @field annotation
    val fieldsParam = for {
      param <- params
      modifier <- param.mods
      newParam <- modifier match {
        case mod"@field" => Some(param)
        case _ => None
      }
    } yield newParam.copy(mods = Nil)

    val tagPairs: scala.collection.immutable.Seq[Term] = tagsParam.map { param =>
      val memberName = Term.Name(param.name.value)
      q"${param.name.value} -> obj.$memberName"
    }

    val fieldPairs: scala.collection.immutable.Seq[Term] = fieldsParam.map { param =>
      val memberName = Term.Name(param.name.value)
      q"${param.name.value} -> obj.$memberName"
    }

    q"""
        import com.github.fsanaulla.model._

        ..$mods class $tName (..${params.map(_.copy(mods = Nil))}) extends $template

        implicit val ${Pat.Var.Term(Term.Name(tName.syntax + "InfluxWriter"))}: InfluxWriter[$tName] = new InfluxWriter[$tName] {
          override def write(obj: $tName): String = {
            val tagsMap = Map[String, Any](..$tagPairs)
            val fieldMap = Map[String, Any](..$fieldPairs)

            val tagStr = tagsMap.map { case(k, v) => k + "=" + v }.mkString(",")
            val fieldStr = fieldMap.map { case(k, v) => k + "=" + v }.mkString(" ")

            tagStr + " " + fieldStr
          }
        }
      """
  }
}