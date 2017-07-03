package com.fsanaulla.query

/**
  * Created by fayaz on 27.06.17.
  */
trait Querys {

  val host: String
  val port: Int

  def createDBQuery(host: String, port: Int, dbName: String): String = {
    s"$host:$port/query?q=CREATE DATABASE $dbName"
  }

  def writeToDB(dbName: String): String = {
    s"$host:$port/write?db=$dbName"
  }

}
