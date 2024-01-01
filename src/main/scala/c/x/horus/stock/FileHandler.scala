package c.x.horus.stock

import zio.*
import zio.stream.ZStream

import java.io.{File, FileInputStream, IOException}
import java.util.Scanner
import scala.io.{BufferedSource, Source}

object FileHandler extends ZIOAppDefault {

  def readLineByLine(path: String): UIO[Unit] =
    ZIO.acquireReleaseWith(ZIO.succeed(new Scanner(new File(path))))(
      scanner => ZIO.succeed(s"close file").debug *> ZIO.succeed(scanner.close())
    ) { scanner =>
      ZIO.succeed{
        while(scanner.hasNextLine)
          println(scanner.nextLine())
      }
    }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = (for {
    _ <- Console.printLine("Start readFile ..")
    _ <- readLineByLine("res/all_stock_2023_utf8.csv")
    _ <- Console.printLine("Finished")
  } yield()).exitCode
}
