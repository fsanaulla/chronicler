package com.github.fsanaulla.chronicler.testing

import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers

/**
 * It's base testing style that will be forced on the whole project
 */
trait BaseSpec extends AnyFreeSpecLike with Matchers
