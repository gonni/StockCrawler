package c.x.horus.stock

import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.defaultCSVFormat

import java.io.{File, FileWriter, PrintWriter}
import scala.io.Source
import scala.util.Try
import zio.*

import java.util.Scanner

object StockItemSync {

  // Sample
  def readCsvFile() = {
    val reader = CSVReader.open(new File("res/all_stock_2023_utf8.csv"))
    println(reader.readNext().get.mkString(","))
    println(reader.readNext().get.mkString(","))
    println(reader.readNext().get.mkString(","))
    reader.close()
  }

  object CsvFileReader {
    def targetFile(path: String): ZIO[Any with Scope, Nothing, Scanner] = ZIO.acquireRelease(
      ZIO.succeed(new Scanner(new File(path))))(
      scanner => ZIO.succeed(s"close file").debug *> ZIO.succeed(scanner.close())
    )

    def live(path: String): ZLayer[String, Nothing, ZIO[Any with Scope, Nothing, Scanner]] =
      ZLayer.fromFunction(targetFile _)
  }

  // real file
  case class StockItemMeta(
                          code: String,
                          name: String,
                          stockCat: String,
                          companyCat: String
                          )

  trait StockItemProvider {
    def getAllData: ZIO[Any, Throwable, List[StockItemMeta]]
  }

  class StockItemProviderImpl extends StockItemProvider {
    override def getAllData: ZIO[Any, Throwable, List[StockItemMeta]] = ???
  }


  def main(args: Array[String]): Unit = {
    readCsvFile()
  }
}
