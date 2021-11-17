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

package com.github.fsanaulla.chronicler.core.management

import com.github.fsanaulla.chronicler.core.management._
import com.github.fsanaulla.chronicler.core.management.user.UserManagement
import com.github.fsanaulla.chronicler.core.management.cq.ContinuousQueryManagement
import com.github.fsanaulla.chronicler.core.management.db.DatabaseManagement
import com.github.fsanaulla.chronicler.core.management.query.QueriesManagement
import com.github.fsanaulla.chronicler.core.management.rp.RetentionPolicyManagement
import com.github.fsanaulla.chronicler.core.management.shard.ShardManagement
import com.github.fsanaulla.chronicler.core.management.subscription.SubscriptionManagement

/** Functionality of management client
  *
  * @tparam F
  *   - Container
  * @tparam Resp
  *   - Response type
  * @tparam U
  *   - Uri type
  * @tparam E
  *   - Request entity type
  */
trait ManagementClient[F[_], G[_], Req, U, E, Resp]
    extends SystemManagement[F]
    with DatabaseManagement[F, G, Req, Resp, U, E]
    with UserManagement[F, G, Req, Resp, U, E]
    with QueriesManagement[F, G, Req, Resp, U, E]
    with RetentionPolicyManagement[F, G, Req, Resp, U, E]
    with ContinuousQueryManagement[F, G, Req, Resp, U, E]
    with ShardManagement[F, G, Req, Resp, U, E]
    with SubscriptionManagement[F, G, Req, Resp, U, E]
    with AutoCloseable
