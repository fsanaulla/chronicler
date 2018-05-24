package com.github.fsanaulla.core.client

import com.github.fsanaulla.core.api.management._
import com.github.fsanaulla.core.handlers.{
  QueryHandler,
  RequestHandler,
  ResponseHandler
}
import com.github.fsanaulla.core.model.{HasCredentials, Mappable}

/** Base client trait that combine functional */
private[fsanaulla] trait InfluxClient[M[_], R, U, E]
    extends RequestHandler[M, R, U, E]
    with ResponseHandler[M, R]
    with QueryHandler[U]
    with Mappable[M, R]
    with HasCredentials
    with SystemManagement[M, E]
    with DatabaseManagement[M, R, U, E]
    with UserManagement[M, R, U, E]
    with QuerysManagement[M, R, U, E]
    with RetentionPolicyManagement[M, R, U, E]
    with ContinuousQueryManagement[M, R, U, E]
    with ShardManagement[M, R, U, E]
    with SubscriptionManagement[M, R, U, E]
    with AutoCloseable
