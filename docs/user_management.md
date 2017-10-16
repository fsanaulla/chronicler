# User management <a name="userManagement"></a>
In this section described [user management](https://docs.influxdata.com/influxdb/v1.3/query_language/authentication_and_authorization/#user-management-commands) operations

Create non-admin user:
```
influx.createUser("UserName", "UserPassword")
res0: Future[Result]
```
Create admin user:
```
influx.createAdmin("AdminUser", "AdminPass")
res0: Future[Result]
```
Drop user:
```
influx.dropUser("UserName")
res0: Future[Result]
```
Set user password:
```
influx.setUserPassword("UserName", "UserPassword")
res0: Future[Result]
```
Set user privileges for some database:
```
import com.fsanaulla.utils.constants.Privileges._

influx.setPrivileges("SomeUser", "SomeDB", Privileges.READ)
res0: Future[Result]
```
Revoke privileges from user
```
import com.fsanaulla.utils.constants.Privileges._

influx.revokePrivileges("SomeUser", "SomeDB", Privileges.READ)
res0: Future[Result]
```
Make admin user from non-admin user:
```
influx.makeAdmin("NonAdminUser")
res0: Future[Result]
```
Demote admin user:
```
influx.disableAdmin("AdminUser")
res0: Future[Result]
```
Show users:
```
influx.showUsers()
res0: Future[Result]
```
Show user's privileges:
```
influx.showUserPrivileges()
res0: Future[Result]
```