# FastForward

FastForward is a macro library that automatically implements traits or abstract classes by forwarding to another instance. It currently provides one macro called `forward` that generates an instance of a trait with abstract methods forwarded to a specific instance you pass to the macro.

## Example

For example, say I have a `trait Foo` that I want to implement. If I have an instance of a class `Bar` that contains members of the same name, `forward` will automatically generate the implementations of the abstract `Foo` methods to forward to the methods of the instance of `Bar` you passed.

```scala
import io.methvin.fastforward._

trait Foo {
  def foo: String
  def bar: Long
  val baz: Option[String]
}
class Bar {
  def foo = "foo"
  def bar: Long = 2
  def baz = Some("baz")
}
val bar = new Bar
val impl: Foo = forward[Foo](bar)
```
