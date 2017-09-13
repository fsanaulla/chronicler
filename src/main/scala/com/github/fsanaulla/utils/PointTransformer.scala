package com.github.fsanaulla.utils

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] trait PointTransformer {

  def toPoint(measurement: String, serializedEntity: String): String = {
    measurement + "," + serializedEntity
  }

  def toPoints(measurement: String, serializedEntitys: Seq[String]): String = {
    serializedEntitys.map(s => measurement + "," + s).mkString("\n")
  }

}
