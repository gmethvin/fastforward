package io.methvin.fastforward

import java.util.UUID

class FastForwardSpec extends org.scalatest.WordSpec {
  "forward macro" should {
    "work with a trait and another class with implementations" in {
      trait Foo {
        def foo: String
        def bar: Long
        val baz: Option[String]
      }
      class FooImpl {
        def foo = "foo"
        def bar: Long = 2
        def baz = Some("baz")
      }
      val impl = forward[Foo](new FooImpl)
      assert(impl.foo === "foo")
      assert(impl.bar === 2)
      assert(impl.baz === Some("baz"))
    }
    "work with a trait and another class with method parameters" in {
      trait Foo {
        def foo: String
        def bar: Long
        def baz(n: Int): Option[String]
      }
      class FooImpl {
        def foo = "foo"
        def bar: Long = 2
        def baz(n: Int) = Some(n.toString)
      }
      val impl = forward[Foo](new FooImpl)
      assert(impl.foo === "foo")
      assert(impl.bar === 2)
      assert(impl.baz(10) === Some("10"))
    }
    "work with a trait and a subclass with implementations" in {
      trait Foo {
        def foo: String
        def bar: Long
        val baz: Option[String]
      }
      class FooImpl extends Foo {
        def foo = "foo"
        def bar: Long = 2
        lazy val baz = Some("baz")
      }
      val impl = forward[Foo](new FooImpl)
      assert(impl.foo === "foo")
      assert(impl.bar === 2)
      assert(impl.baz === Some("baz"))
    }
    "work with a trait that has no abstract methods" in {
      trait Foo {
        def foo: String = "foo"
        def bar: Long = 1
        val baz: Option[String] = Some("baz")
      }
      class FooImpl {
        def foo = "foo"
        def bar: Long = 2
        lazy val baz = Some("baz")
      }
      val impl = forward[Foo](new FooImpl)
      assert(impl.foo === "foo")
      assert(impl.bar === 1)
      assert(impl.baz === Some("baz"))
    }
    "work with an inner abstract class" in {
      case class Request(id: UUID = UUID.randomUUID())
      class AppComponents {
        abstract class RequestComponents(val request: Request) {
          def foo: String
          def bar: Long
          def baz: Option[String]
        }
        def requestComponents(request: Request): RequestComponents = {
          abstract class RequestComponentsWithRequest extends RequestComponents(request)
          forward[RequestComponentsWithRequest](this)
        }
        def foo = "foo"
        def bar: Long = 2
        lazy val baz = Some("baz")
      }
      val components = new AppComponents
      val request = Request()
      val requestComponents = components.requestComponents(request)
      assert(requestComponents.request === request)
      assert(requestComponents.foo === "foo")
      assert(requestComponents.bar === 2)
      assert(requestComponents.baz === Some("baz"))
    }
    "work with an inner trait" in {
      case class Request(id: UUID = UUID.randomUUID())
      class AppComponents {
        trait RequestComponents {
          def request: Request
          def foo: String
          def bar: Long
          def baz: Option[String]
        }
        def requestComponents(req: Request): RequestComponents = {
          trait ThisRequestComponents extends RequestComponents { def request: Request = req }
          forward[ThisRequestComponents](this)
        }
        def foo = "foo"
        def bar: Long = 2
        lazy val baz = Some("baz")
      }
      val components = new AppComponents
      val request = Request()
      val requestComponents = components.requestComponents(request)
      assert(requestComponents.request === request)
      assert(requestComponents.foo === "foo")
      assert(requestComponents.bar === 2)
      assert(requestComponents.baz === Some("baz"))
    }
  }
}
