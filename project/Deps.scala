import sbt._

object Deps {
  private val scalaTestVer = "3.2.7"
  private val zioVer = "2.0.6"
  private val catsVer = "2.9.0"
  private val zioCatsVer = "23.0.0.1"
  private val doobieVer = "1.0.0-RC1"
  private val circeVer = "0.14.1"
  private val tapirVer = "1.2.7"
  private val http4sVersion = "0.23.13"
  private val zioLoggingVer = "2.1.8"
  private val pureconfigVer = "0.17.2"

  val test = Seq("org.scalatest" %% "scalatest" % scalaTestVer % Test)

  val circe = Seq(
    "io.circe" %% "circe-core" % circeVer,
    "io.circe" %% "circe-generic" % circeVer,
    "io.circe" %% "circe-parser" % circeVer
  )

  val zio = Seq(
    "dev.zio" %% "zio" % zioVer
  )

  val cats = Seq(
    "org.typelevel" %% "cats-core" % catsVer,
    "dev.zio" %% "zio-interop-cats" % zioCatsVer,
  )

  val doobie = Seq(
      "org.tpolecat" %% "doobie-core" % doobieVer,
      "org.tpolecat" %% "doobie-postgres" % doobieVer,
      "org.tpolecat" %% "doobie-postgres-circe" % doobieVer,
      "org.tpolecat" %% "doobie-hikari" % doobieVer,
      "org.tpolecat" %% "doobie-scalatest" % doobieVer % "test",
      "io.github.gaelrenoux" %% "tranzactio" % "4.1.0"
  )

  val tapir = Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVer,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % tapirVer,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVer,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVer,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion
  )

  val log = Seq(
      "dev.zio" %% "zio-logging" % zioLoggingVer,
      "dev.zio" %% "zio-logging-slf4j-bridge" % zioLoggingVer
  )

  val config = Seq(
    "com.github.pureconfig" %% "pureconfig" % pureconfigVer
  )
}
