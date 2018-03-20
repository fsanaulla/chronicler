# Write operation 
First of all create database connection. In this situation we choose non type safe connection
```
val db = influx.database("db")
``` 
There are many opportunities to store data. They separated on 2 groups.
First one is non typesafe. You can used from `db` instance. Like below
First one save point in pure [Line Protocol Format](https://docs.influxdata.com/influxdb/v1.3/write_protocols/line_protocol_reference/)
```
// single
db.writeNative("cpu_load_short,host=server02,region=us-west value=0.55 1422568543702900257")
res0: Future[Result]

// bulk
db.bulkWriteNative(Seq("cpu_load_short,host=server02,region=us-west value=0.55 1422568543702900257", "cpu_load_short,host=server03,region=us-west value=0.56 1422568539002900257"))
res1: Future[Result]
```
`writePoint` add possibility to save `Point` into InfluxDB:
```
// single
val p1 = Point("testMeasurement")
            .addTag("name", "Jimbo")
            .addTag("surname", "Bimbo")
            .addField("age", 56)

db.writePoint(p1)
res0: Future[Result]

// bulk
db.bulkWritePoints(Seq(p1, ...))
res0: Future[Result]
```
Another option is to write from file. File format example:
```
test1,host=server02 value=0.67
test1,host=server02,region=us-west value=0.55 1422568543702900257
test1,direction=in,host=server01,region=us-west value=2.0 1422568543702900257
```
every point must be on separate line in Line Protocol format. And then:
```
db.writeFromFile("path/to/your/file")
res0: Future[Result]
```
The same methods able for `InfluxUdpClient`. Like:
```
updInflux.writeNative("cpu_load_short,host=server02,region=us-west value=0.55 1422568543702900257")
res0: Future[Unit]
```
main difference in return type. All udp methods return type is result of [UDP Protocol](https://en.wikipedia.org/wiki/User_Datagram_Protocol) nature

The second group are typesafe operation. To use it create type safe connection:
```
val meas = influx.measurement[FakeEntity]("db", "meas")
```
That one can take any type that have implicit `InfluxWriter`object in the scope, that parse your object to [Line Protocol String](https://docs.influxdata.com/influxdb/v1.3/write_protocols/line_protocol_reference/). For example:
```
import com.github.fsanaulla.annotation._

case class FakeEntity(@tag firstName: String,
                      @tag lastName: String,
                      @field age: Int)
                      
implicit val wr = new InfluxWriter[FakeEntity] {
    def write(fe: FakeEntity): String = {
        // parsing code
    }
}
```
Then you can simply:
```
val fe = FakeEntity("Name", "Surname", 54)

// single
meas.write(fe)
res0: Future[Result]

// bulk
meas.bulkWrite(Seq(fe, ...))
res0: Future[Result]
```
To avoid boilerplate code, and headache. Just use [Macros](docs/macros.md).