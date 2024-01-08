package c.x.horus.sample.streaming

import c.x.horus.db.{StockItem, StockItemRepo}
import io.getquill.jdbczio.Quill
import zio.*
import zio.stream.*

object DbZStream extends ZIOAppDefault {
  val users: ZStream[StockItemRepo, Throwable, StockItem] =
    ZStream.fromIterableZIO(StockItemRepo.getAll)

  val application = users.foreach(a => Console.printLine(a))

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = 
    application.provide(
      StockItemRepo.live,
      Quill.Mysql.fromNamingStrategy(io.getquill.SnakeCase),
      Quill.DataSource.fromPrefix("mydbConf")
    )
}
