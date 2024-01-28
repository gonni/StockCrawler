package c.x.horus.sample.httpserver

import zio.http._

object HelloApp {
  def apply(): Http[Any, Nothing, Request, Response] = {
    Http.collect[Request] {
      case Method.GET -> Root/"hello" =>
        Response.text(s"Hello World at ${System.currentTimeMillis()}")
    }
  }
}
