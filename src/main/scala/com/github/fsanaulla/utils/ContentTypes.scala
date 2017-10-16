package com.github.fsanaulla.utils

import akka.http.scaladsl.model.{MediaType, MediaTypes}

/**
  * Created by fayaz on 12.07.17.
  */
private[fsanaulla] object ContentTypes {

  final val AppJson: MediaType.WithFixedCharset = MediaTypes.`application/json`

  final val OctetStream: MediaType.Binary = MediaTypes.`application/octet-stream`
}
