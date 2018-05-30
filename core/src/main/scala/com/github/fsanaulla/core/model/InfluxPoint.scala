package com.github.fsanaulla.core.model

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 05.08.17
  */
case class InfluxTag(key: String, value: String)

sealed trait InfluxField {
  override def toString: String
}

case class StringField(key: String, value: String) extends InfluxField {
  override def toString: String = key + "=" + "\"" + value + "\""
}

case class IntField(key: String, value: Int) extends InfluxField {
  override def toString: String = key + "=" + value + "i"
}

case class LongField(key: String, value: Long) extends InfluxField {
  override def toString: String = key + "=" + value
}

case class DoubleField(key: String, value: Double) extends InfluxField {
  override def toString: String = key + "=" + value
}

case class BooleanField(key: String, value: Boolean) extends InfluxField {
  override def toString: String = key + "=" + value
}

case class CharField(key: String, value: Char) extends InfluxField {
  override def toString: String = key + "=" + value
}

case class Point(measurement: String,
                 tags: List[InfluxTag] = Nil,
                 fields: List[InfluxField] = Nil,
                 time: Long = -1L) {

  def addTag(key: String, value: String): Point = {
    copy(tags = tags :+ InfluxTag(key, value))
  }

  def addField(key: String, value: String): Point = {
    copy(fields = fields :+ StringField(key, value))
  }

  def addField(key: String, value: Int): Point = {
    copy(fields = fields :+ IntField(key, value))
  }

  def addField(key: String, value: Long): Point = {
    copy(fields = fields :+ LongField(key, value))
  }

  def addField(key: String, value: Double): Point = {
    copy(fields = fields :+ DoubleField(key, value))
  }

  def addField(key: String, value: Boolean): Point = {
    copy(fields = fields :+ BooleanField(key, value))
  }

  def addField(key: String, value: Char): Point = {
    copy(fields = fields :+ CharField(key, value))
  }

  def serialize: String = {
    val sb = StringBuilder.newBuilder

    sb.append(measurement).append(",")
    sb.append(tags.map(tag => tag.key + "=" + tag.value).mkString(","))
    sb.append(" ")
    sb.append(fields.map(_.toString).mkString(" "))

    if (time != -1L) {
      sb.append(" ").append(time)
    }

    sb.toString()
  }
}
