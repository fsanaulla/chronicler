package com.github.fsanaulla.chronicler.sync.shared

import com.github.fsanaulla.chronicler.core.auth.InfluxCredentials
import com.github.fsanaulla.chronicler.core.components.RequestBuilder
import com.github.fsanaulla.chronicler.core.gzip
import com.github.fsanaulla.chronicler.sync.shared.SyncRequestBuilder.contentEncoding
import sttp.client3.{Empty, Identity, asString, basicRequest, emptyRequest}
import sttp.model.Uri.QuerySegment.KeyValue
import sttp.model.{Header, Uri}

import java.nio.charset.StandardCharsets

final class SyncRequestBuilder(val credentials: Option[InfluxCredentials])
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
}

object SyncRequestBuilder {
  val contentEncoding: Header = Header.unsafeApply("Content-Encoding", "gzip")
}
