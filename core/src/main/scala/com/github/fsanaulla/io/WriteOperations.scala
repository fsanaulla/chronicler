package com.github.fsanaulla.io

import com.github.fsanaulla.model.Result
import com.github.fsanaulla.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.utils.constants.Precisions.Precision

import scala.concurrent.Future

private[fsanaulla] trait WriteOperations[A] {

  def write0(dbName: String,
             entity: A,
             consistency: Consistency,
             precision: Precision,
             retentionPolicy: Option[String]): Future[Result]

}
