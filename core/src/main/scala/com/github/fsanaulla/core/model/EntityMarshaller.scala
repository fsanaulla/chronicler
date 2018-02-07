package com.github.fsanaulla.core.model

private[fsanaulla] trait EntityMarshaller[E] {
  def write[A](obj: A): E
}
