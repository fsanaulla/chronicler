package com.fsanaulla.integration

import com.fsanaulla.InfluxClient
import com.fsanaulla.model.{CreateResult, DeleteResult, RetentionPolicyInfo, UpdateResult}
import com.fsanaulla.utils.InfluxDuration._
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class RetentionPolicyManagerSpec
  extends FlatSpec
    with Matchers
    with DockerTestKit
    with DockerKitSpotify
    with DockerInfluxService {

  implicit val pc = PatienceConfig(Span(20, Seconds), Span(1, Second))

  "Influx container" should "get up and run correctly" in {
    // CHECKING CONTAINER
    isContainerReady(influxdbContainer).futureValue shouldBe true
    influxdbContainer.getPorts().futureValue.get(dockerPort) should not be None
    influxdbContainer.getIpAddresses().futureValue should not be Seq.empty
  }

  "retention policy operation" should "correctly work" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClient(influxdbContainer.getIpAddresses().futureValue.head, dockerPort)

    // CREATING DB TEST
    influx.createDatabase("mydb").futureValue shouldEqual CreateResult(200, isSuccess = true)

    influx.createRetentionPolicy("test", "mydb", 2 hours, 2, Some(2 hours), default = true).futureValue shouldEqual CreateResult(200, isSuccess = true)

    influx.showRetentionPolicies("mydb").futureValue shouldEqual Seq(
      RetentionPolicyInfo("autogen", "0s", "168h0m0s", 1, default = false),
      RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true)
    )

    influx.dropRetentionPolicy("autogen", "mydb").futureValue shouldEqual DeleteResult(200, isSuccess = true)

    influx.showRetentionPolicies("mydb").futureValue shouldEqual Seq(RetentionPolicyInfo("test", "2h0m0s", "2h0m0s", 2, default = true))

    influx.updateRetentionPolicy("test", "mydb", Some(3 hours)).futureValue shouldEqual UpdateResult(200, isSuccess = true)

    influx.showRetentionPolicies("mydb").futureValue shouldEqual Seq(RetentionPolicyInfo("test", "3h0m0s", "2h0m0s", 2, default = true))

    influx.dropRetentionPolicy("test", "mydb").futureValue shouldEqual DeleteResult(200, isSuccess = true)

    influx.showRetentionPolicies("mydb").futureValue shouldEqual Nil
  }
}
