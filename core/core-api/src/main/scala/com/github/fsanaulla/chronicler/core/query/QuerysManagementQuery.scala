package com.github.fsanaulla.chronicler.core.query

import com.github.fsanaulla.chronicler.core.handlers.QueryHandler
import com.github.fsanaulla.chronicler.core.model.HasCredentials

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 19.08.17
  */
private[chronicler] trait QuerysManagementQuery[U] {
  self: QueryHandler[U] with HasCredentials =>

  private[chronicler] final def showQuerysQuery(): U = {
    buildQuery("/query", buildQueryParams("SHOW QUERIES"))
  }

  private[chronicler] final def killQueryQuery(queryId: Int): U = {
    buildQuery("/query", buildQueryParams(s"KILL QUERY $queryId"))
  }
}
