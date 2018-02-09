//package com.github.fsanaulla.core.macros
//
//import scala.annotation.compileTimeOnly
//import scala.collection.immutable.Seq
//import scala.meta._
//import AnnotationHelper._
//
///**
//  * Created by
//  * Author: fayaz.sanaulla@gmail.com
//  * Date: 14.09.17
//  */
//@compileTimeOnly("Required macro paradise plugin")
//final class formattable extends scala.annotation.StaticAnnotation {
//
//  inline def apply(defn: Any): Any = meta {
//
//    println("Generating formattable...")
//
//    val modelImport = {
//      q"""
//         import com.github.fsanaulla.model._
//       """
//    }
//
//    val sprayImport = {
//      q"""
//         import spray.json._
//       """
//    }
//
//    val implicitImport = {
//      q"""
//         import com.github.fsanaulla.utils.Implicits._
//       """
//    }
//
//    defn match {
//
//      /**
//        * Match case class without companion object
//        */
//      case cls @ Defn.Class(_, tName, _, ctor, _) =>
//
//        val params = ctor.paramss.flatten
//
//        val clazz = pureClass(cls)
//
//        val (tagPairs, fieldPairs) = generateParams(params)
//
//        val writer = createWriter(tName, tagPairs, fieldPairs)
//
//        val reader = createReader(tName, params)
//
//        q"""
//           $modelImport
//           $sprayImport
//           $implicitImport
//
//           $clazz
//
//           $writer
//
//           $reader
//         """
//
//      /**
//        * Match case class with with an existing companion object
//        */
//      case Term.Block(Seq(cls @ Defn.Class(_, tName, _, ctor, _), Defn.Object(_, _, _))) =>
//
//        val clazz = pureClass(cls)
//
//        val params = ctor.paramss.flatten
//
//        val (tagParams, fieldParams) = generateParams(params)
//
//        val writer = createWriter(tName, tagParams, fieldParams)
//
//        val reader = createReader(tName, params)
//
//        q"""
//           $modelImport
//           $sprayImport
//           $implicitImport
//
//           $clazz
//
//           $writer
//
//           $reader
//         """
//
//      /**
//        * Match all other cases
//        */
//      case _ => abort("Can be applied only for case classes")
//    }
//  }
//}
