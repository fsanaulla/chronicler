package com.github.fsanaulla.api

import com.github.fsanaulla.core.api.MeasurementApi
import com.github.fsanaulla.core.model.InfluxCredentials
import com.github.fsanaulla.io.AsyncWriter
import com.softwaremill.sttp.SttpBackend

import scala.concurrent.{ExecutionContext, Future}

private[fsanaulla] class Measurement[A](dbName: String, measurementName: String)
                                       (protected implicit val ex: ExecutionContext,
                                        protected implicit val credentials: InfluxCredentials,
                                        protected implicit val backend: SttpBackend[Future, Nothing])
  extends MeasurementApi[A, String](dbName, measurementName)
    with AsyncWriter