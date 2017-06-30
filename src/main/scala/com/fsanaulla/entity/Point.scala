package com.fsanaulla.entity

/**
  * Created by fayaz on 27.06.17.
  */
case class Point(key: String,
                 timestamp: Long = -1,
                 tags: List[String] = Nil,
                 fields: List[String] = Nil) {

}
