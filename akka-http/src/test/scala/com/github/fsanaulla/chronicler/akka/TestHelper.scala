package com.github.fsanaulla.chronicler.akka

import _root_.akka.http.scaladsl.model.Uri
import com.github.fsanaulla.chronicler.core.model._

/**
  * Created by fayaz on 11.07.17.
  */
object TestHelper {

  def queryTesterAuth(query: String)(credentials: InfluxCredentials): Uri =
    Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.username, "u" -> credentials.username))

  def queryTesterAuth(db: String, query: String)(credentials: InfluxCredentials): Uri =
    Uri("/query").withQuery(Uri.Query("q" -> query, "p" -> credentials.password, "db" -> db, "u" -> credentials.username))

  def queryTester(query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query))

  def queryTester(db: String, query: String): Uri = Uri("/query").withQuery(Uri.Query("q" -> query, "db" -> db))

  def writeTester(mp: Map[String, String]): Uri = Uri("/write").withQuery(Uri.Query(mp))

  def queryTesterSimple(query: Map[String, String]): Uri = Uri("/query").withQuery(Uri.Query(query))
}