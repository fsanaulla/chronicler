# Streaming

Response streaming support table. It's covers only IO modules.

Module | Support | Method
------------ | ------------- | ----------
akka | yes | `readChunkedJson`, `readChunked`
ahc | no | none
url | yes | `readChunkedJson`,`readChunked`

## Usage
All streaming api are backend specific.

- akka based return `akka.stream.scaladsl.Source`
- url based return `scala.collection.Iterator`