# Read operations
At the moment only unsafe method exist. The base one is:
```
db.readJs("SELEC * FROM measurement")
res0: Future[QueryResult[JsArray]] // where JsArray it's influx point representation
```
You can execute multiple query's in one request:
```
db.bulkReadJs(Seq("SELECT * FROM measurement", "SELECT * FROM measurement1"))
res0: Future[QueryResult[Seq[JsArray]]]
```
The next one it's typed method, for using it you need define your own `InfluxReader[T]` and add it implicitly to scope. There is example of one on that.
Simply mark it with `readable` annotation to generate implicits at compile time
```
@readable
case class FakeEntity(firstName: String, lastName: String, age: Int)
```
And then just use it
```
db.read[FakeEntity]("SELECT * FROM measurement")
res0: Future[QueryResult[FakeEntity]]
```