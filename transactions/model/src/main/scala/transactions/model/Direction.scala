package transactions.model

import io.circe.Codec

object Direction extends Enumeration {
  type Direction = Value
  val In, Out = Value

  implicit val codec: Codec[Direction] = Codec.codecForEnumeration(Direction)
}
