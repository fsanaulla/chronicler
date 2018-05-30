package com.github.fsanaulla.core.model

/**
  * Entity transformer
  */
private[fsanaulla] trait Deserializer[From, To] {

  /**
    * Transform entity to request enityt
    * @param obj - entity which should be transformed
    * @return    - request entity
    */
  def deserialize(obj: From): To
}
