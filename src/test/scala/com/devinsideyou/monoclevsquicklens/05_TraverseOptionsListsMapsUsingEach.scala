package com.devinsideyou
package monoclevsquicklens

class `05_TraverseOptionsListsMapsUsingEach` extends TestSuite {
  case class Person(addresses: List[Address])
  case class Address(street: Option[Street])
  case class Street(name: String)

  test("test") {
    forAll { input: Person =>
      val expected: Person =
        input.copy(
          addresses = input.addresses.map { address =>
            address.copy(
              street = address.street.map { s =>
                s.copy(name = s.name.toUpperCase)
              }
            )
          }
        )

      // Quicklens

      {
        import com.softwaremill.quicklens._

        assert(input, expected) { person =>
          person
            .modify(_.addresses.each.street.each.name)
            .using(_.toUpperCase)
        }
      }

      // Monocle

      {
        import cats.implicits._

        import monocle._
        import monocle.macros._
        import monocle.macros.syntax.lens._
        import monocle.std._

        assert(input, expected) { person =>
          person
            .lens(_.addresses)
            .composeTraversal(Traversal.fromTraverse)
            .composeLens(GenLens[Address](_.street))
            .composePrism(option.some) // or .composeTraversal(Traversal.fromTraverse)
            .composeLens(GenLens[Street](_.name))
            .modify(_.toUpperCase)
        }
      }
    }
  }
}
