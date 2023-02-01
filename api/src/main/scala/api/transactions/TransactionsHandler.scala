package api.transactions

import java.util.UUID

import errors.ApiError
import transactions.model._
import transactions.repository.{TransactionsRepository, TransactionListingSpec}

import io.github.gaelrenoux.tranzactio.doobie._
import zio._

trait TransactionsHandler {
  def get(id: UUID): ZIO[Database, ApiError, Transaction]

  def create(
      request: TransactionWriteRequest
  ): ZIO[Database, ApiError, Transaction]

  def update(
      id: UUID,
      request: TransactionWriteRequest
  ): ZIO[Database, ApiError, Unit]

  def list(spec: TransactionListingSpec): ZIO[Database, ApiError, List[Transaction]]

  def delete(id: UUID): ZIO[Database, ApiError, Unit]
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

  def update(
      id: UUID,
      request: TransactionWriteRequest
  ): ZIO[Database, ApiError, Unit] =
    dbTransaction("Update transaction") {
      for {
        _ <- ZIO.logInfo(s"Updating transaction $id")
        model = request.toModel(id)
        updated <- repository.update(model)
        _ <-
          if (updated == 1) ZIO.unit
          else ZIO.fail(TransactionsError.NotFound(id))
        _ <- ZIO.logInfo(s"Transaction $id successfully updated")
      } yield ()
    }

  def list(spec: TransactionListingSpec): ZIO[Database, ApiError, List[Transaction]] =
    dbTransaction("List transactions") {
      for {
        _ <- ZIO.logInfo(s"Listing transactions")
        result <- repository.list(spec)
        _ <- ZIO.logInfo(s"Found ${result.length} transactions")
      } yield result
    }

  def delete(id: UUID): ZIO[Database, ApiError, Unit] =
    dbTransaction("Delete transaction") {
      for {
        _ <- ZIO.logInfo(s"Deleting transaction $id")
        deleted <- repository.delete(id)
        _ <-
          if (deleted == 1) ZIO.unit
          else ZIO.fail(TransactionsError.NotFound(id))
        _ <- ZIO.logInfo(s"Deleted transaction $id")
      } yield ()
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
