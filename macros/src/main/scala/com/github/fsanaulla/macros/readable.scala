package com.github.fsanaulla.macros

import com.github.fsanaulla.macros.AnnotationHelper._
import spray.json.{DeserializationException, JsArray, JsNumber, JsString}

import scala.collection.immutable.Seq
import scala.meta._

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 14.09.17
  */
final class readable extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    val modelImport = {
      q"""
         import com.github.fsanaulla.model._
       """
    }

    val sprayImport = {
      q"""
         import spray.json._
       """
    }

    val implicitImport = {
      q"""
         import com.github.fsanaulla.utils.Implicits._
       """
    }

    defn match {
      case cls @ Defn.Class(_, tName, _, ctor, _) =>

        val reader = createReader(tName, ctor.paramss.flatten)

        q"""
           $modelImport
           $sprayImport
           $implicitImport

           $cls

           $reader
         """

      case Term.Block(Seq(cls @ Defn.Class(_, tName, _, ctor, _), Defn.Object(_, _, _))) =>

        val reader = createReader(tName, ctor.paramss.flatten)

        q"""
           $modelImport
           $sprayImport
           $implicitImport

           $cls

           $reader
         """

      /**
        * Match all other cases
        */
      case _ => abort("Can be applied only for case classes")

    }
  }
}
