package com.devinsideyou
package monoclevsquicklens

class `01_ModifyDeeplyNestedFieldsInCaseClasses` extends TestSuite {
  case class Person(address: Address, age: Int)
  case class Address(street: Street)
  case class Street(name: String)

  test("test") {
    forAll { (input: Person, syntax: Boolean) =>
      val expectedModified: Person = // format: OFF
        input.copy(
          address = input.address.copy(
            street = input.address.street.copy(
              name = input.address.street.name.toUpperCase
            )
          )
        ) // format: ON

      val expectedSet: Person = // format: OFF
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

        assert(input, expectedModified) { person =>
          if (syntax)
            person
              .modify(_.address.street.name)
              .using(_.toUpperCase)
          else
            modify(person)(_.address.street.name)
              .using(_.toUpperCase)
        }

        assert(input, expectedSet) { person =>
          if (syntax)
            person
              .modify(_.address.street.name)
              .setTo("3 OO Ln.")
          else
            modify(person)(_.address.street.name)
              .setTo("3 OO Ln.")
        }

        assert(input, expectedModified) { person =>
          if (syntax) {
            val lens = modify(_: Person)(_.address.street.name)

            lens(person).using(_.toUpperCase)
          } else {
            val lens = modify[Person](_.address.street.name)

            lens.using(_.toUpperCase)(person)
          }
        }

        assert(input, expectedSet) { person =>
          if (syntax) {
            val lens = modify(_: Person)(_.address.street.name)

            lens(person).setTo("3 OO Ln.")
          } else {
            val lens = modify[Person](_.address.street.name)

            lens.setTo("3 OO Ln.")(person)
          }
        }

        assert(input, expectedModified) { person =>
          if (syntax) {
            val address = modify(_: Person)(_.address)
            val streetName = modify(_: Address)(_.street.name)

            val lens = address andThenModify streetName

            lens(person).using(_.toUpperCase)
          } else {
            val address = modify[Person](_.address)
            val streetName = modify[Address](_.street.name)

            val lens = address andThenModify streetName

            lens.using(_.toUpperCase)(person)
          }
        }

        assert(input, expectedSet) { person =>
          if (syntax) {
            val address = modify(_: Person)(_.address)
            val streetName = modify(_: Address)(_.street.name)

            val lens = address andThenModify streetName

            lens(person).setTo("3 OO Ln.")
          } else {
            val address = modify[Person](_.address)
            val streetName = modify[Address](_.street.name)

            val lens = address andThenModify streetName

            lens.setTo("3 OO Ln.")(person)
          }
        }
      }

      // Monocle

      {
        import monocle.macros.syntax.lens._

        assert(input, expectedModified) { person =>
          person
            .lens(_.address.street.name)
            .modify(_.toUpperCase)
        }

        assert(input, expectedSet) { person =>
          person
            .lens(_.address.street.name)
            .set("3 OO Ln.")
        }

        import monocle.macros._

        assert(input, expectedModified) { person =>
          val lens =
            GenLens[Person](_.address.street.name)

          lens
            .modify(_.toUpperCase)(person)
        }

        assert(input, expectedSet) { person =>
          val lens =
            GenLens[Person](_.address.street.name)

          lens
            .set("3 OO Ln.")(person)
        }

        import monocle.macros._

        assert(input, expectedModified) { person =>
          val address = GenLens[Person](_.address)
          val streetName = GenLens[Address](_.street.name)

          val lens = address composeLens streetName

          lens.modify(_.toUpperCase)(person)
        }

        assert(input, expectedSet) { person =>
          val address = GenLens[Person](_.address)
          val streetName = GenLens[Address](_.street.name)

          val lens = address composeLens streetName

          lens.set("3 OO Ln.")(person)
        }
      }
    }
  }
}
