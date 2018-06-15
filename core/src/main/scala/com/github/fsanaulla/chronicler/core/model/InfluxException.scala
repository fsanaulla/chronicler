package com.github.fsanaulla.chronicler.core.model

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
abstract sealed class InfluxException(errMsg: String) extends Throwable(errMsg)

final class OperationException(errMsg: String) extends InfluxException(errMsg)

final class BadRequestException(errMsg: String) extends InfluxException(errMsg)

final class ResourceNotFoundException(errMsg: String) extends InfluxException(errMsg)

final class AuthorizationException(errMsg: String) extends InfluxException(errMsg)

final class ConnectionException(errMsg: String) extends InfluxException(errMsg)

final class InternalServerError(errMsg: String) extends InfluxException(errMsg)

final class UnknownConnectionException(errMsg: String) extends InfluxException(errMsg)

final class UnknownResponseException(errMsg: String) extends InfluxException(errMsg)

final class DeserializationException(errMsg: String) extends InfluxException(errMsg)
