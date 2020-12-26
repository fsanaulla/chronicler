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

package com.github.fsanaulla.chronicler.ahc

import java.net.URLEncoder

import com.github.fsanaulla.chronicler.core.model.InfluxCredentials

package object management {
  implicit class StringRich(val str: String) extends AnyVal {
    def encode: String = URLEncoder.encode(str, "UTF-8")
  }

  def queryTesterAuth(query: String)(credentials: InfluxCredentials): String =
    s"http://localhost:8086/query?u=${credentials.username.encode}&p=${credentials.password.encode}&q=${query.encode}"

  def queryTesterAuth(db: String, query: String)(credentials: InfluxCredentials): String =
    s"http://localhost:8086/query?db=$db&u=${credentials.username.encode}&p=${credentials.password.encode}&q=${query.encode}"

  def queryTester(query: String): String =
    s"http://localhost:8086/query?q=${query.encode}"

  def queryTester(db: String, query: String): String =
    s"http://localhost:8086/query?db=$db&q=${query.encode}"

  def queryTester(path: String, queryPrms: List[(String, String)]): String = {
    val s = queryPrms
      .map { case (k, v) => s"$k=${v.encode}" }
      .mkString("&")

    s"http://localhost:8086$path?$s"
  }
}
