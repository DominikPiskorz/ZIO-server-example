ThisBuild / scalaVersion := "2.13.10"

lazy val hello = (project in file("."))
  .settings(
    name := "Blank",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.7" % Test,
  )