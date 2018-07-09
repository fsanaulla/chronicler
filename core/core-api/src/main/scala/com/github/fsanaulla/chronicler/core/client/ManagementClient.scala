package com.github.fsanaulla.chronicler.core.client

import com.github.fsanaulla.chronicler.core.api.management._
import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, Mappable}

/**
  * Functionality of management client
  * @tparam M      - Container
  * @tparam Req    - Request type
  * @tparam Resp   - Response type
  * @tparam Uri    - Uri type
  * @tparam Entity - Request entity type
  */
trait ManagementClient[M[_], Req, Resp, Uri, Entity]
  extends RequestHandler[M, Req, Resp, Uri]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with SystemManagement[M]
    with DatabaseManagement[M, Req, Resp, Uri, Entity]
    with UserManagement[M, Req, Resp, Uri, Entity]
    with QuerysManagement[M, Req, Resp, Uri, Entity]
    with RetentionPolicyManagement[M, Req, Resp, Uri, Entity]
    with ContinuousQueryManagement[M, Req, Resp, Uri, Entity]
    with ShardManagement[M, Req, Resp, Uri, Entity]
    with SubscriptionManagement[M, Req, Resp, Uri, Entity] { self: HasCredentials with Mappable[M, Resp] => }
