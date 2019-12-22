package com.devinsideyou
package monoclevsquicklens

class `09_ModifyEitherFieldsUsingEachLeftAndEachRight` extends TestSuite {
  case class Resource(auth: Either[AuthContext, AuthRequest])
  case class AuthContext(token: String)
  case class AuthRequest(url: String)

  test("test") {
    val devResource =
      Resource(auth = Left(AuthContext("fake")))

    val realResource =
      Resource(auth = Left(AuthContext("real")))

    // Quicklens

    {
      import com.softwaremill.quicklens._

      assert(devResource, realResource) { resource =>
        resource
          .modify(_.auth.eachLeft.token)
          .setTo("real")
      }
    }

    // Monocle

    {
      import monocle.macros._
      import monocle.macros.syntax.lens._
      import monocle.std._

      assert(devResource, realResource) { resource =>
        resource
          .lens(_.auth)
          .composePrism(either.stdLeft)
          .composeLens(GenLens[AuthContext](_.token))
          .set("real")
      }
    }
  }
}
