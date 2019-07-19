package com.github.fsanaulla.chronicler.akka.io

import akka.stream.scaladsl.Source
import com.github.fsanaulla.chronicler.akka.shared.handlers.{
  AkkaQueryBuilder,
  AkkaRequestExecutor,
  AkkaResponseHandler
}
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, JPoint}
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.components.BodyBuilder
import com.github.fsanaulla.chronicler.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.chronicler.core.model.Functor
import com.softwaremill.sttp.{Response, Uri}
import org.typelevel.jawn.ast.JValue

import scala.concurrent.Future

final class AkkaDatabaseApi(
    dbName: String,
    gzipped: Boolean
  )(implicit qb: AkkaQueryBuilder,
    bd: BodyBuilder[String],
    re: AkkaRequestExecutor,
    rh: AkkaResponseHandler,
    F: Functor[Future])
  extends DatabaseApi[Future, Response[JValue], Uri, String](dbName, gzipped) {

  /**
    * Read chunked data
    *
    * @param query     - request SQL query
    * @param epoch     - epoch precision
    * @param pretty    - pretty printed result
    * @param chunkSize - number of elements in the respinse chunk
    * @return          - streaming response of batched points
    * @since 0.5.4
    */
  def readChunkedJson(
      query: String,
      epoch: Epoch = Epochs.None,
      pretty: Boolean = false,
      chunkSize: Int
    ): Future[ErrorOr[Source[ErrorOr[Array[JPoint]], Any]]] = {
    val uri = chunkedQuery(dbName, query, epoch, pretty, chunkSize)
    F.map(re.getStream(uri))(rh.queryChunkedResultJson)
  }
}
