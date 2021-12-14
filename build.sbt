ThisBuild / organization  := "ar.empanada"
ThisBuild / version       := "0.0.1"
ThisBuild / scalaVersion  := "2.13.6"

lazy val root = (project in file("."))
  .settings(
    name := "compareLogs"
  )
