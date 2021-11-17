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

package com.github.fsanaulla.chronicler.sync.management

import com.github.fsanaulla.chronicler.core.enums.Privileges
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.github.fsanaulla.chronicler.core.management.user.UserManagementQuery
import sttp.model.Uri
import com.github.fsanaulla.chronicler.sync.shared.SyncQueryBuilder
import com.github.fsanaulla.chronicler.sync.shared.SyncQueryBuilder

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 21.08.17
  */
class UserManagementQuerySpec extends AnyFlatSpec with Matchers with UserManagementQuery[Uri] {

  private val testUsername  = "TEST_USER_NAME"
  private val testPassword  = "TEST_PASSWORD"
  private val testDatabase  = "TEST_DATABASE"
  private val testPrivilege = Privileges.ALL
  implicit val qb           = new SyncQueryBuilder("localhost", 8086)

  it should "create user query" in {
    createUserQuery(testUsername, testPassword).toString shouldEqual
      queryTester(s"CREATE USER $testUsername WITH PASSWORD '$testPassword'")
  }

  it should "create user query without auth" in {
    createUserQuery(testUsername, testPassword).toString shouldEqual
      queryTester(s"CREATE USER $testUsername WITH PASSWORD '$testPassword'")
  }

  it should "create admin user query" in {
    createAdminQuery(testUsername, testPassword).toString shouldEqual
      queryTester(
        s"CREATE USER $testUsername WITH PASSWORD '$testPassword' WITH ALL PRIVILEGES"
      )

  }

  it should "create admin user query without auth" in {
    createAdminQuery(testUsername, testPassword).toString shouldEqual
      queryTester(s"CREATE USER $testUsername WITH PASSWORD '$testPassword' WITH ALL PRIVILEGES")
  }

  it should "drop user query" in {
    dropUserQuery(testUsername).toString shouldEqual
      queryTester(s"DROP USER $testUsername")
  }

  it should "drop user query without auth" in {
    dropUserQuery(testUsername).toString shouldEqual
      queryTester(s"DROP USER $testUsername")
  }

  it should "show users query" in {
    showUsersQuery.toString shouldEqual queryTester("SHOW USERS")
  }

  it should "show users query without auth" in {
    showUsersQuery.toString shouldEqual queryTester("SHOW USERS")
  }

  it should "show user privileges query" in {
    showUserPrivilegesQuery(testUsername).toString shouldEqual
      queryTester(s"SHOW GRANTS FOR $testUsername")
  }

  it should "show user privileges query without auth" in {
    showUserPrivilegesQuery(testUsername).toString shouldEqual queryTester(
      s"SHOW GRANTS FOR $testUsername"
    )
  }

  it should "make admin query" in {
    makeAdminQuery(testUsername).toString shouldEqual
      queryTester(s"GRANT ALL PRIVILEGES TO $testUsername")
  }

  it should "make admin query without auth" in {
    makeAdminQuery(testUsername).toString shouldEqual queryTester(
      s"GRANT ALL PRIVILEGES TO $testUsername"
    )
  }

  it should "disable admin query" in {
    disableAdminQuery(testUsername).toString shouldEqual
      queryTester(s"REVOKE ALL PRIVILEGES FROM $testUsername")
  }

  it should "disable admin query without auth" in {
    disableAdminQuery(testUsername).toString shouldEqual queryTester(
      s"REVOKE ALL PRIVILEGES FROM $testUsername"
    )
  }

  it should "set user password query" in {
    setUserPasswordQuery(testUsername, testPassword).toString shouldEqual
      queryTester(s"SET PASSWORD FOR $testUsername = '$testPassword'")
  }

  it should "set user password query without auth" in {
    setUserPasswordQuery(testUsername, testPassword).toString shouldEqual
      queryTester(s"SET PASSWORD FOR $testUsername = '$testPassword'")
  }

  it should "set privileges query" in {
    setPrivilegesQuery(testDatabase, testUsername, testPrivilege).toString shouldEqual
      queryTester(s"GRANT $testPrivilege ON $testDatabase TO $testUsername")
  }

  it should "set privileges query without auth" in {
    setPrivilegesQuery(testDatabase, testUsername, testPrivilege).toString shouldEqual
      queryTester(s"GRANT $testPrivilege ON $testDatabase TO $testUsername")
  }

  it should "revoke privileges query" in {
    revokePrivilegesQuery(testDatabase, testUsername, testPrivilege).toString shouldEqual
      queryTester(s"REVOKE $testPrivilege ON $testDatabase FROM $testUsername")
  }

  it should "revoke privileges query without auth" in {
    revokePrivilegesQuery(testDatabase, testUsername, testPrivilege).toString shouldEqual
      queryTester(s"REVOKE $testPrivilege ON $testDatabase FROM $testUsername")
  }
}
