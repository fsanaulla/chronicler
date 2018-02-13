package com.github.fsanaulla.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object InfluxFormatterImpl {
  def writer_impl[T: c.WeakTypeTag](c: blackbox.Context): c.universe.Tree = {
    import c.universe._
    val tpe = c.weakTypeOf[T]

    val methods: Seq[c.universe.Tree] = tpe.decls.toList collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        val nm = m.name.toString
        q"$nm=obj.$nm"
    }

    val res = methods.reduceLeft((a, b) => q"$a ++ $b")

    q"""
       new InfluxWriter[$tpe] {
          def write(obj: $tpe): String = {
            $res
          }
       }
      """
  }
}
