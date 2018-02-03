package com.github.fsanaulla.core.io

import com.github.fsanaulla.core.model.Result
import com.github.fsanaulla.core.utils.constants.Consistencys.Consistency
import com.github.fsanaulla.core.utils.constants.Precisions.Precision

import scala.concurrent.Future

trait WriteOperations[A] {

  protected def write0(dbName: String,
                       entity: A,
                       consistency: Consistency,
                       precision: Precision,
                       retentionPolicy: Option[String]): Future[Result]

}
