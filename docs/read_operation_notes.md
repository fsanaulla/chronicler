# Read operations
## Hierarchy
At the top of read operation results stands `ReadResult[_]`. It's separated into 2 types:
- `QueryResult[_]`
- `GroupedResult[_]`

1. `QueryResult[_]` used to represent simply select request without `GROUP BY` clause. For example
`SELECT * FROM db.measurement`. To extract query request result from `ReadResult`, simple execute `.queryResult` on `ReadREsult`.

2. `GroupedResult[_]` used to represent result of query with `GROUP BY` clause. Can  be received by executing `.groupedResult`.

It's support only in single query methods as (`readJs`, `read`).
Bulk methods return `QueryResult` only.

For more details [see](response_handling.md).

## API

At the moment only unsafe method exist. The base one is:
```
db.readJs("SELECT * FROM measurement")
res0: Future[ReadResult[JsArray]] // where JsArray it's influx point representation
```
You can execute multiple query's in one request:
```
db.bulkReadJs(Seq("SELECT * FROM measurement", "SELECT * FROM measurement1"))
res0: Future[ReadResult[Seq[JsArray]]]
```
The next one it's typed method, for using it you need define your own `InfluxReader[T]` and add it implicitly to scope. 
```
case class FakeEntity(firstName: String, lastName: String, age: Int)

implicit val rd = new InfluxReader[FakeEntity] {
    def read(js: JsArray): FakeEntity = {
        // parsing
    }
}
```
And then just use it
```
db.read[FakeEntity]("SELECT * FROM measurement")
res0: Future[ReadResult[FakeEntity]]
```
To avoid boilerplate code, and headache. Just use [Macros](macros.md).
