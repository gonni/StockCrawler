package c.x.horus.sample.httpserver

import zio._
import zio.http._

object HttpServerMain extends ZIOAppDefault {


  def runConsoleApp: ZIO[Any, Nothing, Unit] = {
    ZIO.succeed{
      (1 to 100).foreach { i =>
        Thread.sleep(1000)
        println("Turn #" + i)
      }
    }
  }

  val httpApp = HelloApp()
  val httpServerapp = Server
    .serve(httpApp.withDefaultErrorResponse)
    .provide(
      Server.defaultWithPort(8090)
    )

  val totalApp = for {
    _ <- runConsoleApp.fork
    _ <- httpServerapp
  } yield ()


  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = totalApp.exitCode
}
