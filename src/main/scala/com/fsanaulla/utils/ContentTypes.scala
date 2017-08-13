package com.fsanaulla.utils

import akka.http.scaladsl.model.{MediaType, MediaTypes}

/**
  * Created by fayaz on 12.07.17.
  */
private[fsanaulla] object ContentTypes {
  final val appJson: MediaType.WithFixedCharset = MediaTypes.`application/json`
  final val octetStream: MediaType.Binary = MediaTypes.`application/octet-stream`
}
