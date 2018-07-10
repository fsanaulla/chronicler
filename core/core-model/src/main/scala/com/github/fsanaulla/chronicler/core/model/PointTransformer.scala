package com.github.fsanaulla.chronicler.core.model

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.08.17
  */
private[fsanaulla] trait PointTransformer {

  protected def toPoint(measurement: String, serializedEntity: String): String = {
    measurement + "," + serializedEntity
  }

  protected def toPoints(measurement: String, serializedEntitys: Seq[String]): String = {
    serializedEntitys.map(s => measurement + "," + s).mkString("\n")
  }

}
