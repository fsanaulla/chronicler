package com.github.fsanaulla.chronicler.akka.utils

import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import com.github.fsanaulla.chronicler.akka.utils.AkkaContentTypes.AppJson

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 10.04.18
  */
object Extensions {

  implicit class RichString(private val str: String) extends AnyVal {
    def toResponse: HttpResponse = {
      HttpResponse(entity = HttpEntity(AppJson, str))
    }
  }

}
