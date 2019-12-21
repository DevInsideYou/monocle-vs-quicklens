package com.devinsideyou
package monoclevsquicklens

import org.scalatestplus.scalacheck._

class `06_TraverseSelected ElementsUsingEachWhere` extends TestSuite with Checkers {
  case class Person(addresses: List[Address])
  case class Address(street: Option[Street])
  case class Street(name: String)

  test("test") {
    forAll { input: Person =>
      val expected: Person =
        input.copy(
          addresses = input.addresses.map { address =>
            address.copy(
              street = address
                .street
                .collect {
                  case s if s.name.startsWith("1") =>
                    s.copy(name = s.name.toUpperCase)
                }
                .orElse(address.street)
            )
          }
        )

      def filterAddress: Address => Boolean =
        _.street.isDefined

      // Quicklens

      {
        import com.softwaremill.quicklens._

        assert(input, expected) { person =>
          person
            .modify(
              _.addresses
                .eachWhere(filterAddress)
                .street
                .eachWhere(_.name.startsWith("1"))
                .name
            )
            .using(_.toUpperCase)
        }
      }

      // Monocle

      {
        import cats.implicits._

        import monocle._
        import monocle.macros._
        import monocle.macros.syntax.lens._
        import monocle.unsafe._

        assert(input, expected) { person =>
          person
            .lens(_.addresses)
            .composeTraversal(Traversal.fromTraverse)
            .composePrism(UnsafeSelect.unsafeSelect(filterAddress))
            .composeLens(GenLens[Address](_.street))
            .composeTraversal(Traversal.fromTraverse)
            .composePrism(UnsafeSelect.unsafeSelect[Street](_.name.startsWith("1")))
            .composeLens(GenLens[Street](_.name))
            .modify(_.toUpperCase)
        }
      }
    }
  }

  ignore("UnsafeSelect") {
    import cats.implicits._

    import monocle._
    import monocle.unsafe._
    import monocle.law.discipline._

    val traversal: Traversal[List[Int], Int] =
      Traversal
        .fromTraverse[List, Int]
        .composePrism(UnsafeSelect.unsafeSelect[Int](_ % 2 == 0))

    val tests: TraversalTests.RuleSet =
      TraversalTests(traversal)

    tests
      .all
      .properties
      .map(_._2)
      .foreach(check(_))
  }
}
