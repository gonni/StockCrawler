package c.x.horus.stock


import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.defaultCSVFormat
import io.getquill.jdbczio.Quill
import zio.*
import zio.Console.printLine

import java.io.File
import scala.util.Try

import io.getquill.*
import io.getquill.jdbczio.Quill
import c.x.horus.db._

object Csv2DbZioApp extends ZIOAppDefault {

  class CsvFileService {
    def getCsvFileReader(fileName: String = "res/all_stock_2023_utf8.csv"): Task[CSVReader] = {
      ZIO.succeed(CSVReader.open(new File(fileName)))
    }
  }

  object CsvFileService {
    def create(): CsvFileService = new CsvFileService
    val live: ZLayer[Any, Nothing, CsvFileService] =
      ZLayer.succeed(create())
  }


  val app = for {
    reader <- ZIO.service[CsvFileService]
    csv <- reader.getCsvFileReader()
    repo <- ZIO.service[StockItemRepo]
    res <- repo.batchInsert(csv.toStream.map(row =>
      StockItem(row.head, row(1), row(2), row(3))).toList)
  } yield res.size

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    app.provide(
      CsvFileService.live,
      StockItemRepo.live,
      Quill.Mysql.fromNamingStrategy(io.getquill.SnakeCase),
      Quill.DataSource.fromPrefix("mydbConf"),
//      ZLayer.Debug.mermaid
    ).map(println)
}
