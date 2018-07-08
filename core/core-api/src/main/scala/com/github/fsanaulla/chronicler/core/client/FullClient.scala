package com.github.fsanaulla.chronicler.core.client

import com.github.fsanaulla.chronicler.core.model.{HasCredentials, Mappable}

/** Full InfluxDB client that combine management and IO functionality */
trait FullClient[M[_], Req, Resp, Uri, Entity]
  extends IOClient[M, Entity]
    with ManagementClient[M, Req, Resp, Uri, Entity] {
  self: HasCredentials with Mappable[M, Resp] => }
