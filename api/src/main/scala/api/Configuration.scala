package api

import scala.util.Try

import common.database.DbConfig

import pureconfig._
import pureconfig.generic.auto._
import zio._

object Configuration {
  lazy val load: Task[ApiSettings] =
    ZIO.fromTry(
      Try {
        ConfigSource.file("/opt/docker/etc/api.conf").loadOrThrow[ApiSettings]
      })

  val db: TaskLayer[DbConfig] =
    ZLayer.fromZIO(load.map(_.db))

  val server: TaskLayer[ApiSettings] =
    ZLayer.fromZIO(load)

  private def getEnvVar(key: String): String = sys.env.get(key).get
}
