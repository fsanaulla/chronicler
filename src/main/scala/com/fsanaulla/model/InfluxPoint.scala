package com.fsanaulla.model

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
  override def toString: String = key + "=" + value
}

case class IntField(key: String, value: Int) extends InfluxField {
  override def toString: String = key + "=" + value
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

case class Point(measurement: String, tags: List[InfluxTag] = Nil, fields: List[InfluxField] = Nil, time: Long = -1L) {
  def addTag(key: String, value: String): Point = copy(tags = InfluxTag(key, value) :: tags)

  def addField(key: String, value: String): Point = copy(fields = StringField(key, value) :: fields)
  def addField(key: String, value: Int): Point = copy(fields = IntField(key, value) :: fields)
  def addField(key: String, value: Long): Point = copy(fields = LongField(key, value) :: fields)
  def addField(key: String, value: Double): Point = copy(fields = DoubleField(key, value) :: fields)
  def addField(key: String, value: Boolean): Point = copy(fields = BooleanField(key, value) :: fields)
  def addField(key: String, value: Char): Point = copy(fields = CharField(key, value) :: fields)

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