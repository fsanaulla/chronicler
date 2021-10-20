package com.github.fsanaulla.chronicler.sync.unit

import com.github.fsanaulla.chronicler.sync.SyncQueryBuilder
import com.github.fsanaulla.chronicler.testing.BaseSpec

class SyncQueryBuilderSpec extends BaseSpec {

  "Uri builder" - {
    val qb = new SyncQueryBuilder("localhost", 8080)

    "should build valid URI" - {

      "without query params" in {
        qb.buildQuery("add/me").toString() shouldEqual "http://localhost:8080/add/me"
      }

      "with query params" in {
        qb.buildQuery("add/me", List("a" -> "1", "b" -> "2"))
          .toString() shouldEqual "http://localhost:8080/add/me?a=1&b=2"
      }
    }
  }
}
