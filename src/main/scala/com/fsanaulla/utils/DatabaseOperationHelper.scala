package com.fsanaulla.utils

import spray.json.{JsArray, JsObject, JsValue}

/**
  * Created by fayaz on 12.07.17.
  */
private[fsanaulla] trait DatabaseOperationHelper {

  def toPoint(measurement: String, serializedEntity: String): String = measurement + "," + serializedEntity

  def toPoints(measurement: String, serializedEntitys: Seq[String]): String = serializedEntitys.map(s => measurement + "," + s).mkString("\n")
}
