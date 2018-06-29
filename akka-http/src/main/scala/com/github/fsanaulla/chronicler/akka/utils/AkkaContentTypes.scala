package com.github.fsanaulla.chronicler.akka.utils

import _root_.akka.http.scaladsl.model.{MediaType, MediaTypes}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[akka] object AkkaContentTypes {
  val AppJson: MediaType.WithFixedCharset = MediaTypes.`application/json`
  val OctetStream: MediaType.Binary = MediaTypes.`application/octet-stream`
}
