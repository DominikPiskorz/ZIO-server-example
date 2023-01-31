ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .aggregate(api)

lazy val api = (project in file("api"))
  .dependsOn(
    database,
    transactionsRepository,
    errors
  )
  .settings(
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )
  .settings(
    libraryDependencies ++= 
      Deps.test ++
      Deps.cats ++
      Deps.tapir ++
      Deps.log
  )

lazy val database = (project in file("common/database"))
  .settings(
    libraryDependencies ++= 
      Deps.test ++
      Deps.doobie ++
      Deps.zio
  )

lazy val transactionsModel = (project in file("transactions/model"))
  .settings(
    libraryDependencies ++=
      Deps.test ++
      Deps.circe
  )

lazy val transactionsRepository = (project in file("transactions/repository"))
  .dependsOn(
    transactionsModel,
    errors
  )
  .settings(
    libraryDependencies ++=
      Deps.test ++
      Deps.zio ++
      Deps.doobie
  )

lazy val errors = (project in file("errors"))
  .settings(
    libraryDependencies ++=
      Deps.test ++
      Deps.circe
  )
