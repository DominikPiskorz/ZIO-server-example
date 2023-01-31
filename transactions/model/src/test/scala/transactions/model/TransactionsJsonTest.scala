package transactions.model

import org.scalatest.funsuite._
import java.util.UUID
import java.time.LocalDateTime

import io.circe.syntax._, io.circe.parser._

class TransactionsJsonTest extends AnyFunSuite {
  test("Transaction should serialize to JSON") {
    val id = "e7f68be3-796b-426f-942a-13391a67cd5c"

    val transaction = Transaction(
        id = UUID.fromString(id),
        owner = "user",
        direction = Direction.Out,
        amount = 123,
        currency = "EUR",
        bookedAt = LocalDateTime.of(2023, 1, 1, 10, 0),
        title = Some("title")
    )

    val json = s"""
    {
        "id" : "$id",
        "owner" : "user",
        "direction": "Out",
        "amount" : 123,
        "currency" : "EUR",
        "bookedAt" : "2023-01-01T10:00:00",
        "title": "title"
    }
    """

    assert(parse(json).contains(transaction.asJson))
    assert(parse(json).flatMap(_.as[Transaction]).contains(transaction))
  }
}
