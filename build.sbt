ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .aggregate(api)

lazy val api = (project in file("api"))
  .enablePlugins(DockerPlugin, JavaAppPackaging)
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
  .settings(
    // Docker image:
    // dockerExposedPorts := Seq(80),
    // dockerEntrypoint := Seq(
    //   "authbind", // Run through authbind to allow non-root to bind to :80.
    //   "--deep",
    //   s"bin/${executableScriptName.value}"
    // ),
    // dockerEnvVars += "SCANYE_INVOICE_CONFIG" -> "/opt/docker/etc/api.conf",

    dockerBaseImage := s"amazoncorretto:17",
    dockerExposedPorts := Seq(8080),
    dockerUpdateLatest := true,
    Docker / packageName := s"zio-api",

    // Based on https://github.com/sbt/sbt-native-packager/issues/1427#issuecomment-871995582
    // and https://github.com/sbt/sbt-native-packager/issues/1202#issuecomment-464976550
    Docker / daemonUserUid := None,
    Docker / daemonGroupGid := None,
    Docker / daemonUser := "daemon",

    // Include different config depending on the deployment env.
    // Docker / mappings +=
    //   baseDirectory.value / "conf" / s"${deploymentEnv.value}.conf" -> "/opt/docker/etc/api.conf"
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
