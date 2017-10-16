package com.github.fsanaulla.annotations

import scala.collection.immutable.{Nil, Seq}
import scala.meta._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 18.09.17
  */
object AnnotationHelper {

  def pureClass(cls: Defn.Class): Defn.Class = {

    val params: Seq[Term.Param] = cls.ctor.paramss.flatten.map(_.copy(mods = Nil))

    q"""
       ..${cls.mods} class ${cls.name} (..$params) extends ${cls.templ}
       """
  }

  /**
    * Create apply method for class
    * @param name - Class name
    * @param paramss - parameters for apply method
    * @return - generated apply method
    */
  def createApply(name: Type.Name, paramss: Seq[Seq[Term.Param]]): Defn.Def = {
    val args = paramss.map(_.map(param => Term.Name(param.name.value)))
    q"""def apply(...${paramss.map(_.map(_.copy(mods = Nil)))}): $name = new ${Ctor.Ref.Name(name.value)}(...$args)"""
  }

  /**
    * Generate 2 seq of parameters separated by annotation modifier(@tag, @field)
    * @param params - List of class parameters
    * @return - Pair of seq parameters separated by annotation above
    */
  def generateParams(params: Seq[Term.Param]): (Seq[Term], Seq[Term]) = {

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

  def createReader(clsName: Type.Name, params: Seq[Term.Param]): Defn.Val = {
    val paramsNames = params.map(_.name.value).sorted.map(v => v + " = " + v).mkString(", ")
    val paramsPair = params.map(p => p.name.value -> p.decltpe.get).sortBy(_._1)

    val jsParamsArray = paramsPair.map {
      case (name, Type.Name("String")) => s"JsString($name)"
      case (name, Type.Name("Int")) => s"JsNumber($name)"
      case (name, Type.Name("Long")) => s"JsNumber($name)"
      case (name, Type.Name("Double")) => s"JsNumber($name)"
      case (name, Type.Name("Boolean")) => s"JsBoolean($name)"
      case (_, other) => abort(s"Unknown type $other")
    }

    val defaultMessage = Lit.String(s"Can't deserialize ${Term.Name(clsName.syntax)} object")

    val successCase: Case =
      s"case Vector(_, ${jsParamsArray.mkString(", ")}) => ${clsName.syntax}($paramsNames)"
        .parse[Case]
        .get
    val defaultCase: Case =
      s"case _ => throw DeserializationException($defaultMessage)"
        .parse[Case]
        .get

    val cases = q"""js.elements match {..case { ${successCase :: defaultCase :: Nil} } }"""

    q"""
        implicit val ${Pat.Var.Term(Term.Name(clsName.syntax + "InfluxReader"))}: InfluxReader[$clsName] = (js: JsArray) => $cases
     """
  }

  /**
    * Create implicit InfluxWriter value for class
    * @param clsName - Class name
    * @param tagPairs - Parameters marked with @tag annotation
    * @param fieldPairs - Parameters marked with @field annotation
    * @return - implicit InfluxWriter[clsName] value
    */
  def createWriter(clsName: Type.Name, tagPairs: Seq[Term], fieldPairs: Seq[Term]): Defn.Val = {
    q"""
        implicit val ${Pat.Var.Term(Term.Name(clsName.syntax + "InfluxWriter"))}: InfluxWriter[$clsName] = (obj: $clsName) => {
          val tagsMap = Map[String, Any](..$tagPairs)
          val fieldMap = Map[String, Any](..$fieldPairs)

          val tagStr = tagsMap.map { case(k, v) => k + "=" + v }.mkString(",")
          val fieldStr = fieldMap.map { case(k, v) => k + "=" + v }.mkString(" ")

          tagStr + " " + fieldStr
        }"""
  }
}
