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

package com.github.fsanaulla.chronicler.core.management.user

import com.github.fsanaulla.chronicler.core.alias.{ErrorOr, ResponseCode}
import com.github.fsanaulla.chronicler.core.components._
import com.github.fsanaulla.chronicler.core.enums.Privilege
import com.github.fsanaulla.chronicler.core.management.ManagementResponseHandler
import com.github.fsanaulla.chronicler.core.typeclasses.{FunctionK, MonadError}

trait UserManagement[F[_], G[_], Req, Resp, U, E] extends UserManagementQuery[U] {
  implicit val qb: QueryBuilder[U]
  implicit val rb: RequestBuilder[Req, U, E]
  implicit val re: RequestExecutor[F, Req, Resp]
  implicit val rh: ManagementResponseHandler[G, Resp]
  implicit val ME: MonadError[F, Throwable]
  implicit val FK: FunctionK[G, F]

  /** * Create new username
    * @param username
    *   - Name for new user
    * @param password
    *   - Password for new user
    * @return
    *   - Result of execution
    */
  final def createUser(username: String, password: String): F[ErrorOr[ResponseCode]] = {
    val uri  = createUserQuery(username, password)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Create admin user
    * @param username
    *   - admin name
    * @param password
    *   - admin password
    * @return
    *   - execution response
    */
  final def createAdmin(username: String, password: String): F[ErrorOr[ResponseCode]] = {
    val uri  = createAdminQuery(username, password)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Drop user */
  final def dropUser(username: String): F[ErrorOr[ResponseCode]] = {
    val uri  = dropUserQuery(username)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Set password for user */
  final def setUserPassword(username: String, password: String): F[ErrorOr[ResponseCode]] = {
    val uri  = setUserPasswordQuery(username, password)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Set user privilege on specified database */
  final def setPrivileges(
      username: String,
      dbName: String,
      privilege: Privilege
  ): F[ErrorOr[ResponseCode]] = {
    val uri  = setPrivilegesQuery(dbName, username, privilege)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Revoke user privilege on specified database */
  final def revokePrivileges(
      username: String,
      dbName: String,
      privilege: Privilege
  ): F[ErrorOr[ResponseCode]] = {
    val uri  = revokePrivilegesQuery(dbName, username, privilege)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Grant admin rights */
  final def makeAdmin(username: String): F[ErrorOr[ResponseCode]] = {
    val uri  = makeAdminQuery(username)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Remove admin rights */
  final def disableAdmin(username: String): F[ErrorOr[ResponseCode]] = {
    val uri  = disableAdminQuery(username)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.writeResult(resp)))
  }

  /** Show user lists */
  final def showUsers: F[ErrorOr[Array[UserInfo]]] = {
    val uri  = showUsersQuery
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[UserInfo](resp)))
  }

  /** Show user privileges */
  final def showUserPrivileges(username: String): F[ErrorOr[Array[UserPrivilegesInfo]]] = {
    val uri  = showUserPrivilegesQuery(username)
    val req  = rb.get(uri, compress = false)
    val resp = re.execute(req)

    ME.flatMap(resp)(resp => FK(rh.queryResult[UserPrivilegesInfo](resp)))
  }
}
