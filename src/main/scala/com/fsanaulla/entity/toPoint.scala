package com.fsanaulla.entity

/**
  * Created by fayaz on 27.06.17.
  */
trait toPoint[T] {
  def write(obj: T): String
}
