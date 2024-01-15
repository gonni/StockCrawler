package c.x.horus.sample.streaming

import c.x.horus.db.{StockItem, StockItemRepo}
import io.getquill.jdbczio.Quill
import zio.*
import zio.stream.*

object DbZStream extends ZIOAppDefault {
  val stockStream: ZStream[StockItemRepo, Throwable, StockItem] =
    ZStream.fromIterableZIO(StockItemRepo.getAll)

//  val application = users.foreach(a => Console.printLine(a))

//  val criFilter: String => Boolean = stockName =>
//    stockName.contains("크리")

  val criFilterPipeline: ZPipeline[Any, Nothing, StockItem, StockItem] =
    ZPipeline.filter(si => si.companyNm.contains("크리"))

//  val printlnZink: ZSink[Any, Throwable, ]
  import Console._

  val application = stockStream
    .via(criFilterPipeline)
    .run(ZSink.foreach(si => Console.printLine(si)))

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = 
    application.provide(
      StockItemRepo.live,
      Quill.Mysql.fromNamingStrategy(io.getquill.SnakeCase),
      Quill.DataSource.fromPrefix("mydbConf")
    )
}
