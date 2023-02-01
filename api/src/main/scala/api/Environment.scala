package api

import api.transactions._
import common.database.DatabaseConnection
import _root_.transactions.repository.PostresTransactionsRepository

import io.github.gaelrenoux.tranzactio.doobie.Database
import zio._

object Environment {

  /** Constructs layer with environment needed to run api server.
    */
  def live: TaskLayer[RunEnvironment] =
    routes ++ Configuration.server ++ database

  val database: TaskLayer[Database] =
    Configuration.db >>> DatabaseConnection.layer

  val generic: ULayer[GenericRoutes] = ZLayer.succeed(GenericRoutes())

  val transactions: TaskLayer[TransactionsRoutes] =
    PostresTransactionsRepository.live >>>
      LiveTransactionsHandler.layer >>>
      TransactionsRoutes.layer

  val routes: TaskLayer[Routes] =
    (generic ++ transactions) >>> Routes.layer
}
