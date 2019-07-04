package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.api.MeasurementApi
import com.github.fsanaulla.chronicler.core.components.{BodyBuilder, ResponseHandler}
import com.github.fsanaulla.chronicler.core.either
import com.github.fsanaulla.chronicler.core.either._
import com.github.fsanaulla.chronicler.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.chronicler.core.model.{Failable, Functor, InfluxReader}
import com.github.fsanaulla.chronicler.urlhttp.shared.Url
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor}
import requests.Response

import scala.reflect.ClassTag
import scala.util.Try

class UrlMeasurementApi[T: ClassTag](dbName: String,
                                     measurementName: String,
                                     gzipped: Boolean)
                                    (implicit qb: UrlQueryBuilder, bd: BodyBuilder[String],
                                     re: UrlRequestExecutor, rh: ResponseHandler[Response],
                                     F: Functor[Try], FA: Failable[Try])
  extends MeasurementApi[Try, Response, Url, String, T](dbName, measurementName, gzipped) {

  def readChunked(query: String,
                  epoch: Epoch = Epochs.None,
                  pretty: Boolean = false,
                  chunkSize: Int = 10000)(implicit rd: InfluxReader[T]): Iterator[ErrorOr[Array[T]]] = {
    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunkSize)
    re.executeStreaming(uri)
      .map(_.flatMapRight(arr => either.array[Throwable, T](arr.map(rd.read))))
  }
}
