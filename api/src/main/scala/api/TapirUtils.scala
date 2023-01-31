package api

import errors.ApiError

import io.circe.Printer
import io.circe.syntax._
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{DecodeResult, Schema, Validator}

object TapirUtils extends TapirJsonCirce {
  implicit def apiErrorsSchema[E <: ApiError]: Schema[E] = Schema.string

  /**
    * This makes sure all empty optional values are omitted in API responses
    */
  override def jsonPrinter: Printer = Printer.spaces2.copy(dropNullValues = true)
}
