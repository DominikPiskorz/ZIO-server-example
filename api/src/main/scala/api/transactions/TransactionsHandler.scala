package api.transactions

import java.util.UUID

import errors.ApiError
import transactions.model._
import transactions.repository.TransactionsRepository

import io.github.gaelrenoux.tranzactio.doobie._
import zio._

trait TransactionsHandler {
  def get(id: UUID): ZIO[Database, ApiError, Transaction]

  def create(
      request: TransactionWriteRequest
  ): ZIO[Database, ApiError, Transaction]
}

class LiveTransactionsHandler(
    repository: TransactionsRepository,
    generateUUID: () => UUID = UUID.randomUUID
) extends TransactionsHandler {
  import Direction._
  import api.DatabaseTransaction._

  def get(id: UUID): ZIO[Database, ApiError, Transaction] =
    dbTransaction("Get transaction") {
      for {
        _ <- ZIO.logInfo(s"Fetching transaction for id $id")
        result <- repository.get(id)
        transaction <- ZIO.getOrFailWith(TransactionsError.NotFound(id))(result)
        _ <- ZIO.logInfo(s"Found transaction for id $id")
      } yield transaction
    }

  def create(
      request: TransactionWriteRequest
  ): ZIO[Database, ApiError, Transaction] =
    dbTransaction("Create transaction") {
      for {
        _ <- ZIO.logInfo("Creating new transaction")
        model = request.toModel(generateUUID())
        _ <- repository.create(model)
        _ <- ZIO.logInfo("Transaction successfully created")
      } yield model
    }
}

object LiveTransactionsHandler {
  val layer: URLayer[TransactionsRepository, TransactionsHandler] =
    ZLayer {
      for {
        repository <- ZIO.service[TransactionsRepository]
      } yield new LiveTransactionsHandler(repository)
    }
}
