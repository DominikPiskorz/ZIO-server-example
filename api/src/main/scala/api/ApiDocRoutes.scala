package api

import io.github.gaelrenoux.tranzactio.doobie.Database
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._

object ApiDocRoutes {
  def forEndpoints(endpoints: List[ZServerEndpoint[Database, Any]]) =
    ZHttp4sServerInterpreter()
      .from(
        SwaggerInterpreter()
          .fromServerEndpoints(endpoints, "ZIO Api Server", "1.0")
      )
      .toRoutes
}
