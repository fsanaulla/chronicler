package com.fsanaulla.integration

import com.fsanaulla.model.Writable

/**
  * Created by fayaz on 11.07.17.
  */
object Samples {

  case class FakeEntity(firstName: String, lastName: String, age: Int)

  implicit object WritableFakeEntity extends Writable[FakeEntity] {
    override def write(obj: FakeEntity): String =
      s"firstName=${obj.firstName},lastName=${obj.lastName} age=${obj.age}"
  }

  val singleEntity = FakeEntity("Martin", "Odersky", 58)
}