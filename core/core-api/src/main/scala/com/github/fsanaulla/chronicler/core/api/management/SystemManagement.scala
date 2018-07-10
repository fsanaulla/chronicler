package com.github.fsanaulla.chronicler.core.api.management

import com.github.fsanaulla.chronicler.core.model.WriteResult

/**
  * Basic system related management operations
  * @tparam M - Response container type
  */
trait SystemManagement[M[_]] {

  /**
    * Method for checking InfluxDB status
    * @return - Write result with status information
    */
  def ping: M[WriteResult]
}
