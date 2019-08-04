package com.github.fsanaulla.chronicler.urlhttp.shared

import java.net.HttpCookie

import requests.{BaseSession, Compress, RequestAuth}

import scala.collection.mutable

/**
  * Same as requester base session, with small changes in default headers
  */
class ChroniclerSession extends BaseSession {
  def cookies = mutable.Map.empty[String, HttpCookie]

  val headers: Map[String, String] = Map(
    "User-Agent" -> "requests-scala",
//    "Accept-Encoding" -> "gzip, deflate",
    "Connection" -> "keep-alive",
    "Accept"     -> "*/*"
  )

  def auth: RequestAuth.Empty.type = RequestAuth.Empty

  def proxy: Null = null

  def maxRedirects: Int = 5

  def persistCookies = false

  def readTimeout: Int = 10 * 1000

  def connectTimeout: Int = 10 * 1000

  def verifySslCerts: Boolean = true

  def autoDecompress: Boolean = true

  def compress: Compress = Compress.None
}
