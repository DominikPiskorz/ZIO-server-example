package api.transactions

import java.util.UUID

import errors.ApiError

object TransactionsError {
  final case class NotFound(id: UUID)
      extends ApiError(
        userMessage = s"Transaction not found: $id",
        meta = Map("id" -> id.toString)
      )
}
