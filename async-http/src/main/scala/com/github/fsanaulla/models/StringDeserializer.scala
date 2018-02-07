package com.github.fsanaulla.models

import com.github.fsanaulla.core.model.Deserializer

private[fsanaulla] trait StringDeserializer[A] extends Deserializer[A, String] {
  override def deserialize(obj: A): String
}
