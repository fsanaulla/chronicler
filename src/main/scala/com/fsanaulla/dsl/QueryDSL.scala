package com.fsanaulla.dsl

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 22.08.17
  */
object QueryDSL {

  def select(field: String): SelectStatement = SelectStatement("SELECT " + field)

  sealed trait Statement

  case class SelectStatement(query: String) extends Statement {
    def from(from: String): FromStatement = FromStatement(s"$query FROM $from")
  }

  case class FromStatement(query: String) extends Statement {
    def where(condition: String): ConditionStatement = ConditionStatement(s"$query WHERE $condition")
  }

  case class ConditionStatement(query: String) extends Statement {
    def or(condition: String): ConditionStatement = ConditionStatement(s"$query AND $condition")

    def and(condition: String): ConditionStatement = ConditionStatement(s"$query AND $condition")

    def limit(limit: Int): ConditionStatement = ConditionStatement(query + " LIMIT " + limit)

    def offset(offset: Int): ConditionStatement = ConditionStatement(query + " OFFSET " + offset)
  }
}
