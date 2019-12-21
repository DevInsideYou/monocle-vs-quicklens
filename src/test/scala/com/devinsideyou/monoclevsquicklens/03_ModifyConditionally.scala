package com.devinsideyou
package monoclevsquicklens

class `03_ModifyConditionally` extends TestSuite {
  case class Person(address: Address, age: Int)
  case class Address(street: Street)
  case class Street(name: String)

  test("test") {
    forAll { input: Person =>
      val expected: Person = // format: OFF
        input.copy(
          address = input.address.copy(
            street = input.address.street.copy(
              name = "3 OO Ln."
            )
          )
        ) // format: ON

      // Quicklens

      {
        import com.softwaremill.quicklens._

        assert(input, expected) { person =>
          person
            .modify(_.address.street.name)
            .setToIfDefined(Some("3 OO Ln."))
        }

        assert(input, expected = input) { person =>
          person
            .modify(_.address.street.name)
            .setToIfDefined(None)
        }

        assert(input, expected) { person =>
          person
            .modify(_.address.street.name)
            .setToIf(true)("3 OO Ln.")
        }

        assert(input, expected = input) { person =>
          person
            .modify(_.address.street.name)
            .setToIf(false)("3 OO Ln.")
        }
      }

      // Monocle

      {
        import monocle.macros.syntax.lens._

        assert(input, expected) { person =>
          person
            .lens(_.address.street.name)
            .setToIfDefined(Some("3 OO Ln."))
        }

        assert(input, expected = input) { person =>
          person
            .lens(_.address.street.name)
            .setToIfDefined(None)
        }

        assert(input, expected) { person =>
          person
            .lens(_.address.street.name)
            .setToIf(true)("3 OO Ln.")
        }

        assert(input, expected = input) { person =>
          person
            .lens(_.address.street.name)
            .setToIf(false)("3 OO Ln.")
        }
      }
    }
  }
}
