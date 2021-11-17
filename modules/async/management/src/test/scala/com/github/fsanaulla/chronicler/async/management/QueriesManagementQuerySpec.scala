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

package com.github.fsanaulla.chronicler.async.management

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.github.fsanaulla.chronicler.core.management.query.QueriesManagementQuery
import sttp.model.Uri
import com.github.fsanaulla.chronicler.async.shared.AsyncQueryBuilder

/** Created by Author: fayaz.sanaulla@gmail.com Date: 20.08.17
  */
class QueriesManagementQuerySpec
    extends AnyFlatSpec
    with Matchers
    with QueriesManagementQuery[Uri] {

  implicit val qb = new AsyncQueryBuilder("localhost", 8086)

  it should "show query" in {
    showQuerysQuery.toString shouldEqual queryTester("SHOW QUERIES")
  }

  it should "kill query" in {
    killQueryQuery(5).toString shouldEqual queryTester("KILL QUERY 5")
  }

  it should "show query without auth" in {
    showQuerysQuery.toString shouldEqual queryTester("SHOW QUERIES")
  }

  it should "kill query without auth" in {
    killQueryQuery(5).toString shouldEqual queryTester("KILL QUERY 5")
  }
}
