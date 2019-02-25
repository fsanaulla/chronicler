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

package com.github.fsanaulla.chronicler.core.model

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 31.07.17
  */
abstract sealed class InfluxException(msg: String) extends Throwable(msg)

final class OperationException(msg: String) extends InfluxException(msg)

final class BadRequestException(msg: String) extends InfluxException(msg)

final class ResourceNotFoundException(msg: String) extends InfluxException(msg)

final class AuthorizationException(msg: String) extends InfluxException(msg)

final class ConnectionException(msg: String) extends InfluxException(msg)

final class InternalServerError(msg: String) extends InfluxException(msg)

final class UnknownConnectionException(msg: String) extends InfluxException(msg)

final class UnknownResponseException(msg: String) extends InfluxException(msg)

final class DeserializationException(msg: String) extends InfluxException(msg)

final class HeaderNotFoundException(msg: String) extends InfluxException(msg)
