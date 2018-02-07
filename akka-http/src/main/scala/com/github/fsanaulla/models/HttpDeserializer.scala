package com.github.fsanaulla.models

import akka.http.scaladsl.model.RequestEntity
import com.github.fsanaulla.core.model.Deserializer

import scala.annotation.implicitNotFound

/**
  * Trait that define functionality for serializing into HTTP body entity
  *
  * @tparam A - type parameter
  */
@implicitNotFound(
  "No HttpDeserializer found for type ${A}. Try to implement an implicit HttpWriter for this type."
)
private[fsanaulla] trait HttpDeserializer[A] extends Deserializer[A, RequestEntity] {
  override def deserialize(obj: A): RequestEntity
}
