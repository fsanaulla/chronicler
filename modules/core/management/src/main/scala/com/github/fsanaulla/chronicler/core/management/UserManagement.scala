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

import com.github.fsanaulla.chronicler.core.enums.Privilege
import com.github.fsanaulla.chronicler.core.implicits._
import com.github.fsanaulla.chronicler.core.model._
import com.github.fsanaulla.chronicler.core.query.UserManagementQuery
import com.github.fsanaulla.chronicler.core.typeclasses.{FlatMap, QueryBuilder, RequestExecutor, ResponseHandler}

private[fsanaulla] trait UserManagement[F[_], Req, Resp, Uri, Entity] extends UserManagementQuery[Uri] {
  self: RequestExecutor[F, Req, Resp, Uri]
    with ResponseHandler[F, Resp]
    with QueryBuilder[Uri]
    with FlatMap[F]
    with HasCredentials =>

  /***
    * Create new username
    * @param username - Name for new user
    * @param password - Password for new user
    * @return         - Result of execution
    */
  final def createUser(username: String, password: String): F[WriteResult] =
    flatMap(execute(createUserQuery(username, password)))(toResult)

  /**
    * Create admin user
    * @param username - admin name
    * @param password - admin password
    * @return         - execution response
    */
  final def createAdmin(username: String, password: String): F[WriteResult] =
    flatMap(execute(createAdminQuery(username, password)))(toResult)

  /** Drop user */
  final def dropUser(username: String): F[WriteResult] =
    flatMap(execute(dropUserQuery(username)))(toResult)

  /** Set password for user */
  final def setUserPassword(username: String, password: String): F[WriteResult] =
    flatMap(execute(setUserPasswordQuery(username, password)))(toResult)

  /** Set user privilege on specified database */
  final def setPrivileges(username: String, dbName: String, privilege: Privilege): F[WriteResult] =
    flatMap(execute(setPrivilegesQuery(dbName, username, privilege)))(toResult)


  /** Revoke user privilege on specified datasbase */
  final def revokePrivileges(username: String, dbName: String, privilege: Privilege): F[WriteResult] =
    flatMap(execute(revokePrivilegesQuery(dbName, username, privilege)))(toResult)

  /** Grant admin rights */
  final def makeAdmin(username: String): F[WriteResult] =
    flatMap(execute(makeAdminQuery(username)))(toResult)

  /** Remove admin rights */
  final def disableAdmin(username: String): F[WriteResult] =
    flatMap(execute(disableAdminQuery(username)))(toResult)

  /** Show use lists */
  final def showUsers: F[QueryResult[UserInfo]] =
    flatMap(execute(showUsersQuery))(toQueryResult[UserInfo])

  /** Show user privileges */
  final def showUserPrivileges(username: String): F[QueryResult[UserPrivilegesInfo]] =
    flatMap(execute(showUserPrivilegesQuery(username)))(toQueryResult[UserPrivilegesInfo])

}
