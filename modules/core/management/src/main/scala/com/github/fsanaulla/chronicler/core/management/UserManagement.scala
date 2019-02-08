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

  implicit val qb: QueryBuilder[Uri]
  implicit val re: RequestExecutor[F, Req, Resp, Uri]
  implicit val rh: ResponseHandler[F, Resp]
  implicit val fm: FlatMap[F]

  import re.buildRequest

  /***
    * Create new username
    * @param username - Name for new user
    * @param password - Password for new user
    * @return         - Result of execution
    */
  final def createUser(username: String, password: String): F[WriteResult] =
    fm.flatMap(re.execute(createUserQuery(username, password)))(rh.toResult)

  /**
    * Create admin user
    * @param username - admin name
    * @param password - admin password
    * @return         - execution response
    */
  final def createAdmin(username: String, password: String): F[WriteResult] =
    fm.flatMap(re.execute(createAdminQuery(username, password)))(rh.toResult)

  /** Drop user */
  final def dropUser(username: String): F[WriteResult] =
    fm.flatMap(re.execute(dropUserQuery(username)))(rh.toResult)

  /** Set password for user */
  final def setUserPassword(username: String, password: String): F[WriteResult] =
    fm.flatMap(re.execute(setUserPasswordQuery(username, password)))(rh.toResult)

  /** Set user privilege on specified database */
  final def setPrivileges(username: String, dbName: String, privilege: Privilege): F[WriteResult] =
    fm.flatMap(re.execute(setPrivilegesQuery(dbName, username, privilege)))(rh.toResult)


  /** Revoke user privilege on specified datasbase */
  final def revokePrivileges(username: String, dbName: String, privilege: Privilege): F[WriteResult] =
    fm.flatMap(re.execute(revokePrivilegesQuery(dbName, username, privilege)))(rh.toResult)

  /** Grant admin rights */
  final def makeAdmin(username: String): F[WriteResult] =
    fm.flatMap(re.execute(makeAdminQuery(username)))(rh.toResult)

  /** Remove admin rights */
  final def disableAdmin(username: String): F[WriteResult] =
    fm.flatMap(re.execute(disableAdminQuery(username)))(rh.toResult)

  /** Show use lists */
  final def showUsers: F[QueryResult[UserInfo]] =
    fm.flatMap(re.execute(showUsersQuery))(rh.toQueryResult[UserInfo])

  /** Show user privileges */
  final def showUserPrivileges(username: String): F[QueryResult[UserPrivilegesInfo]] =
    fm.flatMap(re.execute(showUserPrivilegesQuery(username)))(rh.toQueryResult[UserPrivilegesInfo])

}
