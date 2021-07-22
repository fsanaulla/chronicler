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

package com.github.fsanaulla.chronicler.akka

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

package object query {

  def urlBase(url: String): Uri =
    Uri.from(
      "http",
      host = "localhost",
      port = 8086,
      path = url
    )

  def queryTesterAuth(query: String)(credentials: InfluxCredentials): Uri =
    urlBase("/query").withQuery(
      Uri.Query("u" -> credentials.username, "p" -> credentials.username, "q" -> query)
    )

  def queryTesterAuth(db: String, query: String)(credentials: InfluxCredentials): Uri =
    urlBase("/query").withQuery(
      Uri.Query("db" -> db, "u" -> credentials.username, "p" -> credentials.password, "q" -> query)
    )

  def queryTester(query: String): Uri =
    urlBase("/query").withQuery(Uri.Query("q" -> query))

  def queryTester(db: String, query: String): Uri =
    urlBase("/query").withQuery(Uri.Query("db" -> db, "q" -> query))

  def writeTester(mp: Map[String, String]): Uri = urlBase("/write").withQuery(Uri.Query(mp))
}
