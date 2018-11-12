package com.github.fsanaulla.chronicler.testing.it

import com.github.fsanaulla.chronicler.core.model.{AuthorizationException, WriteResult}

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 02.03.18
  */
object ResultMatchers {
  val OkResult = WriteResult(200, isSuccess = true)
  val NoContentResult = WriteResult(204, isSuccess = true)
  val AuthErrorResult = WriteResult(401, isSuccess = false, Some(new AuthorizationException("unable to parse authentication credentials")))
}
