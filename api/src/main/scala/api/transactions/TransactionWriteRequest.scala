package api.transactions

import java.time.LocalDateTime
import java.util.UUID

import transactions.model._

import io.circe.Codec, io.circe.generic.semiauto.deriveCodec
import sttp.tapir.Schema.annotations._

import Direction._

final case class TransactionWriteRequest(
    owner: String,
    direction: Direction,
    amount: Int,
    currency: String,
    @encodedExample("2023-01-01T10:00:00") bookedAt: LocalDateTime,
    title: Option[String]
) {
  def toModel(id: UUID) = Transaction(
    id,
    owner,
    direction,
    amount,
    currency,
    bookedAt,
    title
  )
}

object TransactionWriteRequest {
  implicit val codec: Codec[TransactionWriteRequest] = deriveCodec
}
