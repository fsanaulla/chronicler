package com.fsanaulla.api

import com.fsanaulla.model.InfluxCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 16.08.17
  */
private[fsanaulla] trait HasCredentials {
  implicit val credentials: InfluxCredentials
}
