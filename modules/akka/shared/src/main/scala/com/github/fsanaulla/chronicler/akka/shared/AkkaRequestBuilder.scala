package com.github.fsanaulla.chronicler.akka.shared

import com.github.fsanaulla.chronicler.akka.shared.AkkaRequestBuilder.contentEncoding
import com.github.fsanaulla.chronicler.core.auth.InfluxCredentials
import com.github.fsanaulla.chronicler.core.components.RequestBuilder
import com.github.fsanaulla.chronicler.core.gzip
import sttp.capabilities.akka.AkkaStreams
import sttp.client3.{Empty, Identity, asStreamUnsafe, asString, basicRequest, emptyRequest}
import sttp.model.Uri.QuerySegment.KeyValue
import sttp.model.{Header, Uri}

import java.nio.charset.StandardCharsets

private[akka] final class AkkaRequestBuilder(val credentials: Option[InfluxCredentials])
    extends RequestBuilder[RequestE[Identity], Uri, String] {

  private def embedBasicCredentials(credentials: InfluxCredentials.Basic, uri: Uri) =
    uri
      .addQuerySegment(
        KeyValue("u", credentials.username, valueEncoding = Uri.QuerySegmentEncoding.All)
      )
      .addQuerySegment(
        KeyValue("p", credentials.password, valueEncoding = Uri.QuerySegmentEncoding.All)
      )

  private def embedJwtToken(
      req: RequestE[Empty],
      credentials: InfluxCredentials.Jwt
  ): RequestE[Empty] = req.auth.bearer(credentials.token)

  override def get(
      uri: Uri,
      compress: Boolean
  ): RequestE[Identity] = {
    val baseRequest = if (compress) basicRequest else emptyRequest

    val maybeAuthorized = credentials match {
      case Some(basic: InfluxCredentials.Basic) =>
        val authorizedUri = embedBasicCredentials(basic, uri)
        baseRequest.get(authorizedUri)
      case Some(jwt: InfluxCredentials.Jwt) =>
        embedJwtToken(baseRequest, jwt).get(uri)
      case _ =>
        baseRequest.get(uri)
    }

    maybeAuthorized.response(asString)
  }

  override def post(
      uri: Uri,
      body: String,
      compress: Boolean
  ): RequestE[Identity] = {
    val maybeAuthorized = credentials match {
      case Some(basic: InfluxCredentials.Basic) =>
        val authorizedUri = embedBasicCredentials(basic, uri)
        emptyRequest.post(authorizedUri)
      case Some(jwt: InfluxCredentials.Jwt) =>
        embedJwtToken(emptyRequest, jwt).post(uri)
      case _ =>
        emptyRequest.post(uri)
    }

    val maybeCompressed = {
      val bts = body.getBytes(StandardCharsets.UTF_8)

      if (!compress) maybeAuthorized.body(bts)
      else {
        val (length, entity) = gzip.compress(bts)
        maybeAuthorized.headers(contentEncoding).contentLength(length).body(entity)
      }
    }

    maybeCompressed.response(asString)
  }

  override def post(uri: Uri): RequestE[Identity] = {
    credentials match {
      case Some(basic: InfluxCredentials.Basic) =>
        val authorizedUri = embedBasicCredentials(basic, uri)
        emptyRequest.post(authorizedUri)
      case Some(jwt: InfluxCredentials.Jwt) =>
        embedJwtToken(emptyRequest, jwt).post(uri)
      case _ =>
        emptyRequest.post(uri)
    }
  }

  def getStream(
      uri: Uri,
      compress: Boolean
  ): RequestS[Identity] = {
    val baseRequest = if (compress) basicRequest else emptyRequest

    val maybeAuthorized = credentials match {
      case Some(basic: InfluxCredentials.Basic) =>
        val authorizedUri = embedBasicCredentials(basic, uri)
        baseRequest.get(authorizedUri)
      case Some(jwt: InfluxCredentials.Jwt) =>
        embedJwtToken(baseRequest, jwt).get(uri)
      case _ =>
        baseRequest.get(uri)
    }

    maybeAuthorized.response(asStreamUnsafe(AkkaStreams))
  }
}

object AkkaRequestBuilder {
  val contentEncoding: Header = Header.unsafeApply("Content-Encoding", "gzip")
}
