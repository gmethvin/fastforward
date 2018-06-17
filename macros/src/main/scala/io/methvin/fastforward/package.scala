package io.methvin

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

package object fastforward {

  /**
    * Instantiate a trait (or zero-parameter abstract class) by forwarding to the methods of another object.
    *
    * @param target the object to forward to. Must have methods matching the name and type of [[T]]'s abstract methods.
    * @tparam T the type of the trait or abstract class to implement
    * @return an instance of [[T]] with all abstract methods implemented by forwarding to `target`.
    */
  def forward[T](target: Any): T = macro impl.forward[T]

  private object impl {
    def forward[T](
      c: blackbox.Context
    )(target: c.Expr[Any])(implicit tag: c.universe.WeakTypeTag[T]): c.universe.Tree = {
      import c.universe._
      val tpe = tag.tpe
      val implementedMembers: Seq[Tree] = tpe.members.collect {
        case member if member.isMethod && member.isAbstract =>
          val term = member.asTerm
          val termName = term.name.toTermName
          if (term.isVal) {
            q"override val $termName = $target.$termName"
          } else {
            val paramLists = member.asMethod.paramLists
            val paramDefs = paramLists.map {
              _.map { sym =>
                q"val ${sym.name.toTermName}: ${sym.typeSignature}"
              }
            }
            val paramNames = paramLists.map {
              _.map {
                _.name.toTermName
              }
            }
            q"override def $termName(...$paramDefs) = $target.$termName(...$paramNames)"
          }
      }(collection.breakOut)
      val impl = if (implementedMembers.isEmpty) {
        q"new $tpe { }"
      } else {
        q"new $tpe { ..$implementedMembers }"
      }
      q"$impl: $tpe"
    }
  }
}
