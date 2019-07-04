package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.components.{BodyBuilder, ResponseHandler}
import com.github.fsanaulla.chronicler.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.chronicler.core.model.Functor
import com.github.fsanaulla.chronicler.urlhttp.shared.Url
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor}
import jawn.ast.JArray
import requests.Response

import scala.util.Try

final class UrlDatabaseApi(dbName: String, gzipped: Boolean)
                          (implicit qb: UrlQueryBuilder, bd: BodyBuilder[String],
                           re: UrlRequestExecutor, rh: ResponseHandler[Response],
                           F: Functor[Try])
  extends DatabaseApi[Try, Response, Url, String](dbName, gzipped) {

  def readChunkedJson(query: String,
                      epoch: Epoch = Epochs.None,
                      pretty: Boolean = false,
                      chunkSize: Int = 10000): Iterator[ErrorOr[Array[JArray]]] = {
    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunkSize)
    re.executeStreaming(uri)
  }
}
