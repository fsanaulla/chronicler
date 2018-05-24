package com.github.fsanaulla.core.model

/** Mixing mapper to context*/
trait Mappable[M[_], R] {
  def m: Mapper[M, R]
}
