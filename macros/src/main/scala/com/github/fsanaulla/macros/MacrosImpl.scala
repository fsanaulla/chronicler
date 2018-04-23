package com.github.fsanaulla.macros

import com.github.fsanaulla.core.model.DeserializationException
import com.github.fsanaulla.macros.annotations.{field, tag}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 13.02.18
  */
private[macros] object MacrosImpl {

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def writer_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._

    def tpdls[A: TypeTag]: c.universe.Type = typeOf[A].dealias

    val SUPPORTED_TAGS_TYPES = Seq(tpdls[Option[String]], tpdls[String])
    val SUPPORTED_FIELD_TYPES = Seq(tpdls[Boolean], tpdls[Int], tpdls[Long], tpdls[Double], tpdls[String])

    /** Is it Option container*/
    def isOption(tpe: c.universe.Type): Boolean =
      tpe.typeConstructor =:= typeOf[Option[_]].typeConstructor

    def isSupportedTagType(tpe: c.universe.Type): Boolean =
      SUPPORTED_TAGS_TYPES.exists(t => t =:= tpe)

    def isSupportedFieldType(tpe: c.universe.Type): Boolean =
      SUPPORTED_FIELD_TYPES.exists(t => t =:= tpe)


    /** Predicate for finding fields of instance marked with '@tag' annotation */
    def isTag(m: MethodSymbol): Boolean = {
      if (m.annotations.exists(_.tree.tpe =:= typeOf[tag])) {
        if (isSupportedTagType(m.returnType)) true
        else c.abort(c.enclosingPosition, s"@tag ${m.name} has unsupported type ${m.returnType}. Tag must have String or Optional[String]")
      } else false
    }

    /** Predicate for finding fields of instance marked with '@field' annotation */
    def isField(m: MethodSymbol): Boolean = {
      if (m.annotations.exists(_.tree.tpe =:= typeOf[field])) {
        if (isSupportedFieldType(m.returnType)) true
        else c.abort(c.enclosingPosition, s"Unsupported type for @field ${m.name}: ${m.returnType}")
      } else false
    }

    val tpe = c.weakTypeOf[T]

    val methods: List[MethodSymbol] = tpe.decls.toList collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }

    // If `methods` comes up empty we raise a compilation error:
    if (methods.lengthCompare(1) < 0) {
      c.abort(c.enclosingPosition, "Type parameter must be a case class with more then 1 fields")
    }

    val optTags: List[c.universe.Tree] = methods collect {
      case m: MethodSymbol if isTag(m) && isOption(m.returnType) =>
        q"${m.name.decodedName.toString} -> obj.${m.name}"
    }

    val nonOptTags: List[c.universe.Tree] = methods collect {
      case m: MethodSymbol if isTag(m) && !isOption(m.returnType) =>
        q"${m.name.decodedName.toString} -> obj.${m.name}"
    }

    val fields = methods collect {
      case m: MethodSymbol if isField(m) =>
        q"${m.name.decodedName.toString} -> obj.${m.name}"
    }

    q"""
       new InfluxWriter[$tpe] {
          def write(obj: $tpe): String = {
             val fieldsMap: Map[String, Any] = Map(..$fields)
             val fields = fieldsMap map { case (k, v) => k + "=" + v } mkString(" ")

             val nonOptTagsMap: Map[String, String] = Map(..$nonOptTags)
             val nonOptTags: String = nonOptTagsMap map {
                case (k: String, v: String) => k + "=" + v
             } mkString(",")

             val optTagsMap: Map[String, Option[String]] = Map(..$optTags)
             val optTags: String = optTagsMap collect {
                case (k: String, v: Option[String]) if v.isDefined => k + "=" + v.get
             } mkString(",")

             val combTags: String = if (optTags.isEmpty) nonOptTags else nonOptTags + "," + optTags

             combTags + " " + fields trim
          }
       }"""
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def reader_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._

    def tpdls[A: TypeTag]: c.universe.Type = typeOf[A].dealias

    val tpe = c.weakTypeOf[T]

    val methods = tpe.decls.toList collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        m.name.decodedName.toString -> m.returnType.dealias
    }

    if (methods.lengthCompare(1) < 0) {
      c.abort(c.enclosingPosition, "Type parameter must be a case class with more then 1 fields")
    }

    val bool = tpdls[Boolean]
    val int = tpdls[Int]
    val long = tpdls[Long]
    val double = tpdls[Double]
    val string = tpdls[String]
    val optString = tpdls[Option[String]]

    val params = methods
      .sortBy(_._1)
      .map { case (k, v) => TermName(k) -> v }
      .map {
        case (k, `bool`) => q"$k = $k.asBoolean"
        case (k, `string`) => q"$k = $k.asString"
        case (k, `int`) => q"$k = $k.asInt"
        case (k, `long`) => q"$k = $k.asLong"
        case (k, `double`) => q"$k = $k.asDouble"
        case (k, `optString`) => q"$k = $k.getString"
        case (_, other) => c.abort(c.enclosingPosition, s"Unsupported type $other")
      }

    val paramss = methods
      .map(_._1)
      .sorted // influx return results in alphabetical order
      .map(k => TermName(k))
      .map(k => pq"$k: JValue")

    // success case clause component
    val successPat = pq"Array(..$paramss)"
    val successBody = q"new $tpe(..$params)"
    val successCase = cq"$successPat => $successBody"

    // failure case clause component
    val failurePat = pq"_"
    val failureMsg = s"Can't deserialize $tpe object"
    val failureBody = q"throw new DeserializationException($failureMsg)"
    val failureCase = cq"$failurePat => $failureBody"

    val cases = successCase :: failureCase :: Nil

    q"""
       new InfluxReader[$tpe] {
          import jawn.ast.{JValue, JArray}
          import com.github.fsanaulla.core.model.DeserializationException

          def read(js: JArray): $tpe = js.vs.tail match { case ..$cases }
       }
      """
  }

  /***
    * Generate AST for current type at compile time.
    * @tparam T - Type parameter for whom will be generated AST
    */
  def format_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._

    def tpdls[A: TypeTag]: c.universe.Type = typeOf[A].dealias

    val SUPPORTED_TAGS_TYPES = Seq(tpdls[Option[String]], tpdls[String])
    val SUPPORTED_FIELD_TYPES = Seq(tpdls[Boolean], tpdls[Int], tpdls[Long], tpdls[Double], tpdls[String])

    val tpe = c.weakTypeOf[T]

    val methods = tpe.decls.toList

    if (methods.lengthCompare(1) < 0) {
      c.abort(c.enclosingPosition, "Type parameter must be a case class with more then 1 fields")
    }

    def createWriteMethod(methods: List[c.universe.Symbol]): c.universe.Tree = {

      val writeMethods: List[MethodSymbol] = methods collect {
        case m: MethodSymbol if m.isCaseAccessor => m
      }

      /** Is it Option container*/
      def isOption(tpe: c.universe.Type): Boolean =
        tpe.typeConstructor =:= typeOf[Option[_]].typeConstructor

      def isSupportedTagType(tpe: c.universe.Type): Boolean =
        SUPPORTED_TAGS_TYPES.exists(t => t =:= tpe)

      def isSupportedFieldType(tpe: c.universe.Type): Boolean =
        SUPPORTED_FIELD_TYPES.exists(t => t =:= tpe)

      /** Predicate for finding fields of instance marked with '@tag' annotation */
      def isTag(m: MethodSymbol): Boolean = {
        if (m.annotations.exists(_.tree.tpe =:= typeOf[tag])) {
          if (isSupportedTagType(m.returnType)) true
          else c.abort(c.enclosingPosition, s"@tag ${m.name} has unsupported type ${m.returnType}. Tag must have String or Optional[String]")
        } else false
      }

      /** Predicate for finding fields of instance marked with '@field' annotation */
      def isField(m: MethodSymbol): Boolean = {
        if (m.annotations.exists(_.tree.tpe =:= typeOf[field])) {
          if (isSupportedFieldType(m.returnType)) true
          else c.abort(c.enclosingPosition, s"Unsupported type for @field ${m.name}: ${m.returnType}")
        } else false
      }

      def isMarked(m: MethodSymbol): Boolean = isTag(m) || isField(m)

//      val (tags, fields) = writeMethods
//        .filter(isMarked)
//        .span {
//          case m: MethodSymbol if isTag(m) => true
//          case _ => false
//        }

      val optTags: List[c.universe.Tree] = writeMethods collect {
        case m: MethodSymbol if isTag(m) && isOption(m.returnType) =>
          q"${m.name.decodedName.toString} -> obj.${m.name}"
      }

      val nonOptTags: List[c.universe.Tree] = writeMethods collect {
        case m: MethodSymbol if isTag(m) && !isOption(m.returnType) =>
          q"${m.name.decodedName.toString} -> obj.${m.name}"
      }

      val fields = writeMethods collect {
        case m: MethodSymbol if isField(m) =>
          q"${m.name.decodedName.toString} -> obj.${m.name}"
      }


      q"""def write(obj: $tpe): String = {
            val fieldsMap: Map[String, Any] = Map(..$fields)
            val fields = fieldsMap map { case (k, v) => k + "=" + v } mkString(" ")

            val nonOptTagsMap: Map[String, String] = Map(..$nonOptTags)
            val nonOptTags: String = nonOptTagsMap map {
              case (k: String, v: String) => k + "=" + v
            } mkString(",")

            val optTagsMap: Map[String, Option[String]] = Map(..$optTags)
            val optTags: String = optTagsMap collect {
                case (k: String, v: Option[String]) if v.isDefined => k + "=" + v.get
            } mkString(",")

            val combTags: String = if (optTags.isEmpty) nonOptTags else nonOptTags + "," + optTags

            combTags + " " + fields trim
          }"""
    }

    def createReadMethod(methods: List[c.universe.Symbol]) = {

      val readMethods = methods collect {
        case m: MethodSymbol if m.isCaseAccessor =>
          m.name.decodedName.toString -> m.returnType.dealias
      }

      val bool = tpdls[Boolean]
      val int = tpdls[Int]
      val long = tpdls[Long]
      val double = tpdls[Double]
      val string = tpdls[String]
      val optString = tpdls[Option[String]]

      val params = readMethods
        .sortBy(_._1)
        .map { case (k, v) => TermName(k) -> v }
        .map {
          case (k, `bool`) => q"$k = $k.asBoolean"
          case (k, `string`) => q"$k = $k.asString"
          case (k, `int`) => q"$k = $k.asInt"
          case (k, `long`) => q"$k = $k.asLong"
          case (k, `double`) => q"$k = $k.asDouble"
          case (k, `optString`) => q"$k = if ($k.isNull) None else $k.getString"
          case (_, other) => c.abort(c.enclosingPosition, s"Unsupported type $other")
        }

      val paramss = readMethods
        .map(_._1)
        .sorted // influx return results in alphabetical order
        .map(k => TermName(k))
        .map(k => pq"$k: JValue")

      // success case clause component
      val successPat = pq"Array(..$paramss)"
      val successBody = q"new $tpe(..$params)"
      val successCase = cq"$successPat => $successBody"

      // failure case clause component
      val failurePat = pq"_"
      val failureMsg = s"Can't deserialize $tpe object"
      val failureBody = q"throw new DeserializationException($failureMsg)"
      val failureCase = cq"$failurePat => $failureBody"

      val cases = successCase :: failureCase :: Nil

      q"""
         def read(js: JArray): $tpe = js.vs.tail match { case ..$cases }
       """
    }

    q"""
       new InfluxFormatter[$tpe] {
          import jawn.ast.{JValue, JArray}
          import com.github.fsanaulla.core.model.DeserializationException

          ${createWriteMethod(methods)}
          ${createReadMethod(methods)}
       }"""
  }
}
