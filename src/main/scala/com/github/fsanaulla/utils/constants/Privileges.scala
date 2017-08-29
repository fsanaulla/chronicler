package com.github.fsanaulla.utils.constants

object Privileges {

  implicit def privilege2value(priv: Privilege): String = priv.value

  sealed trait Privilege {
    def value: String
  }

  case object READ extends Privilege {
    override def value: String = "READ"
  }

  case object WRITE extends Privilege {
    override def value: String = "WRITE"
  }

  case object ALL extends Privilege {
    override def value: String = "ALL"
  }

  case object NO_PRIVILEGES extends Privilege {
    override def value: String = "NO PRIVILEGES"
  }
}
