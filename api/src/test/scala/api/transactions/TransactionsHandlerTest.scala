package api.transactions

import java.time.LocalDateTime
import java.util.UUID
import javax.sql.DataSource

import errors.ApiError
import transactions.model._
import transactions.repository.TransactionsRepository

import io.github.gaelrenoux.tranzactio.doobie._
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import zio._

class TransactionsHandlerTest extends AnyFunSuite with MockFactory {
  val runtime = Runtime.default

  test("Create transaction") {
    val id = UUID.fromString("5b1bd521-c850-4406-ae1c-380bca99466a")
    val request = TransactionWriteRequest(
      "owner",
      Direction.In,
      100,
      "PLN",
      LocalDateTime.of(2023, 2, 1, 0, 0),
      Some("title")
    )
    val model = request.toModel(id)

    val mockRepo = mock[TransactionsRepository]

    (mockRepo.create _).expects(model).returning(ZIO.unit).once()

    val handler = new LiveTransactionsHandler(
      mockRepo,
      () => id
    )

    val result = Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe
        .run(
          handler.create(request).provideLayer(Database.none)
        )
        .getOrThrowFiberFailure()
    }

    assert(result == model)
  }

}
