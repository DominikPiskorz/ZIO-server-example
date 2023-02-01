package api

import api.transactions.TransactionsRoutes

import cats.implicits._
import zio._
import zio.interop.catz._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter

final case class Routes(
    generic: GenericRoutes,
    transactions: TransactionsRoutes
) {
  val allEndpoints =
    generic.endpoints ++
      transactions.endpoints

  val docRoutes = ApiDocRoutes.forEndpoints(allEndpoints)

  val normalRoutes = ZHttp4sServerInterpreter().from(allEndpoints).toRoutes

  val allRoutes = docRoutes <+> normalRoutes
}

object Routes {
  val layer: URLayer[GenericRoutes & TransactionsRoutes, Routes] =
    ZLayer {
      for {
        generic <- ZIO.service[GenericRoutes]
        transactions <- ZIO.service[TransactionsRoutes]
      } yield Routes(generic, transactions)
    }

  val allRoutes =
    ZIO.service[Routes].map(_.allRoutes)
}
