package api

import errors.ApiError

import io.github.gaelrenoux.tranzactio._
import io.github.gaelrenoux.tranzactio.doobie._
import zio._

object DatabaseTransaction {
  implicit class dbOpErrorExt[A](
      op: ZIO[Database, Either[DbException, ApiError], A]
  ) {
    def mapDbError(actionDesc: String): ZIO[Database, ApiError, A] =
      op.mapError {
        case Left(e)  => ApiError.Database(e.getCause(), actionDesc)
        case Right(r) => r
      }
  }

  implicit class errorExt[A](op: ZIO[Database, ApiError, A]) {
    def logDbError(): ZIO[Database, ApiError, A] =
      op.flatMapError {
        case dbError: ApiError.Database =>
          for {
            _ <- ZIO.log(dbError.getMessage)
          } yield dbError
        case e => ZIO.succeed(e)
      }
  }

  def dbTransaction[T](action: String)(
      op: ZIO[Connection, ApiError, T]
  ): ZIO[Database, ApiError, T] =
    Database.transaction(op).mapDbError(action).logDbError()
}
