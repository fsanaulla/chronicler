package com.github.fsanaulla.chronicler.core.model

trait Serializable[To] {

  /**
    * Auotmatically serialize entity of type ${From} to ${To}
    * @param f     - entity
    * @param sr    - serializer
    * @tparam From - frp, which type
    * @return      - serialized entity
    */
  implicit def ser[From](f: From)(implicit sr: Serializer[From, To]): To = sr.serialize(f)
}
