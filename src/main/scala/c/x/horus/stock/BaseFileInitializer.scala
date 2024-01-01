package c.x.horus.stock

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.*

class BaseFileInitializer {
  def convertFileCharset() =
    Try {
      val fw = new PrintWriter(new File("res/all_stock_2023_utf8.csv"))
      val source = Source.fromFile("res/all_stock_2023.csv", enc = "EUC-KR")
      source.getLines().foreach(line => fw.println(line))
      fw.close()
      source.close()
    }.toEither match {
      case Left(e) => println(s"IOException : $e")
      case Right(_) => println("File reWrite succeed ..")
    }
}
