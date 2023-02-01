package transactions.repository

import zio._

final case class TransactionListingSpec(
    limit: Int = 10,
    page: Int = 0,
    sortBy: TransactionListingSortField = TransactionListingSortField.BookedAt,
    asc: Boolean = false
) {
  def offset: Int = limit * page

  def ascSql: String = if (asc) "asc" else "desc"
}

object TransactionListingSpec {
  def fromOptionals(
      limit: Option[Int],
      page: Option[Int],
      sortBy: Option[TransactionListingSortField],
      asc: Option[Boolean]
  ): TransactionListingSpec = {
    val defaultSpec = TransactionListingSpec()

    val withLimit =
      limit.map(l => defaultSpec.copy(limit = l)).getOrElse(defaultSpec)
    val withPage =
      page.map(p => withLimit.copy(page = p)).getOrElse(withLimit)
    val withSort =
      sortBy.map(s => withPage.copy(sortBy = s)).getOrElse(withPage)
    val withAsc =
      asc.map(p => withSort.copy(asc = p)).getOrElse(withSort)

    withAsc
  }
}

trait TransactionListingSortField {
  def toSqlField: String
}

object TransactionListingSortField {
  object BookedAt extends TransactionListingSortField {
    def toSqlField = "booked_at"
  }

  object Title extends TransactionListingSortField {
    def toSqlField = "title"
  }

  object Amount extends TransactionListingSortField {
    def toSqlField = "amount"
  }

  object Currency extends TransactionListingSortField {
    def toSqlField = "currency"
  }

  object Direction extends TransactionListingSortField {
    def toSqlField = "direction"
  }

  def fromString(s: String): Task[TransactionListingSortField] = s match {
      case "bookedAt"  => ZIO.succeed(BookedAt)
      case "title"     => ZIO.succeed(Title)
      case "amount"    => ZIO.succeed(Amount)
      case "currency"  => ZIO.succeed(Currency)
      case "direction" => ZIO.succeed(Direction)
      case other       => ZIO.fail(new IllegalArgumentException(s"Invalid sort: $other"))
  }
}
