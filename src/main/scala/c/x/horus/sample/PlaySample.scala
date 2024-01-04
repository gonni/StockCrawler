package c.x.horus.sample


import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

object PlaySample extends App {
  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
//  val a = Future[Int]{
//    println("Activated 1")
//    1
//  }
//
//  val b = Future[Int](2)
//
//  val c = for {
//    a1 <- a
//    _ <- Future.failed(new Exception("Failed by Intent"))
//    b1 <- b
//  } yield (a1 + b1)
//
//  val c1 = Await.result(c, Duration.Inf)
//
//  println("result -> " + c1)

  def doFutureThings(a: Int, b: Int = 100) = {
//    val res = if (a == 0) {
//      Future.failed(new Exception("Cannot be divided by zero"))
////      Future(-1)
//    } else {
//      Future(b / a)
//    }
//    res.recover{
//      case e: Exception => {
//        println("Invalid value detected :" + a)
//
//      }
//    }
//    res
    (for {
      v <- if(a == 0) {
        Future.failed(new Exception("Invalid value"))
      } else Future.successful(a)
      aa <- Future(b / v)
    } yield aa).recover{
      case e: Exception => -1
    }

  }


  val lstNumsFuture = Future(List[Int](1,2,3,0,5,6,7))
  val result = for {
    lstNums <- lstNumsFuture
    _ <- Future(println("Active .."))
    ni <- Future.sequence(lstNums.map(c => doFutureThings(c)))
    _ <- Future(println("Completed .."))
  } yield ni

  val res1 = Await.result(result, Duration.Inf)
  res1.foreach(println)


}
