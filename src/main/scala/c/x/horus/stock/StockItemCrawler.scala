package c.x.horus.stock

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.defaultCSVFormat

import java.io.{File, FileWriter, PrintWriter}
import scala.io.Source
import scala.util.Try

object StockItemCrawler {

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

  def readCsvFile() = {
    val reader = CSVReader.open(new File("res/all_stock_2023_utf8.csv"))
    println(reader.readNext().head.mkString(","))
    println(reader.readNext().head.mkString(","))
    println(reader.readNext().head.mkString(","))
    reader.close()
  }

  def main(args: Array[String]): Unit = {
    readCsvFile()
  }
}
