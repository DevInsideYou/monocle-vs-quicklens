import sbt._

object Dependencies {
  case object com {
    case object github {
      case object alexarchambault {
        val `scalacheck-shapeless_1.14` = "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.3"
      }

      case object `julien-truffaut` {
        val `monocle-core` = dependency("monocle-core")
        val `monocle-generic` = dependency("monocle-generic")
        val `monocle-macro` = dependency("monocle-macro")
        val `monocle-state` = dependency("monocle-state")
        val `monocle-refined` = dependency("monocle-refined")
        val `monocle-unsafe` = dependency("monocle-unsafe")
        val `monocle-law` = dependency("monocle-law")

        def dependency(artifact: String): ModuleID =
          "com.github.julien-truffaut" %% artifact % "2.0.0"
      }
    }

    case object softwaremill {
      val quicklens = "com.softwaremill.quicklens" %% "quicklens" % "1.4.12"
    }
  }

  case object org {
    case object scalacheck {
      val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.3"
    }

    case object scalatest {
      val scalatest = "org.scalatest" %% "scalatest" % "3.0.8"
    }

    case object typelevel {
      val `kind-projector` = "org.typelevel" %% "kind-projector" % "0.10.3"
      val `kittens` = "org.typelevel" %% "kittens" % "2.0.0"
    }
  }
}
