import sbt._

object Dependencies {
  object V{
    val circeGeneric = "0.14.1"

    val fs2 = "3.2.3"
    val cats = "2.7.0"
    val catsEffect = "2.2.0"

    val circeCore = "0.14.1"
    val circeFs2 = "0.14.0"
  }

  object Libraries{
    val catsCore = "org.typelevel" %% "cats-core" % V.cats
    val catsEffect = "org.typelevel" %% "cats-effect" % V.catsEffect
    val fs2Core = "co.fs2" %% "fs2-core" % V.fs2
    val fs2IO   = "co.fs2" %% "fs2-io" % V.fs2

    val circe = "io.circe" %% "circe-core" % V.circeCore
    val circeFs2 = "io.circe" %% "circe-fs2" % V.circeFs2
    val circeGeneric = "io.circe" %% "circe-generic" % V.circeGeneric
  }
}
