package transactions.model

import java.time.LocalDateTime
import java.util.UUID

import io.circe.Codec, io.circe.generic.semiauto.deriveCodec

import Direction._

final case class Transaction(
    id: UUID,
    owner: String,
    direction: Direction,
    amount: Long,
    currency: String,
    bookedAt: LocalDateTime,
    title: Option[String]
)

object Transaction {
  implicit val codec: Codec[Transaction] = deriveCodec
}
