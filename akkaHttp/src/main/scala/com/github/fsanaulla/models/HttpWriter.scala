package com.github.fsanaulla.models

import akka.http.scaladsl.model.RequestEntity

import scala.annotation.implicitNotFound

/**
  * Trait that define functionality for serializing into HTTP body entity
 *
  * @tparam A
  */
@implicitNotFound(
  "No HttpWriter found for type ${A}. Try to implement an implicit HttpWriter for this type."
)
trait HttpWriter[A] {
  def write(obj: A): RequestEntity
}
