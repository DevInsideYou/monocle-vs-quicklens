package com.devinsideyou
package monoclevsquicklens

class `02_ChainModifications` extends TestSuite {
  case class Person(address: Address, age: Int)
  case class Address(street: Street)
  case class Street(name: String)

  test("test") {
    forAll { input: Person =>
      val expectedModified: Person = // format: OFF
        input.copy(
          address = input.address.copy(
            street = input.address.street.copy(
              name = input.address.street.name.toUpperCase
            )
          ),
          age = 70
        ) // format: ON

      val expectedSet: Person = // format: OFF
        input.copy(
          address = input.address.copy(
            street = input.address.street.copy(
              name = "3 OO Ln."
            )
          ),
          age = input.age - 1
        ) // format: ON

      // Quicklens

      {
        import com.softwaremill.quicklens._

        assert(input, expectedModified) { person =>
          person
            .modify(_.address.street.name)
            .using(_.toUpperCase)
            .modify(_.age)
            .setTo(70)
        }

        assert(input, expectedSet) { person =>
          person
            .modify(_.address.street.name)
            .setTo("3 OO Ln.")
            .modify(_.age)
            .using(_ - 1)
        }
      }

      // Monocle

      {
        import monocle.macros.syntax.lens._

        assert(input, expectedModified) { person =>
          person
            .lens(_.address.street.name)
            .modify(_.toUpperCase)
            .lens(_.age)
            .set(70)
        }

        assert(input, expectedSet) { person =>
          person
            .lens(_.address.street.name)
            .set("3 OO Ln.")
            .lens(_.age)
            .modify(_ - 1)
        }
      }
    }
  }
}
