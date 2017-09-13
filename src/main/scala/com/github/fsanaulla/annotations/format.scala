package com.github.fsanaulla.annotations

import scala.collection.immutable._
import scala.meta._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.09.17
  */
class format extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {

    def createApply(name: Type.Name, paramss: Seq[Seq[Term.Param]]): Defn.Def = {
      val args = paramss.map(_.map(param => Term.Name(param.name.value)))
      q"""def apply(...${paramss.map(_.map(_.copy(mods = Nil)))}): $name = new ${Ctor.Ref.Name(name.value)}(...$args)"""
    }

    def generateTagField(params: Seq[Term.Param]): (Seq[Term], Seq[Term]) = {
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

      val tagPairs: Seq[Term] = tagsParam.map { param =>
        val memberName = Term.Name(param.name.value)
        q"${param.name.value} -> obj.$memberName"
      }

      val fieldPairs: Seq[Term] = fieldsParam.map { param =>
        val memberName = Term.Name(param.name.value)
        q"${param.name.value} -> obj.$memberName"
      }

      tagPairs -> fieldPairs
    }

    defn match {
      case _@Defn.Class(mods, tName, _, ctor, template) =>

        val params = ctor.paramss.flatten

        val (tagPairs, fieldPairs) = generateTagField(params)

        val applyMethod = createApply(tName, ctor.paramss)

        q"""
        import com.github.fsanaulla.model._

        ..$mods class $tName (..${params.map(_.copy(mods = Nil))}) extends $template

        object ${Term.Name(tName.value)} {
           $applyMethod

           implicit val ${Pat.Var.Term(Term.Name(tName.syntax + "InfluxWriter"))}: InfluxWriter[$tName] = new InfluxWriter[$tName] {
              override def write(obj: $tName): String = {
                val tagsMap = Map[String, Any](..$tagPairs)
                val fieldMap = Map[String, Any](..$fieldPairs)

                val tagStr = tagsMap.map { case(k, v) => k + "=" + v }.mkString(",")
                val fieldStr = fieldMap.map { case(k, v) => k + "=" + v }.mkString(" ")

                tagStr + " " + fieldStr
              }
          }
        }
      """

      case Term.Block(Seq(cls @ Defn.Class(clsMods, clsName, _, ctor, clsTemplate), companion @ Defn.Object(objMods, objName, objTemplate))) =>
        val applyMethod = createApply(clsName, ctor.paramss)

        val tempStat = applyMethod +: companion.templ.stats.getOrElse(Nil)

        val (tagPairs, fieldPairs) = generateTagField(ctor.paramss.flatten)
        q"""
           import com.github.fsanaulla.model._

           ..$clsMods class $clsName (..${ctor.paramss.flatten.map(_.copy(mods = Nil))}) extends $clsTemplate

           ..$objMods object ${Term.Name(clsName.value)} {

             implicit val ${Pat.Var.Term(Term.Name(clsName.syntax + "InfluxWriter"))}: InfluxWriter[$clsName] = new InfluxWriter[$clsName] {
                override def write(obj: $clsName): String = {
                  val tagsMap = Map[String, Any](..$tagPairs)
                  val fieldMap = Map[String, Any](..$fieldPairs)

                  val tagStr = tagsMap.map { case(k, v) => k + "=" + v }.mkString(",")
                  val fieldStr = fieldMap.map { case(k, v) => k + "=" + v }.mkString(" ")

                  tagStr + " " + fieldStr
                }
             }

             ..$tempStat
           }"""

      case _ => abort("Can be applied for case classes")
    }
  }
}