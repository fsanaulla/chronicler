package com.github.fsanaulla.chronicler.akka

import com.github.fsanaulla.chronicler.testing.BaseSpec
import com.github.fsanaulla.chronicler.core.auth.InfluxCredentials
import com.github.fsanaulla.chronicler.akka.shared.AkkaQueryBuilder
import com.github.fsanaulla.chronicler.akka.shared.AkkaRequestBuilder

class AkkaRequestBuilderSpec extends BaseSpec {
  val qb = new AkkaQueryBuilder("localhost", 8086)

  "Request Builder" - {

    "should build request" - {

      "with" - {

        "base crendetials" in {
          val creds = InfluxCredentials.Basic("username", "password")
          val rb    = new AkkaRequestBuilder(Some(creds))

          rb.get(qb.buildQuery("/query"), false)
            .uri
            .toString() shouldEqual "http://localhost:8086/query?u=username&p=password"

        }

        "token crendentials" in {
          val creds = InfluxCredentials.Jwt("token")
          val rb    = new AkkaRequestBuilder(Some(creds))

          rb.get(qb.buildQuery("/query"), false)
            .header("Authorization") shouldEqual Some("Bearer " + creds.token)
        }

      }

      "without credentials" - {
        val rb  = new AkkaRequestBuilder(None)
        val req = rb.get(qb.buildQuery("/query"), false)

        req.uri.toString() shouldEqual "http://localhost:8086/query"
        req.header("Authorization") shouldEqual None
      }
    }
  }
}
