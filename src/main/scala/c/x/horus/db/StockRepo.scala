package c.x.horus.db

import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.json.*
import zio.*

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*

case class StockItem(
                      //                    id: Long,
                      itemCd: String,
                      companyNm: String,
                      stockCat: String,
                      companyCat: String
                    )

object StockItem {
  given codec: JsonCodec[StockItem] = DeriveJsonCodec.gen[StockItem]
}

object StockRepo extends ZIOAppDefault{

  import io.getquill.SnakeCase

  val program = for {
    repo <- ZIO.service[StockItempRepo]
    _ <- repo.create(StockItem("TEST05", "YGX", "KOSPI", "BIG3"))
    _ <- repo.create(StockItem("TEST06", "YGC", "KOSDAQ", "BIG2"))
  } yield ()


  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program.provide(
      StockItemRepo.live,
      Quill.Mysql.fromNamingStrategy(io.getquill.SnakeCase),
      Quill.DataSource.fromPrefix("mydbConf")
    )
}


trait StockItempRepo {
  def create(stockItem: StockItem): Task[Long]
  def batchInsert(stockItems: List[StockItem]): Task[List[Long]]
//  def update(itemCd: String, op:StockItem => StockItem): Task[StockItem]
//  def delete(itemCd: String): Task[StockItem]
  def getById(itemCd: String): Task[Option[StockItem]]
  def get: Task[List[StockItem]]
}

class StockItemRepoImpl(quill: Quill.Mysql[SnakeCase]) extends StockItempRepo {
  import quill.*

  inline given schema: SchemaMeta[StockItem] = schemaMeta[StockItem]("STOCK_ITEMS")
//  inline given instMeta: InsertMeta[StockItem] = insertMeta[StockItem](_.itemCd)
//  inline given upMeta: UpdateMeta[StockItem] = updateMeta[StockItem](_.itemCd)

  override def create(stockItem: StockItem): Task[Long] =
    run {
      query[StockItem]
        .insertValue(lift(stockItem))
    }

  override def batchInsert(stockItems: List[StockItem]): Task[List[Long]] = {
    val a = quote {
      liftQuery(stockItems).foreach(e => query[StockItem].insertValue(e))
    }
    run(a)
  }
//  override def update(itemCd: String, op: StockItem => StockItem): Task[StockItem] = for {
//    current <- getById(id)
//  }

//  override def delete(itemCd: String): Task[StockItem] = ???

  override def getById(itemCd: String): Task[Option[StockItem]] =
    run{
      query[StockItem]
        .filter(_.itemCd == lift(itemCd))
    }.map(_.headOption)

  override def get: Task[List[StockItem]] =
    run(query[StockItem])


}

object StockItemRepo {
  val live = ZLayer{
    ZIO.service[Quill.Mysql[SnakeCase]].map(quill => StockItemRepoImpl(quill))
  }

}