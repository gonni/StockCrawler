package c.x.horus.api

import zio.http._
import zio._

object HttpSample extends ZIOAppDefault {

//  val routes = Routes(
//    Method.GET / "hello" -> handler(ZIO.succeed(Response.text("Hello World")))
//  )

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ???
}
