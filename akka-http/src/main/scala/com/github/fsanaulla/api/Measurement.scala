package com.github.fsanaulla.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.RequestEntity
import akka.stream.ActorMaterializer
import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.model.InfluxCredentials
import com.github.fsanaulla.io.AkkaWriter
import com.github.fsanaulla.utils.AkkaTypeAlias.Connection

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 03.09.17
  */
private[fsanaulla] class Measurement[E](dbName: String, measurementName: String)
                                       (protected implicit val credentials: InfluxCredentials,
                                        protected implicit val actorSystem: ActorSystem,
                                        protected implicit val mat: ActorMaterializer,
                                        protected implicit val ex: ExecutionContext,
                                        protected implicit val connection: Connection)
    extends MeasurementApi[E, RequestEntity](dbName, measurementName)
      with AkkaWriter
      with AkkaEntityMarshaller
