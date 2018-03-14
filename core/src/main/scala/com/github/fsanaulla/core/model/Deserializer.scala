package com.github.fsanaulla.core.model

private[fsanaulla] trait Deserializer[A, E] {
  def deserialize(obj: A): E
}
