package c.x.horus.sample.httpserver

import zio._

trait UserRepo:
  def register(user: User): Task[Long]

//  def lookup(id: String): Task[Option[User]]

  def users: Task[List[User]]

object UserRepo:
  def register(user: User): ZIO[UserRepo, Throwable, Long] =
    ZIO.serviceWithZIO[UserRepo](_.register(user))

//  def lookup(id: String): ZIO[UserRepo, Throwable, Option[User]] =
//    ZIO.serviceWithZIO[UserRepo](_.lookup(id))

  def users: ZIO[UserRepo, Throwable, List[User]] =
    ZIO.serviceWithZIO[UserRepo](_.users)