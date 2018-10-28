/*
 * Copyright 2017-2018 Faiaz Sanaulla
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

import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.github.fsanaulla.chronicler.core.typeclasses.QueryBuilder
import com.softwaremill.sttp.Uri.QueryFragment
import com.softwaremill.sttp.Uri.QueryFragment.KeyValue
import com.softwaremill.sttp._

import scala.annotation.tailrec

private[urlhttp] trait UrlQueryBuilder extends QueryBuilder[Uri] with HasCredentials {

  private[urlhttp] val host: String
  private[urlhttp] val port: Int

  private[chronicler] override def buildQuery(uri: String, queryParams: Map[String, String]): Uri = {
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
