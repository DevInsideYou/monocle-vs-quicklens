package com.devinsideyou
package monoclevsquicklens

class `04_ModifySeveralFieldsInOneGo` extends TestSuite {
  case class Person(firstName: String, middleName: Option[String], lastName: String)

  test("test") {
    forAll { input: Person =>
      val expected: Person =
        input.copy(
          firstName = input.firstName.capitalize,
          middleName = input.middleName.map(_.capitalize),
          lastName = input.lastName.capitalize
        )

      // Quicklens

      {
        import com.softwaremill.quicklens._

        assert(input, expected) { person =>
          person
            .modifyAll(_.firstName, _.middleName.each, _.lastName)
            .using(_.capitalize)
        }
      }

      // Monocle

      {
        import monocle._
        import monocle.macros._
        import monocle.macros.syntax.lens._
        import monocle.std._

        assert(input, expected) { person =>
          person // format: OFF
            .lens(_.firstName).modify(_.capitalize)
            .lens(_.middleName).composePrism(option.some).modify(_.capitalize)
            .lens(_.lastName).modify(_.capitalize) // format: ON
        }

        assert(input, expected) { person =>
          Seq(
            GenLens[Person](_.firstName).asOptional,
            GenLens[Person](_.middleName).composePrism(option.some),
            GenLens[Person](_.lastName).asOptional
          ).map(_.modify(_.capitalize)).reduceLeft(_ andThen _)(person)
        }

        import cats.implicits._

        assert(input, expected) { person =>
          Seq(
            GenLens[Person](_.firstName).asTraversal,
            GenLens[Person](_.middleName).composeTraversal(Traversal.fromTraverse),
            GenLens[Person](_.lastName).asTraversal
          ).map(_.modify(_.capitalize)).reduceLeft(_ andThen _)(person)
        }
      }
    }
  }
}
