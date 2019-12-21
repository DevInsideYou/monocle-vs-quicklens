package com.devinsideyou
package monoclevsquicklens

class `07_ModifySpecificSequenceElementsUsingAt` extends TestSuite {
  case class Person(addresses: List[Address])
  case class Address(street: Option[Street])
  case class Street(name: String)

  test("test") {
    forAll { (street1: Option[Street], street2: Option[Street]) =>
      val input: Person =
        Person(
          List(
            Address(street1),
            Address(street2)
          )
        )

      val expected: Person =
        input.copy(
          addresses = input.addresses match {
            case head :: atIndexOne :: tail =>
              val modifiedAtIndexOne =
                atIndexOne.copy(street = atIndexOne.street.map { s =>
                  s.copy(name = s.name.toUpperCase)
                })

              head :: modifiedAtIndexOne :: tail

            case addresses => addresses
          }
        )

      // Quicklens

      {
        import com.softwaremill.quicklens._

        assert(input, expected) { person =>
          person
            .modify(_.addresses.at(1).street.each.name)
            .using(_.toUpperCase)
        }

        an[IndexOutOfBoundsException] should be thrownBy {
          input
            .modify(_.addresses.at(2).street.each.name)
            .using(_.toUpperCase)
        }
      }

      // Monocle

      {
        import cats.implicits._

        import monocle._
        import monocle.function.all._
        import monocle.macros._
        import monocle.macros.syntax.lens._

        assert(input, expected) { person =>
          person
            .lens(_.addresses)
            .composeOptional(index(1))
            .composeLens(GenLens[Address](_.street))
            .composeTraversal(Traversal.fromTraverse)
            .composeLens(GenLens[Street](_.name))
            .modify(_.toUpperCase)
        }

        assert(input, expected = input) { person =>
          person
            .lens(_.addresses)
            .composeOptional(index(2))
            .composeLens(GenLens[Address](_.street))
            .composeTraversal(Traversal.fromTraverse)
            .composeLens(GenLens[Street](_.name))
            .modify(_.toUpperCase)
        }
      }
    }
  }
}
