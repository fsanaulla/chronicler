package com.fsanaulla.utils.constants

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
object Epochs {

  implicit def epoch2val(epoch: Epoch): String = epoch.value

  sealed trait Epoch extends Serializable {
    def value: String
  }

  case object NANOSECONDS extends Epoch {
    override def value: String = "ns"
  }

  case object MICROSECONDS extends Epoch {
    override def value: String = "u"
  }

  case object MILLISECONDS extends Epoch {
    override def value: String = "ms"
  }

  case object SECONDS extends Epoch {
    override def value: String = "s"
  }

  case object MINUTES extends Epoch {
    override def value: String = "m"
  }

  case object HOURS extends Epoch {
    override def value: String = "h"
  }
}
