package com.github.fsanaulla.macros

import com.github.fsanaulla.macros.AnnotationHelper._

import scala.annotation.compileTimeOnly
import scala.collection.immutable._
import scala.meta._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 08.09.17
  */
@compileTimeOnly("Required macro paradise plugin")
final class writable extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    val imports: Import = {
      q"""
         import com.github.fsanaulla.model._
       """
    }

    defn match {

      /**
        * Match case class without companion object
        */
      case cls @ Defn.Class(_, tName, _, ctor, _) =>

        val clazz = pureClass(cls)

        val params = ctor.paramss.flatten

        val (tagPairs, fieldPairs) = generateParams(params)

        val writer = createWriter(tName, tagPairs, fieldPairs)

        q"""
           $imports

           $clazz

           $writer
         """

      /**
        * Match case class with with an existing companion object
        */
      case Term.Block(Seq(cls @ Defn.Class(_, clsName, _, ctor, _), Defn.Object(_, _, _))) =>

        val clazz = pureClass(cls)

        val params = ctor.paramss.flatten

        val (tagParams, fieldParams) = generateParams(params)

        val writer = createWriter(clsName, tagParams, fieldParams)

        q"""
           $imports

           $clazz

           $writer
         """

      /**
        * Match all other cases
        */
      case _ => abort("Can be applied only for case classes")
    }
  }
}