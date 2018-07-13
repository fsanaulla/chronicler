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

package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.enums.Privilege
import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

private[fsanaulla] trait UserManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  private[chronicler] final def showUsersQuery(): U =
    buildQuery("/query", buildQueryParams("SHOW USERS"))


  private[chronicler] final def showUserPrivilegesQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"SHOW GRANTS FOR $username"))


  private[chronicler] final def setUserPasswordQuery(username: String, password: String): U =
    buildQuery("/query", buildQueryParams(s"SET PASSWORD FOR $username = '$password'"))


  private[chronicler] final def createAdminQuery(username: String, password: String): U =
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password' WITH ALL PRIVILEGES"))


  private[chronicler] final def makeAdminQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"GRANT ALL PRIVILEGES TO $username"))


  private[chronicler] final def disableAdminQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"REVOKE ALL PRIVILEGES FROM $username"))


  private[chronicler] final def createUserQuery(username: String, password: String): U =
    buildQuery("/query", buildQueryParams(s"CREATE USER $username WITH PASSWORD '$password'"))


  private[chronicler] final def dropUserQuery(username: String): U =
    buildQuery("/query", buildQueryParams(s"DROP USER $username"))


  private[chronicler] final def setPrivilegesQuery(dbName: String,
                               username: String,
                               privileges: Privilege): U =
    buildQuery("/query", buildQueryParams(s"GRANT $privileges ON $dbName TO $username"))


  private[chronicler] final def revokePrivilegesQuery(dbName: String,
                                  username: String,
                                  privileges: Privilege): U =
    buildQuery("/query", buildQueryParams(s"REVOKE $privileges ON $dbName FROM $username"))

}
