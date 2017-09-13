package com.github.fsanaulla.utils.constants

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 29.07.17
  */
object Precisions {

  implicit def prec2val(prec: Precision): String = prec.value

  sealed trait Precision extends Serializable {

    def value: String
  }

  case object NANOSECONDS extends Precision {

    override def value: String = "ns"
  }

  case object MICROSECONDS extends Precision {

    override def value: String = "u"
  }

  case object MILLISECONDS extends Precision {

    override def value: String = "ms"
  }

  case object SECONDS extends Precision {

    override def value: String = "s"
  }

  case object MINUTES extends Precision {

    override def value: String = "m"
  }

  case object HOURS extends Precision {

    override def value: String = "h"
  }
}
