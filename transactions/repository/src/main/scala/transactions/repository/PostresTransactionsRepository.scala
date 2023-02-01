package transactions.repository

import java.util.UUID

import errors.ApiError
import transactions.model._

import doobie.{Fragment, LogHandler}
import doobie.implicits._
import doobie.postgres.implicits.{JavaTimeLocalDateTimeMeta, UuidType}
import doobie.util.{Get, Put, Read}
import io.github.gaelrenoux.tranzactio._
import io.github.gaelrenoux.tranzactio.doobie._
import zio._
import java.time.LocalDateTime

class PostresTransactionsRepository extends TransactionsRepository {
  import PostresTransactionsRepository._

  def get(id: UUID): ZIO[Connection, ApiError, Option[Transaction]] =
    tzio {
      sql"""SELECT * FROM transactions WHERE id = ${id}"""
        .queryWithLogHandler[Transaction](LogHandler.jdkLogHandler)
        .option
    }.mapDbError("Get transaction")

  def create(transaction: Transaction): ZIO[Connection, ApiError, Unit] =
    tzio {
      sql"""
      INSERT INTO transactions(
        id, owner, direction, amount, currency, booked_at, title
      ) VALUES (
        ${transaction.id}, ${transaction.owner}, ${transaction.direction},
        ${transaction.amount}, ${transaction.currency},
        ${transaction.bookedAt}, ${transaction.title}
      )
    """.updateWithLogHandler(LogHandler.jdkLogHandler).run
    }.mapDbError("Create transaction").unit

  def update(transaction: Transaction): ZIO[Connection, ApiError, Int] =
    tzio {
      sql"""
      UPDATE transactions SET
        owner = ${transaction.owner},
        direction = ${transaction.direction},
        amount = ${transaction.amount}, 
        currency = ${transaction.currency},
        booked_at = ${transaction.bookedAt},
        title = ${transaction.title}
      WHERE id = ${transaction.id}
    """.updateWithLogHandler(LogHandler.jdkLogHandler).run
    }.mapDbError("Update transaction")

  def delete(id: UUID): ZIO[Connection, ApiError, Int] =
    tzio {
      sql"""DELETE FROM transactions WHERE id = ${id}"""
        .updateWithLogHandler(LogHandler.jdkLogHandler)
        .run
    }.mapDbError("Delete transaction")

  def list(
      spec: TransactionListingSpec
  ): ZIO[Connection, ApiError, List[Transaction]] =
    tzio {
      Fragment
        .const(
          s"""SELECT * FROM transactions
        ORDER BY ${spec.sortBy.toSqlField} ${spec.ascSql}
        LIMIT ${spec.limit} OFFSET ${spec.offset}"""
        )
        .queryWithLogHandler[Transaction](LogHandler.jdkLogHandler)
        .to[List]
    }.mapDbError("List transactions")
}

object PostresTransactionsRepository {
  import Direction._

  val live: ULayer[TransactionsRepository] =
    ZLayer.succeed(new PostresTransactionsRepository())

  implicit class dbOpErrorExt[A](op: ZIO[Connection, DbException, A]) {
    def mapDbError(actionDesc: String): ZIO[Connection, ApiError, A] =
      op.mapError(e => ApiError.Database(e.getCause(), actionDesc))
  }

  implicit val sqlValueToTransactionDirection: Get[Direction] = Get[Int].temap {
    case 0 => Right(Direction.Out)
    case 1 => Right(Direction.In)
    case d => Left(s"Invalid transaction direction in db: $d")
  }

  implicit val TransactionDirectionToSqlValue: Put[Direction] =
    Put[Int].contramap {
      case Direction.Out => 0
      case Direction.In  => 1
    }

  implicit val readTransaction: Read[Transaction] = Read[
    (
        UUID,
        String,
        Direction,
        Long,
        String,
        LocalDateTime,
        Option[String]
    )
  ].map {
    case (
          id,
          owner,
          direction,
          amount,
          currency,
          bookedAt,
          title
        ) =>
      Transaction(
        id,
        owner,
        direction,
        amount,
        currency,
        bookedAt,
        title
      )
  }
}
