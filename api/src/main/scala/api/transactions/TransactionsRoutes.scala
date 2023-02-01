package api.transactions

import java.util.UUID

import api.TapirUtils._
import errors.ApiError
import transactions.model._
import transactions.repository.{
  TransactionListingSpec,
  TransactionListingSortField
}

import cats.implicits._
import zio.interop.catz._
import io.github.gaelrenoux.tranzactio.doobie.Database
import sttp.model.StatusCode
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.ztapir._
import sttp.tapir.{PublicEndpoint, Schema}
import zio._

class TransactionsRoutes(
    handler: TransactionsHandler
) {
  private val transactionsEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] =
    endpoint.in("transactions")

  implicit lazy val sTransaction: Schema[Transaction] = Schema.derived
  implicit lazy val sWriteRequest: Schema[TransactionWriteRequest] =
    Schema.derived

  private val errors =
    oneOf[ApiError](
      oneOfVariant(
        statusCode(StatusCode.NotFound)
          .and(jsonBody[TransactionsError.NotFound].description("not found"))
      ),
      oneOfDefaultVariant(jsonBody[ApiError.Generic].description("unknown"))
    )

  private val getById: ZServerEndpoint[Database, Any] =
    transactionsEndpoint.get
      .in(path[UUID]("id"))
      .errorOut(errors)
      .out(jsonBody[Transaction])
      .out(statusCode(StatusCode.Ok))
      .zServerLogic { id =>
        handler.get(id)
      }

  private val create: ZServerEndpoint[Database, Any] =
    transactionsEndpoint.put
      .in(jsonBody[TransactionWriteRequest])
      .errorOut(errors)
      .out(jsonBody[Transaction])
      .out(statusCode(StatusCode.Ok))
      .zServerLogic { request =>
        handler.create(request)
      }

  private val update: ZServerEndpoint[Database, Any] =
    transactionsEndpoint.put
      .in(path[UUID]("id"))
      .in(jsonBody[TransactionWriteRequest])
      .errorOut(errors)
      .out(statusCode(StatusCode.Ok))
      .zServerLogic { case (id, request) =>
        handler.update(id, request)
      }

  private val list: ZServerEndpoint[Database, Any] =
    transactionsEndpoint.get
      .in(query[Option[Int]]("limit"))
      .in(query[Option[Int]]("page"))
      .in(query[Option[String]]("sortBy"))
      .in(query[Option[Boolean]]("asc"))
      .errorOut(errors)
      .out(jsonBody[List[Transaction]])
      .out(statusCode(StatusCode.Ok))
      .out(header[Int]("X-limit"))
      .out(header[Int]("X-page"))
      .zServerLogic { case (limit, page, sortByString, asc) =>
        for {
          sortBy <- sortByString
            .map(TransactionListingSortField.fromString)
            .sequence
            .mapError(e => ApiError.Generic(e.getMessage()))
          spec = TransactionListingSpec.fromOptionals(limit, page, sortBy, asc)
          result <- handler.list(spec)
        } yield (result, spec.limit, spec.page)
      }

  private val delete: ZServerEndpoint[Database, Any] =
    transactionsEndpoint.delete
      .in(path[UUID]("id"))
      .errorOut(errors)
      .out(statusCode(StatusCode.Ok))
      .zServerLogic { id =>
        handler.delete(id)
      }

  val endpoints = List(getById, create, update, list, delete)
  val routes = ZHttp4sServerInterpreter().from(endpoints).toRoutes
}

object TransactionsRoutes {
  val layer: URLayer[TransactionsHandler, TransactionsRoutes] =
    ZLayer {
      for {
        handler <- ZIO.service[TransactionsHandler]
      } yield new TransactionsRoutes(handler)
    }
}
