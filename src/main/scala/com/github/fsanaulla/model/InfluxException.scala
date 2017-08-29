package com.github.fsanaulla.model

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
abstract class InfluxException(errMsg: String) extends Throwable(errMsg)

class OperationException(errMsg: String) extends InfluxException(errMsg)
class BadRequestException(errMsg: String) extends InfluxException(errMsg)
class ResourceNotFoundException(errMsg: String) extends InfluxException(errMsg)
class AuthorizationException(errMsg: String) extends InfluxException(errMsg)
class ConnectionException(errMsg: String) extends InfluxException(errMsg)
class InternalServerError(errMsg: String) extends InfluxException(errMsg)
class UnknownConnectionException(errMsg: String) extends InfluxException(errMsg)
class UnknownResponseException(errMsg: String) extends InfluxException(errMsg)
