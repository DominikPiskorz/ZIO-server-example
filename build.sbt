ThisBuild / scalaVersion := "2.13.10"

val scalaTestVer = "3.2.7"
val zioVer = "2.0.5"
val catsVer = "2.9.0"
val doobieVer = "1.0.0-RC1"
val circeVer = "0.14.1"

lazy val blank = (project in file("."))
  .settings(
    name := "Blank",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVer % Test,
      "dev.zio" %% "zio" % zioVer,
      "org.typelevel" %% "cats-core" % catsVer,
      "org.tpolecat" %% "doobie-core" % doobieVer,
      "org.tpolecat" %% "doobie-postgres" % doobieVer,
      "org.tpolecat" %% "doobie-scalatest" % doobieVer % "test",
      "io.circe" %% "circe-core" % circeVer,
      "io.circe" %% "circe-generic" % circeVer,
      "io.circe" %% "circe-parser" % circeVer
    )
  )

lazy val api = (project in file("api"))
  .aggregate(blank)
  .dependsOn(blank)
  .settings(
    name := "Api"
  )
