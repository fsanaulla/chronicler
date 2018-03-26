package com.github.fsanaulla.chronicler.akka.utils

import _root_.akka.http.scaladsl.model.{MediaType, MediaTypes}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 15.03.18
  */
private[fsanaulla] object AkkaContentTypes {

  final val AppJson: MediaType.WithFixedCharset = MediaTypes.`application/json`

  final val OctetStream: MediaType.Binary = MediaTypes.`application/octet-stream`
}
