package com.fsanaulla

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.fsanaulla.api.DatabaseOperation
import com.fsanaulla.utils.TypeAlias._
import spray.json.JsArray

import scala.concurrent.ExecutionContext

/**
  * Created by fayaz on 04.07.17.
  */
class Database(dbName: String,
               username: Option[String] = None,
               password: Option[String] = None)
              (implicit val actorSystem: ActorSystem,
               override val mat: ActorMaterializer,
               override val ex: ExecutionContext,
               override val connection: Connection)
  extends DatabaseOperation(dbName, username, password)
