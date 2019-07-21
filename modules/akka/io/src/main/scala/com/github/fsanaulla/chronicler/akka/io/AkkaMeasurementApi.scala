package com.github.fsanaulla.chronicler.akka.io

import akka.http.scaladsl.model.{HttpResponse, Uri}
import akka.stream.scaladsl.Source
import com.github.fsanaulla.chronicler.akka.shared.handlers.{
  AkkaQueryBuilder,
  AkkaRequestExecutor,
  AkkaResponseHandler
}
import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.api.MeasurementApi
import com.github.fsanaulla.chronicler.core.components.BodyBuilder
import com.github.fsanaulla.chronicler.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.chronicler.core.model.{Failable, Functor, InfluxReader}

import scala.concurrent.Future
import scala.reflect.ClassTag

final class AkkaMeasurementApi[T: ClassTag](
    dbName: String,
    measurementName: String,
    gzipped: Boolean
  )(implicit qb: AkkaQueryBuilder,
    bd: BodyBuilder[String],
    re: AkkaRequestExecutor,
    rh: AkkaResponseHandler,
    F: Functor[Future],
    FA: Failable[Future])
  extends MeasurementApi[Future, HttpResponse, Uri, String, T](dbName, measurementName, gzipped) {

  /**
    * Read chunked data, typed
    *
    * @param query     - request SQL query
    * @param epoch     - epoch precision
    * @param pretty    - pretty printed result
    * @param chunkSize - number of elements in the respinse chunk
    * @return          - streaming response of batched items
    * @since 0.5.4
    */
  def readChunked(
      query: String,
      epoch: Epoch = Epochs.None,
      pretty: Boolean = false,
      chunkSize: Int
    )(implicit rd: InfluxReader[T]
    ): Future[Source[ErrorOr[Array[T]], Any]] = {
    val uri = chunkedQuery(dbName, query, epoch, pretty, chunkSize)
    F.map(re.getStream(uri))(rh.queryChunkedResult[T])
  }
}
