package com.github.fsanaulla.chronicler.core.components

import scala.language.higherKinds

trait RequestExecutor[F[_], Req, Resp] {
  def execute(req: Req): F[Resp]
}
