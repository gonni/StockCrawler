package c.x.horus.sample

import zio.*

object ZLayerSample extends ZIOAppDefault{

  case class User(name: String, email: String)

  class UserSubscription(emailService: EmailService, userDatabase: UserDatabase) {
    def subscribeUser(user: User): Task[Unit] =
      for {
        _ <- emailService.email(user)
        _ <- userDatabase.insert(user)
      } yield()
  }

  object UserSubscription {
    def create(emailService: EmailService, userDatabase: UserDatabase) =
      new UserSubscription(emailService, userDatabase)

    def live: ZLayer[EmailService with UserDatabase, Nothing, UserSubscription] =
      ZLayer.fromFunction(create _)
  }

  class EmailService {
    def email(user: User): Task[Unit] =
      ZIO.succeed(println(s"You.ve just beed subscribed to Rock the JVM, Welcome ${user.name}"))
  }

  object EmailService {
    def create(): EmailService = new EmailService
    val live: ZLayer[Any, Nothing, EmailService] =
      ZLayer.succeed(create())
  }

  class UserDatabase(connectionPool: ConnectionPool) {
    def insert(user: User): Task[Unit] = for {
      conn <- connectionPool.get
      _ <- conn.runQuery(s"insert into subsribers(name, email) values (${user.name}, ${user.email})")
    } yield ()
  }

  object UserDatabase {
    def create(connectionPool: ConnectionPool): UserDatabase =
      new UserDatabase(connectionPool)
    val live: ZLayer[ConnectionPool, Nothing, UserDatabase] = ZLayer.fromFunction(create _)
  }

  class ConnectionPool(nConnection: Int) {
    def get: Task[Connection] =
      ZIO.succeed(println("Acquired connection")) *> ZIO.succeed(Connection())
  }

  object ConnectionPool {
    def create(nConnection: Int) = new ConnectionPool(nConnection)
    def live(nConnections: Int): ZLayer[Any, Nothing, ConnectionPool] =
      ZLayer.succeed(create(nConnections))
  }

  case class Connection() {
    def runQuery(query: String): Task[Unit] =
      ZIO.succeed(println(s"Executing query: $query"))
  }

//  val subscriptionService = ZIO.succeed(
//    new UserSubscription(
//      new EmailService,
//      new UserDatabase(
//        new ConnectionPool(10)
//      )
//    )
//  )

  def subscribe_v2(user: User): ZIO[UserSubscription, Throwable, Unit] = for {
    sub <- ZIO.service[UserSubscription]
    _ <- sub.subscribeUser(user)
  } yield ()


  val program_v2 = for {
    _ <- subscribe_v2(User("A", "a@abc.com"))
    _ <- subscribe_v2(User("B", "b@abc.com"))
  } yield ()

  // ------------
  // ZLayer
  val connectionPoolLayer: ZLayer[Any, Nothing, ConnectionPool] = ZLayer.succeed(ConnectionPool.create(10))
  val databaseLayer: ZLayer[ConnectionPool, Nothing, UserDatabase] = ZLayer.fromFunction(UserDatabase.create _)
  val emailServiceLayer = ZLayer.succeed(EmailService.create())
  val userSubscriptionServiceLayer: ZLayer[UserDatabase & EmailService, Nothing, UserSubscription] =
    ZLayer.fromFunction(UserSubscription.create _)

  // compose layers
  // vertical composition
  val databaseLayerFull: ZLayer[Any, Nothing, UserDatabase] = connectionPoolLayer >>> databaseLayer
  // horizontal composition
  val subscriptionReqLayer: ZLayer[Any, Nothing, UserDatabase & EmailService]
    = databaseLayerFull ++ emailServiceLayer
  // mix & match
  val userSubScriptionLayer: ZLayer[Any, Nothing, UserSubscription] =
    subscriptionReqLayer >>> userSubscriptionServiceLayer

  // Final Solution



  // best practice
  val runnableProgram = program_v2.provide(userSubScriptionLayer)
  // magic
  val runnableProgram_v2 = program_v2.provide(
    UserSubscription.live,
    EmailService.live,
    UserDatabase.live,
    ConnectionPool.live(10),
//    ZLayer.Debug.tree,
    ZLayer.Debug.mermaid
  )

//  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
//    program_v2.provide(userSubScriptionLayer)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    runnableProgram_v2

//  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
//    program_2.provide(
//      ZLayer.succeed(
//        UserSubscription.create(
//          EmailService.create(),
//          UserDatabase.create(
//            ConnectionPool.create(10)
//          )
//        )
//      )
//    )
}
