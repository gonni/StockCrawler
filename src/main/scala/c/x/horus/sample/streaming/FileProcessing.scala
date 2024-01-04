package c.x.horus.sample.streaming

import zio.stream.*
import zio.*
import zio.Console.printLine

import scala.io.Source

object FileProcessing extends ZIOAppDefault{

//  val zs = ZStream.fromFileName("res/all_stock_2023_utf8.csv")
//    .via(ZPipeline.utf8Decode >>> ZPipeline.splitLines)
//    .foreach(printLine(_))

  val lines: ZStream[Any, Throwable, String] =
    ZStream
      .acquireReleaseWith(
        ZIO.attempt(Source.fromFile("res/all_stock_2023_utf8.csv")) <* printLine("The file was opened.")
      )(x => ZIO.succeed(x.close()) <* printLine("The file was closed.").orDie)
      .flatMap { is =>
        ZStream.fromIterator(is.getLines())
      }

  val rs = lines
//    .via(ZPipeline.utf8Decode >>> ZPipeline.splitLines)
    .foreach(printLine(_))

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = rs.exit
}
