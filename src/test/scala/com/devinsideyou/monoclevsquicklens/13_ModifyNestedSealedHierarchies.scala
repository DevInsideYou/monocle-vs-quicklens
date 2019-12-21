package com.devinsideyou
package monoclevsquicklens

class `13_ModifyNestedSealedHierarchies` extends TestSuite {
  sealed abstract class Pet extends Product with Serializable {
    def name: String
  }

  sealed abstract class LeggedPet extends Pet

  case class Cat(name: String) extends LeggedPet
  case class Dog(name: String) extends LeggedPet
  case class Fish(name: String) extends Pet

  val input: List[Pet] =
    List(
      Cat("Catia"),
      Dog("Douglas"),
      Fish("Finn")
    )

  val expected: List[Pet] =
    List(
      Cat("Catia, Jr."),
      Dog("Douglas, Jr."),
      Fish("Finn, Jr.")
    )

  // Quicklens

  {
    import com.softwaremill.quicklens._

    assert(input, expected) { person =>
      person
        .modify(_.each.name)
        .using(_ + ", Jr.")
    }
  }

  // Monocle

  {
    import cats.implicits._

    import monocle._
    import monocle.macros._
    import monocle.macros.syntax.lens._

    assert(input, expected) { person =>
      Traversal
        .fromTraverse[List, Pet]
        .composeLens(
          Lens[Pet, String](_.name) { newName =>
            {
              case pet @ Cat(_)  => pet.lens(_.name).set(newName)
              case pet @ Fish(_) => pet.lens(_.name).set(newName)
              case pet @ Dog(_)  => pet.lens(_.name).set(newName)
            }
          }
        )
        .modify(_ + ", Jr.")(person)
    }

    assert(input, expected) { person =>
      val traversal: Traversal[List[Pet], Pet] =
        Traversal.fromTraverse

      Seq(
        traversal
          .composePrism(GenPrism[Pet, Cat])
          .composeLens(GenLens[Cat](_.name)),
        traversal
          .composePrism(GenPrism[Pet, Dog])
          .composeLens(GenLens[Dog](_.name)),
        traversal
          .composePrism(GenPrism[Pet, Fish])
          .composeLens(GenLens[Fish](_.name))
      ).map(_.modify(_ + ", Jr.")).reduceLeft(_ andThen _)(person)
    }
  }
}
