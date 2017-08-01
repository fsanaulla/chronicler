package com.fsanaulla.model

import spray.json.JsArray

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
sealed trait InfluxResult {
  val code: Int
  val isSuccess: Boolean
}

case class WriteResult(code: Int, isSuccess: Boolean) extends InfluxResult

case class CreateResult(code: Int, isSuccess: Boolean) extends InfluxResult

case class DeleteResult(code: Int, isSuccess: Boolean) extends InfluxResult

case class UpdateResult(code: Int, isSuccess: Boolean) extends InfluxResult