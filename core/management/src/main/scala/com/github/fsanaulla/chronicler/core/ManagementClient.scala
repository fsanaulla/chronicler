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

package com.github.fsanaulla.chronicler.core

import com.github.fsanaulla.chronicler.core.management._
import com.github.fsanaulla.chronicler.core.model.HasCredentials
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor, ResponseHandler}

/**
  * Functionality of management client
  *
  * @tparam F      - Container
  * @tparam Req    - Request type
  * @tparam Resp   - Response type
  * @tparam Uri    - Uri type
  * @tparam Entity - Request entity type
  */
trait ManagementClient[F[_], Req, Resp, Uri, Entity]
  extends RequestExecutor[F, Req, Resp, Uri]
    with ResponseHandler[F, Resp]
    with QueryBuilder[Uri]
    with SystemManagement[F]
    with DatabaseManagement[F, Req, Resp, Uri, Entity]
    with UserManagement[F, Req, Resp, Uri, Entity]
    with QueriesManagement[F, Req, Resp, Uri, Entity]
    with RetentionPolicyManagement[F, Req, Resp, Uri, Entity]
    with ContinuousQueryManagement[F, Req, Resp, Uri, Entity]
    with ShardManagement[F, Req, Resp, Uri, Entity]
    with SubscriptionManagement[F, Req, Resp, Uri, Entity]
    with FlatMap[F]
    with HasCredentials
