# Subscription management <a name="subsManagement"></a>
You can simply create `subscription`:
```
import com.fsanaulla.utils.constants.Destinations._

influx.createSubscription("subsName", "dbName", "rpName", Destinations.ALL, Seq("host1", "host2"))
res0: Future[Result]
```
To drop subscription:
```
influx.dropSubscription("subsName", "dbName", "rpName")
res0: Future[Result]
```
Show subscriptions:
```
influx.showSubscriptionsInfo()
res0: Future[QueryResult[SubscriptionInfo]]
```
To show database related subscriptions:
```
influx.showSubscriptions("dbName")
res0: Future[QueryResult[Subscription]]
```
There is no update method in subs api, so it's just simulation using drop and create:
```
import com.fsanaulla.utils.constants.Destinations._

influx.updateSubscription("subsName", "dbName", "rpName", Destinations.ALL, Seq("host1", "host2"))
res0: Future[Result]
```