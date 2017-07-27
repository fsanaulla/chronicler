package com.fsanaulla.unit.management.data

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import com.fsanaulla.Helper._
import com.fsanaulla.model.DatabaseInfo
import com.fsanaulla.utils.ContentTypes.appJson
import com.fsanaulla.utils.DataManagementHelper._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.ExecutionContext

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 27.07.17
  */
class DataManagementHelperSpec
  extends FlatSpec
    with Matchers
    with BeforeAndAfterAll {

  implicit val actorSystem: ActorSystem = ActorSystem("TestActorSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ex: ExecutionContext = actorSystem.dispatcher

  override def afterAll(): Unit = {
    actorSystem.terminate()
  }

  val dbJson = """{
                   "results": [
                     {
                       "statement_id": 0,
                       "series": [
                         {
                           "name": "databases",
                           "columns": [
                             "name"
                           ],
                           "values": [
                             [
                               "_internal"
                             ],
                             [
                               "test"
                             ],
                             [
                               "my_test_db"
                             ],
                             [
                               "mydb"
                             ],
                             [
                               "db"
                             ]
                           ]
                         }
                       ]
                     }
                   ]
                 }"""

  val dbResponse = HttpResponse(entity = HttpEntity(appJson, dbJson))

  "toDatabaseInfo" should "return info about databases" in {
    await(toDatabaseInfo(dbResponse)) shouldEqual Seq(
      DatabaseInfo("test"),
      DatabaseInfo("my_test_db"),
      DatabaseInfo("mydb"),
      DatabaseInfo("db")
    )
  }
}
