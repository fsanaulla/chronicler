package com.github.fsanaulla.chronicler.core.model

/**
  * Entity transformer
  */
trait Serializer[From, To] {

  /**
    * Transform entity to request enityt
    * @param obj - entity which should be transformed
    * @return    - request entity
    */
  def serialize(obj: From): To
}
