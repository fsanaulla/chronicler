package com.github.fsanaulla.chronicler.async.shared

import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import org.asynchttpclient.AsyncHttpClientConfig

import scala.concurrent.Future

abstract class AsyncHttpClient(asyncClientConfig: Option[AsyncHttpClientConfig]) { self: AutoCloseable =>

  private[async] implicit val backend: SttpBackend[Future, Nothing] =
    asyncClientConfig.fold(AsyncHttpClientFutureBackend())(AsyncHttpClientFutureBackend.usingConfig)
  private[async] def close(): Unit = backend.close()
}
