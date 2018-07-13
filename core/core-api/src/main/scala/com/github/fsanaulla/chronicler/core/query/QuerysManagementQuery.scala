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

package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait QuerysManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  private[chronicler] final def showQuerysQuery(): U = {
    buildQuery("/query", buildQueryParams("SHOW QUERIES"))
  }

  private[chronicler] final def killQueryQuery(queryId: Int): U = {
    buildQuery("/query", buildQueryParams(s"KILL QUERY $queryId"))
  }
}
