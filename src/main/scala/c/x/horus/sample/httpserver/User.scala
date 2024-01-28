package c.x.horus.sample.httpserver
import zio.json.*

import java.util.UUID

case class User(name: String, age: Int)

object User:
  given JsonEncoder[User] =
    DeriveJsonEncoder.gen[User]
  given JsonDecoder[User] =
    DeriveJsonDecoder.gen[User]