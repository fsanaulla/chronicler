package com.github.fsanaulla.chronicler.testing.it

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

/**
 * It's base testing style that will be forced on the whole project
 */
trait BaseSpec extends AnyFreeSpec with Matchers
