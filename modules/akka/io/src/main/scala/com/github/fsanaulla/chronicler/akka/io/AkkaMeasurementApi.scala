/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.akka.io

import akka.stream.scaladsl.Source
import com.github.fsanaulla.chronicler.akka.shared.{
  AkkaQueryBuilder,
  AkkaRequestBuilder,
  AkkaRequestExecutor,
  RequestE,
  ResponseE,
  futureApply
}
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.api.MeasurementApi
import com.github.fsanaulla.chronicler.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.chronicler.core.model.InfluxReader
import com.github.fsanaulla.chronicler.core.typeclasses.{Functor, MonadError}
import sttp.client3.Identity
import sttp.model.Uri

import scala.concurrent.Future
import scala.reflect.ClassTag

final class AkkaMeasurementApi[T: ClassTag](
    dbName: String,
    measurementName: String,
    compress: Boolean
)(
    implicit qb: AkkaQueryBuilder,
    rb: AkkaRequestBuilder,
    re: AkkaRequestExecutor,
    rh: AkkaResponseHandler,
    ME: MonadError[Future, Throwable],
    F: Functor[Future]
) extends MeasurementApi[Future, Id, RequestE[Identity], Uri, String, ResponseE, T](
      dbName,
      measurementName,
      compress
    ) {

  /**
    * Read chunked data, typed
    *
    * @param query     - request SQL query
    * @param epoch     - epoch precision
    * @param pretty    - pretty printed result
    * @param chunkSize - number of elements in the response chunk
    * @return          - streaming response of batched items
    * @since 0.5.4
    */
  def readChunked(
      query: String,
      epoch: Epoch = Epochs.None,
      pretty: Boolean = false,
      chunkSize: Int
  )(implicit rd: InfluxReader[T]): Future[Source[ErrorOr[Array[T]], Any]] = {
    val uri  = chunkedQuery(dbName, query, epoch, pretty, chunkSize)
    val req  = rb.getStream(uri, compress)
    val resp = re.executeStream(req)

    F.map(resp)(rh.queryChunkedResult[T])
  }
}
