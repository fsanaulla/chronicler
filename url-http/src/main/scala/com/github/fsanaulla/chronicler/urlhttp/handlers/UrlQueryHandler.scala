package com.github.fsanaulla.chronicler.urlhttp.handlers

import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.softwaremill.sttp.Uri.QueryFragment
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue
import com.softwaremill.sttp._

import scala.annotation.tailrec

private[urlhttp] trait UrlQueryHandler extends QueryHandler[Uri] with HasCredentials {

  protected val host: String
  protected val port: Int

  override def buildQuery(uri: String, queryParams: Map[String, String]): Uri = {
    val u = Uri(host = host, port).path(uri)
    val encoding = Uri.QueryFragmentEncoding.All
    val kvLst = queryParams.map {
      case (k, v) => KeyValue(k, v, valueEncoding = encoding)
    }

    @tailrec
    def addQueryParam(u: Uri, lst: Seq[QueryFragment]): Uri = {
      lst match {
        case Nil => u
        case h :: tail => addQueryParam(u.queryFragment(h), tail)
      }
    }

    addQueryParam(u, kvLst.toList)
  }
}
