package com.github.fsanaulla.core.io

import com.github.fsanaulla.core.model.Result
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision

import scala.concurrent.Future

private[fsanaulla] trait WriteOperations[E] {

  def write0(dbName: String,
             entity: E,
             consistency: Consistency,
             precision: Precision,
             retentionPolicy: Option[String]): Future[Result]

}
