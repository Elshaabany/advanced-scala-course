package lectures.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}
import concurrent.duration.*

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  // Future requires execution context
  // that can be passed implicitly by importing
  // import concurrent.ExecutionContext.Implicits.global
  val aFuture = Future {
    // the code here runs on another thread
    calculateMeaningOfLife
  }

//  Thread.sleep(2000)
  println(aFuture.value)

  aFuture.onComplete {
    case Success(value) => println(value)
    case Failure(value) => println(value)
  }

//  Thread.sleep(2000)

  // mini social network example

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // database
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchingBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }

  }

  // client: mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchingBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(exception) => exception.printStackTrace()
      }
    }
    case Failure(exception) => exception.printStackTrace()
  }

  Thread.sleep(2000)
  println(mark)

  val nameOnTheWall = mark.map(profile => profile.name)

  Thread.sleep(3000)
  println(nameOnTheWall.value)

  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchingBestFriend(profile))
  Thread.sleep(3000)

  println(marksBestFriend.value)

  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("B"))

  println(zucksBestFriendRestricted.value)

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchingBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallback
  // recover
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Dummy")
  }

  // recover with
  val fetchAnotherProfile = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  // fallback to
  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo {
    SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  // example for blocking on futures
  // online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "InstaPay"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from the DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // simulate some processes
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status
      Await.result(transactionStatusFuture, 2.seconds)
    }
  }

  println(BankingApp.purchase("eslam", "pixel 8 pro", "Google play", 800))

  // promises
  // promises is controller over the future
  // it make us manipulate the future manually
  val promise = Promise[Int]()
  val future = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(r) => println("[consumer] I've received " + r)
    case _ =>
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    // fail on condition
//    if (true) promise.failure(throw new RuntimeException("no meaning of life"))
//     "fulfilling" the promise
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)


  // exercises

  /*
    1) fulfill a future IMMEDIATELY with a value
    2) inSequence(fa, fb)  [run fb after fa finishes]
    3) first(fa, fb) => new future with the first finished value of the two futures
    4) last(fa, fb) => new future with the last finished value
    5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   */

  // 1
  // this will run synchronously on the same thread
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  // 2
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
    first.flatMap(_ => second)

  // 3
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
  }

  // 4
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    // we will create 2 promises
    // the first both of them will try to complete
    // the remaining future will complete the second promise
    val both = Promise[A]
    val last = Promise[A]
    val checkAndComplete = (result: Try[A]) =>
      if(!both.tryComplete(result))
        last.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    last.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future {
    Thread.sleep(200)
    45
  }

  first(fast, slow).foreach(println)
  last(fast, slow).foreach(println)

  Thread.sleep(1000)

  // 5
  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] = {
    action()
      .filter(condition)
      .recoverWith {
      case _ => retryUntil(action, condition)
    }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(res => println("settled at " + res))
  Thread.sleep(200000)

}