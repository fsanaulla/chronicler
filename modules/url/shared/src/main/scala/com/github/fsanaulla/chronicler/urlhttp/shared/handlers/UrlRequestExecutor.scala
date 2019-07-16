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

package com.github.fsanaulla.chronicler.urlhttp.shared.handlers

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.components.{JsonHandler, RequestExecutor}
import com.github.fsanaulla.chronicler.core.either._
import com.github.fsanaulla.chronicler.core.jawn.RichJParser
import com.github.fsanaulla.chronicler.urlhttp.shared.Url
import org.typelevel.jawn.ast.{JArray, JParser}
import requests._

import scala.io.Source
import scala.util.Try

private[urlhttp] final class UrlRequestExecutor(ssl: Boolean, jsonHandler: JsonHandler[Response])
  extends RequestExecutor[Try, Response, Url, String] {

  /**
    * Execute uri
    *
    * @param uri - request uri
    * @return    - Return wrapper response
    */
  override def get(uri: Url): Try[Response] = {
    Try {
      requests.get(
        uri.make,
        params = uri.params,
        verifySslCerts = ssl
      )
    }
  }

  override def post(
      uri: Url,
      body: String,
      gzipped: Boolean
    ): Try[Response] = {
    Try {
      requests.post(
        uri.make,
        RequestAuth.Empty,
        uri.params,
        // todo: PR for ssl support
        verifySslCerts = ssl,
        compress = if (gzipped) Compress.Gzip else Compress.None,
        data = RequestBlob.StringRequestBlob(body)
      )
    }
  }

  def getStream(url: Url): Iterator[ErrorOr[Array[JArray]]] = {
    var iterator: Iterator[String] = null

    requests.get.stream(
      url.make,
      params = url.params
    )(onDownload = in => iterator = Source.fromInputStream(in).getLines())

    iterator
      .map(JParser.parseFromStringEither(_))
      .map(_.flatMapRight(jsonHandler.queryResult))
  }
}
