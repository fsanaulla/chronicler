package com.fsanaulla.model

import spray.json.JsArray

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 30.07.17
  */
sealed trait InfluxResult {
  val code: Int
}

case class WriteResult(code: Int) extends InfluxResult

case class CreateResult(code: Int) extends InfluxResult

case class DropResult(code: Int) extends InfluxResult

case class UpdateResult(code: Int) extends InfluxResult