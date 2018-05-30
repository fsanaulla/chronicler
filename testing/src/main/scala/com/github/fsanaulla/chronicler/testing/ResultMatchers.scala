package com.github.fsanaulla.chronicler.testing

import com.github.fsanaulla.core.model.{AuthorizationException, Result}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
object ResultMatchers {

  final val OkResult = Result(200, isSuccess = true)
  final val NoContentResult = Result(204, isSuccess = true)
  final val AuthErrorResult = Result(401, isSuccess = false, Some(new AuthorizationException("unable to parse authentication credentials")))
}
