package api

import errors.ApiError

import io.github.gaelrenoux.tranzactio._
import io.github.gaelrenoux.tranzactio.doobie._
import zio._

object DatabaseTransaction {
  implicit class dbOpErrorExt[A](op: ZIO[Database, Either[DbException, ApiError], A]) {
    def mapDbError(actionDesc: String): ZIO[Database, ApiError, A] =
      op.mapError {
          case Left(e) => ApiError.Database(e.getCause(), actionDesc)
          case Right(r) => r
      }
  }

  def dbTransaction[T](action: String)(op: ZIO[Connection, ApiError, T]): ZIO[Database, ApiError, T] =
      Database.transaction(op).mapDbError(action)
}
