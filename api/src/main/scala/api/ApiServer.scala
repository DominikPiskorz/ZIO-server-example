package api

import cats.syntax.all._
import cats._
import cats.syntax._
import io.github.gaelrenoux.tranzactio.doobie.Database
import org.http4s._
import org.http4s.server.Router
import org.http4s.blaze.server.BlazeServerBuilder
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import zio._
import zio.logging.{console => zioConsole, _}

object ApiServer extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> zioConsole(
      logLevel = LogLevel.Info,
      format = LogFormat.colored
    )

  def run =
    server.provide(Environment.live).exitCode

  // Starting the server
  val server: ZIO[RunEnvironment, Throwable, Unit] =
    for {
      executor <- ZIO.executor
      routes <- Routes.allRoutes
      settings <- ZIO.service[ApiSettings]
      _ <- ZIO.logInfo(s"Starting api server on port: ${settings.port}")
      res <- serve(executor, routes, settings)
    } yield res

  def serve(
      executor: Executor,
      routes: HttpRoutes[RIO[Database, *]],
      settings: ApiSettings
  ) =
    BlazeServerBuilder[RIO[Database, *]]
      .withExecutionContext(executor.asExecutionContext)
      .bindHttp(settings.port, settings.host)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .serve
      .compile
      .drain
}
