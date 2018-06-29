package com.github.fsanaulla.chronicler.core.client

import com.github.fsanaulla.chronicler.core.api.management._
import com.github.fsanaulla.chronicler.core.handlers.{QueryHandler, RequestHandler, ResponseHandler}
import com.github.fsanaulla.chronicler.core.model.{HasCredentials, Mappable, RequestBuilder}

/** Base client trait that combine functional */
private[chronicler] trait InfluxClient[M[_], Req, Resp, Uri, Entity]
    extends RequestHandler[M, Req, Resp]
    with ResponseHandler[M, Resp]
    with QueryHandler[Uri]
    with SystemManagement[M, Entity]
    with DatabaseManagement[M, Req, Resp, Uri, Entity]
    with UserManagement[M, Req, Resp, Uri, Entity]
    with QuerysManagement[M, Req, Resp, Uri, Entity]
    with RetentionPolicyManagement[M, Req, Resp, Uri, Entity]
    with ContinuousQueryManagement[M, Req, Resp, Uri, Entity]
    with ShardManagement[M, Req, Resp, Uri, Entity]
    with SubscriptionManagement[M, Req, Resp, Uri, Entity]
    with AutoCloseable { self: HasCredentials with Mappable[M, Resp] with RequestBuilder[Uri, Req] => }
