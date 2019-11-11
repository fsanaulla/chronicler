# Response handling <a name="resp"></a>
We can divide Response AST into 2 parts:
- `WriteResult`
- `ReadResult[_]`

## `WriteResult`
1. `WriteResult` looks like:
```
case class WriteResult(code: Int,                    // HTTP response code
                       isSuccess: Boolean,           // success status
                       ex: Option[Throwable] = None) // optional exception
```
It's returns when you use write functionality, like (`setPassword`, `makeAdmin`, `createDatabase`).

You can handle it like that:
```
influx.setUserPassword("SomeUser", "newPassword") map {
        case Result(_, _, None) => // succesfully finished operation
        case _ => // handle error
}
```

## `ReadResult`
`ReadResult` returns from Query API. like(`readJs`, `read`, `bulkReadJs`). It's a base interface for:
- `QueryResult[_]`
- `GroupedResult[_]`

`QueryResult` used to represent simply query response, it's looks like:
 ```
 case class QueryResult[T](code: Int,                    // response HTTP code
                           isSuccess: Boolean,           // success status 
                           queryResult: Seq[T] = Nil,    // quered data  
                           ex: Option[Throwable] = None) // optional exception 
 ```
In universal methods like `readJs`, if you expect query result you should handle it like:  

```
// M depend on your backend, it can be Future, Try, etc.
val res: M[Array[JArray]] = db.readJs("SELECT * FROM some_measurement") map {
      case QueryResult(_, _, queryResult, None) => queryResult // if no exсeption exist
      case _ => // handle error
}
```

or in short:

```
db.readJs("SELECT * FROM some_measurement").map(_.queryResult)
```
Otherwise if you expect `GROUP BY` result:

```
// tuple of grouped tags and aggregated result
val res: M[Array[(Array[String], JArray)] = db.readJs("SELECT sum(age) FROM some_measurement GROUP BY sex") map {
      case GroupedResult(_, _, groupedResult, None) => groupedResult // if no exсeption exist
      case _ => // handle error
}
```

or in short:
```
db.readJs("SELECT sum(age) FROM some_measurement GROUP BY sex").map(_.groupedResult)
```

If you will try execute wrong result extraction from ReadResult type. it will throw `UnsupportedOperationException`.

All non-universe methods can be handler as `QueryResult[_]`.