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
