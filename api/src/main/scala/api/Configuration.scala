package api

import common.database.DbConfig

import zio._

object Configuration {
  // TODO use pureconfig or something similar
  lazy val load: Task[ApiSettings] =
    ZIO.succeed(
      ApiSettings(
        host = "0.0.0.0",
        port = 8080,
        db = DbConfig(
          "org.postgresql.Driver",
          "jdbc:postgresql://postgres:5432/db_user",
          "db_user",// getEnvVar("DB_USERNAME"),
          "db_password"// getEnvVar("DB_PASSWORD")
        )
      )
    )

  val db: TaskLayer[DbConfig] =
    ZLayer.fromZIO(load.map(_.db))

  val server: TaskLayer[ApiSettings] =
    ZLayer.fromZIO(load)

  private def getEnvVar(key: String): String = sys.env.get(key).get
}
