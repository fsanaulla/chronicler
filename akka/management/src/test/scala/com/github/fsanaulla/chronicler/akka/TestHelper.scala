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

package com.github.fsanaulla.chronicler.akka

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.core.model._

/**
  * Created by fayaz on 11.07.17.
  */
object TestHelper {

  def queryTesterAuth(query: String)(credentials: InfluxCredentials): Uri =
    Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.username, "u" -> credentials.username))

  def queryTesterAuth(db: String, query: String)(credentials: InfluxCredentials): Uri =
    Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.password, "db" -> db, "u" -> credentials.username))

  def queryTester(query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query))

  def queryTester(db: String, query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query, "db" -> db))

  def writeTester(mp: Map[String, String]): Uri = Uri("/write").withQuery(Uri.Query(mp))

  def queryTesterSimple(query: Map[String, String]): Uri = Uri("/query").withQuery(Uri.Query(query))
}