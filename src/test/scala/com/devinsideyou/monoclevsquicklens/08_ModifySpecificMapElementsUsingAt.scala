package com.devinsideyou
package monoclevsquicklens

class `08_ModifySpecificMapElementsUsingAt` extends TestSuite {
  case class Person(name: String, props: Map[String, Property])
  case class Property(value: String)

  test("test") {
    forAll { (name: String, pair1: (String, Property), pair2: (String, Property)) =>
      val input: Person =
        Person(name, Map(pair1, pair2))

      val expected: Person =
        Person(name, Map(pair1, pair2.copy(_2 = Property("45"))))

      // Quicklens

      {
        import com.softwaremill.quicklens._

        assert(input, expected) { person =>
          person
            .modify(_.props.at(pair2._1).value)
            .setTo("45")
        }

        an[NoSuchElementException] should be thrownBy {
          input
            .modify(_.props.at("chances are this key doesn't exist").value)
            .setTo("45")
        }
      }

      // Monocle

      {
        import monocle.function.all._
        import monocle.macros._
        import monocle.macros.syntax.lens._
        import monocle.std._

        assert(input, expected) { person =>
          person
            .lens(_.props)
            .composeLens(at(pair2._1))
            .composePrism(option.some)
            .composeLens(GenLens[Property](_.value))
            .set("45")
        }

        assert(input, expected = input) { person =>
          person
            .lens(_.props)
            .composeLens(at("chances are this key doesn't exist"))
            .composePrism(option.some)
            .composeLens(GenLens[Property](_.value))
            .set("45")
        }
      }
    }
  }
}
