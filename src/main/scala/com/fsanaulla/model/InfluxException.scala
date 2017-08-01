package com.fsanaulla.model

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
abstract class InfluxException(errMsg: String) extends Exception(errMsg)

class BadRequestException(errMsg: String) extends InfluxException(errMsg)
class ResourceNotFoundException(errMsg: String) extends InfluxException(errMsg)
class InternalServerError(errMsg: String) extends InfluxException(errMsg)
class UnknownException(errMsg: String) extends InfluxException(errMsg)
