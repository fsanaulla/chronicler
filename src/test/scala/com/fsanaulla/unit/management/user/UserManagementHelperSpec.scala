package com.fsanaulla.unit.management.user

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import com.fsanaulla.Helper._
import com.fsanaulla.model.UserInfo
import com.fsanaulla.model.UserInfo.UserInfoInfluxReader
import com.fsanaulla.utils.ContentTypes.appJson
import com.fsanaulla.utils.UserManagementHelper
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 25.07.17
  */
class UserManagementHelperSpec
  extends FlatSpec
    with Matchers
    with UserManagementHelper
    with BeforeAndAfterAll {

  implicit val actorSystem = ActorSystem("TestActorSystem")
  implicit val mat: ActorMaterializer = ActorMaterializer()

  override def afterAll: Unit = {
    actorSystem.terminate()
  }


  val strJson = """{
       "results":[
          {
             "statement_id":0,
             "series":[
                {
                   "columns":[
                      "user",
                      "admin"
                   ],
                   "values":[
                      [
                         "Martin",
                         false
                      ],
                      [
                        "Jonny",
                        true
                      ]
                   ]
                }
             ]
          }
       ]
    }"""

  val httpResponse = HttpResponse(entity = HttpEntity(appJson, strJson))

  "toShowResult function" should "correclty work" in {
    await(toShowResult(httpResponse)) shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Jonny", isAdmin = true))
  }

}
