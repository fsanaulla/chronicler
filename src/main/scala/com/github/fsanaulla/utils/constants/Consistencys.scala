package com.github.fsanaulla.utils.constants

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
object Consistencys {

  implicit def cons2val(cons: Consistency): String = cons.value

  sealed trait Consistency extends Serializable {

    def value: String
  }

  case object ONE extends Consistency {

    override def value: String = "one"
  }

  case object QUORUM extends Consistency {

    override def value: String = "quorum"
  }

  case object ALL extends Consistency {

    override def value: String = "all"
  }

  case object ANY extends Consistency {

    override def value: String = "any"
  }
}
