package errors

import io.circe._
import io.circe.syntax._

/**
  * @param logMessage   Message logged in system logs
  * @param userMessage  Message returned to end user
  */
abstract class ApiError(
    val userMessage: String,
    val meta:       Map[String, String] = Map.empty
) extends Throwable {
  val logMessage: String = userMessage
}

object ApiError {
  final case class Generic(
      override val userMessage: String = "Internal server error"
  ) extends ApiError(userMessage)

  final case class Database(
      error: Throwable,
      actionDesc: String,
      override val userMessage: String = "Internal server error"
  ) extends ApiError(
    userMessage
  ) {
    override val logMessage = s"Database error when performing action: $actionDesc"
  }

  implicit def jsonDecoder[E <: ApiError]: Decoder[E] =
    Decoder.decodeJsonObject.emap { _ =>
      Left("Decoding unsupported")
    }

  implicit def jsonEncoder[E <: ApiError]: Encoder[E] =
    Encoder.encodeJson.contramap { err =>
      val payload: Map[String, String] =
        Map("message" -> err.userMessage) ++
          err.meta.map { case (k, v) => k -> v }
      payload.asJson
    }
}