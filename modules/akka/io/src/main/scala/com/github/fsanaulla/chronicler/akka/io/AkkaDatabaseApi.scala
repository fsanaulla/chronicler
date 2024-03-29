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
  ResponseE
}
import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, Id}
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.chronicler.core.typeclasses.{FunctionK, Functor, Monad}
import org.typelevel.jawn.ast.JArray
import sttp.client3.Identity
import sttp.model.Uri

import scala.concurrent.Future

final class AkkaDatabaseApi(
    dbName: String,
    compressed: Boolean
)(implicit
    qb: AkkaQueryBuilder,
    rb: AkkaRequestBuilder,
    re: AkkaRequestExecutor,
    rh: AkkaResponseHandler,
    M: Monad[Future],
    F: Functor[Future],
    FK: FunctionK[Id, Future]
) extends DatabaseApi[Future, Id, RequestE[Identity], Uri, String, ResponseE](
      dbName,
      compressed
    ) {

  /** Read chunked data
    *
    * @param query
    *   - request SQL query
    * @param epoch
    *   - epoch precision
    * @param pretty
    *   - pretty printed result
    * @param chunkSize
    *   - number of elements in the respinse chunk
    * @return
    *   - streaming response of batched points
    * @since 0.5.4
    */
  def readChunkedJson(
      query: String,
      epoch: Epoch = Epochs.None,
      pretty: Boolean = false,
      chunkSize: Int
  ): Source[ErrorOr[Array[JArray]], Future[Any]] = {
    val uri  = chunkedQuery(dbName, query, epoch, pretty, chunkSize)
    val req  = rb.getStream(uri, compress = false)
    val resp = re.executeStream(req)

    Source.fromFutureSource(F.map(resp)(resp => rh.queryChunkedResultJson(resp)))
  }
}
