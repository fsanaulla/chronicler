package com.fsanaulla.unit.management.user

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer
import com.fsanaulla.Helper._
import com.fsanaulla.model.UserInfo.UserInfoInfluxReader
import com.fsanaulla.model.UserPrivilegesInfo.UserInfoInfluxReader
import com.fsanaulla.model.{UserInfo, UserPrivilegesInfo}
import com.fsanaulla.utils.ContentTypes.appJson
import com.fsanaulla.utils.UserManagementHelper
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.ExecutionContext

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
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ex: ExecutionContext = actorSystem.dispatcher

  override def afterAll: Unit = {
    actorSystem.terminate()
  }


  val usersJson = """{
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

  val usersResponse = HttpResponse(entity = HttpEntity(appJson, usersJson))

  val privilegesJson = """{
                           "results": [
                             {
                               "statement_id": 0,
                               "series": [
                                 {
                                   "columns": [
                                     "database",
                                     "privilege"
                                   ],
                                   "values": [
                                     [
                                       "mydb",
                                       "READ"
                                     ]
                                   ]
                                 }
                               ]
                             }
                           ]
                         }"""

  val privilegesResponse = HttpResponse(entity = HttpEntity(appJson, privilegesJson))

  "toUserInfo function" should "return list of user info" in {
    await(toUserInfo(usersResponse)) shouldEqual Seq(UserInfo("Martin", isAdmin = false), UserInfo("Jonny", isAdmin = true))
  }

  "toUserPrivilegesInfo function" should "return user privileges" in {
    await(toUserPrivilegesInfo(privilegesResponse)) shouldEqual Seq(UserPrivilegesInfo("mydb", "READ"))
  }
}
