package c.x.horus.sample.httpserver

import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.jdbczio.Quill
import io.getquill.*
import zio.*

case class MysqlUserRepo(ctx: Quill.Mysql[SnakeCase]) extends UserRepo {
  import ctx.*

  inline given schema: SchemaMeta[User] = schemaMeta[User]("TEMP_USERS")

  override def register(user: User): Task[Long] = run{
    query[User].insertValue(lift(user))
  }

  override def users: Task[List[User]] = run {
    query[User]
  }
}
