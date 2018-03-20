package com.github.fsanaulla.core.io

import com.github.fsanaulla.core.enums.{Consistency, Precision}
import com.github.fsanaulla.core.model.Result

import scala.concurrent.Future

private[fsanaulla] trait WriteOperations[E] {

  def _write(dbName: String,
             entity: E,
             consistency: Consistency,
             precision: Precision,
             retentionPolicy: Option[String]): Future[Result]

}
