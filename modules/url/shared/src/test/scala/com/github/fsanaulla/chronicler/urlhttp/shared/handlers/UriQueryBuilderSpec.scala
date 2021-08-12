package com.github.fsanaulla.chronicler.urlhttp.shared.handlers

import com.github.fsanaulla.chronicler.urlhttp.shared.UrlQueryBuilder
import com.github.fsanaulla.chronicler.testing.BaseSpec

class UriQueryBuilderSpec extends BaseSpec {

  "Uri builder" - {
    val qb = new UrlQueryBuilder("localhost", 8080, None)

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
