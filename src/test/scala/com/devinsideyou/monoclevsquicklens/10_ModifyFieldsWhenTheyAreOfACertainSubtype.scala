package com.devinsideyou
package monoclevsquicklens

class `10_ModifyFieldsWhenTheyAreOfACertainSubtype` extends TestSuite {
  case class Zoo(animals: List[Animal])

  sealed abstract class Animal extends Product with Serializable
  case class Dog(age: Int) extends Animal
  case class Cat(ages: List[Int]) extends Animal

  test("test") {
    val input: Zoo =
      Zoo(List(Dog(4), Cat(List(3, 12, 13))))

    val expected: Zoo =
      Zoo(List(Dog(5), Cat(List(4, 12, 13))))

    // Quicklens

    {
      import com.softwaremill.quicklens._

      assert(input, expected) { person =>
        person
          .modifyAll(
            _.animals.each.when[Dog].age,
            _.animals.each.when[Cat].ages.at(0)
          )
          .using(_ + 1)
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
          .lens(_.animals)
          .composeTraversal(Traversal.fromTraverse)
          .composePrism(GenPrism[Animal, Dog])
          .composeLens(GenLens[Dog](_.age))
          .modify(_ + 1)
          .lens(_.animals)
          .composeTraversal(Traversal.fromTraverse)
          .composePrism(GenPrism[Animal, Cat])
          .composeLens(GenLens[Cat](_.ages))
          .composeOptional(index(0))
          .modify(_ + 1)
      }

      assert(input, expected) { person =>
        Seq(
          GenLens[Zoo](_.animals)
            .composeTraversal(Traversal.fromTraverse)
            .composePrism(GenPrism[Animal, Dog])
            .composeLens(GenLens[Dog](_.age)),
          GenLens[Zoo](_.animals)
            .composeTraversal(Traversal.fromTraverse)
            .composePrism(GenPrism[Animal, Cat])
            .composeLens(GenLens[Cat](_.ages))
            .composeOptional(index(0))
        ).map(_.modify(_ + 1)).reduceLeft(_ andThen _)(person)
      }
    }
  }
}
