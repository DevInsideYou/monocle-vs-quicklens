ThisBuild / organization := "com.devinsideyou"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.0.1-SNAPSHOT"

import Dependencies._

lazy val `monocle-vs-quicklens` =
  project
    .in(file("."))
    .settings(
      name := "monocle-vs-quicklens",
      addCompilerPlugin(org.typelevel.`kind-projector`),
      libraryDependencies ++= Seq(
        com.github.`julien-truffaut`.`monocle-macro`,
        com.github.`julien-truffaut`.`monocle-unsafe`,
        com.softwaremill.quicklens
      ),
      libraryDependencies ++= Seq(
        com.github.`alexarchambault`.`scalacheck-shapeless_1.14`,
        com.github.`julien-truffaut`.`monocle-law`,
        org.scalacheck.scalacheck,
        org.scalatest.scalatest,
        org.typelevel.kittens
      ).map(_ % Test),
      dependencyOverrides ++= Seq(
        org.scalatest.scalatest
      ),
      scalacOptions ++= Seq(
        "-deprecation",
        "-feature",
        "-language:_",
        "-unchecked",
        // "-Wunused:_",
        // "-Xfatal-warnings",
        "-Ymacro-annotations"
      ),
      Compile / console / scalacOptions --= Seq(
        "-Wunused:_",
        "-Xfatal-warnings"
      ),
      Test / console / scalacOptions :=
        (Compile / console / scalacOptions).value
    )
