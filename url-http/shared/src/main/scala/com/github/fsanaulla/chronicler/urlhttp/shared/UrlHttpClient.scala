package com.github.fsanaulla.chronicler.urlhttp.shared

import java.net.HttpURLConnection

import com.github.fsanaulla.chronicler.urlhttp.shared.UrlHttpClient.CustomizationF
import com.softwaremill.sttp.{SttpBackend, TryHttpURLConnectionBackend}

import scala.util.Try

abstract class UrlHttpClient(customization: Option[CustomizationF]) { self: AutoCloseable =>

  private[urlhttp] implicit val backend: SttpBackend[Try, Nothing] =
    customization.fold(TryHttpURLConnectionBackend())(cust => TryHttpURLConnectionBackend(customizeConnection = cust))

  def close(): Unit = backend.close()
}

object UrlHttpClient {
  type CustomizationF = HttpURLConnection => Unit
}
