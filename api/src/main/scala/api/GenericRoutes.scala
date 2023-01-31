package api

import io.github.gaelrenoux.tranzactio.doobie.Database
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import zio.ZIO

final case class GenericRoutes() {

  private val status: ZServerEndpoint[Any, Any] =
    endpoint
      .in("status")
      .get
      .zServerLogic { id =>
        ZIO.succeed(())
      }

  val endpoints = List(status.widen[Database])
  val routes = ZHttp4sServerInterpreter().from(endpoints).toRoutes
}
