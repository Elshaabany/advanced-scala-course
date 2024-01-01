package lectures.part3concurrency

object ConcurrencyProblems {

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    // race condition
    println(x)
  }

  // example on race condition

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int) = {
    // race condition
    // not atomic operation
    // because it involves 3 steps:
    // - read old value
    // - compute result
    // - write new value
    // multiple threads can read the value at the same time
    // so the result will not be synchronized
    bankAccount.amount -= price
  }
  // solution of this problem
  def buySafe(bankAccount: BankAccount, thing: String, price: Int) = {
    // this achieve synchronization on bankAccount values
    // by not allowing multiple threads to run on the same value at one time
    bankAccount.synchronized {
      // write critical operations here
      bankAccount.amount -= price
    }
  }

  def main(args: Array[String]): Unit = {
    // we can't determine which thread will change the var first
    runInParallel()

//    (1 to 100_000_000).foreach { _ =>
//
//      val account = new BankAccount(50000)
//      val thread1 = new Thread(() => buySafe(account, "shoes", 3000))
//      val thread2 = new Thread(() => buySafe(account, "shoes", 4000))
//      thread1.start()
//      thread2.start()
//      thread1.join()
//      thread2.join()
//      if(account.amount != 43000) println(s"bank got broken ${account.amount}")
//    }

    demoSleepFallacy()
  }

  /**
   * Exercises
   * 1 - create "inception threads"
   * thread 1
   * -> thread 2
   * -> thread 3
   * ....
   * each thread prints "hello from thread $i"
   * Print all messages IN REVERSE ORDER
   *
   * 2 - What's the max/min value of x?
   * 3 - "sleep fallacy": what's the value of message?
   */
  // 1
  def inceptionThread(maxThread: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i < maxThread) {
        val newThread = inceptionThread(maxThread, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $i")
    })
  }

  // 2
  // min: 1, max: 100
  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start())
  }

  // 3

  // it's not grantee for awesome thread to finish before the main thread
  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "scala is awesome"
    })

    message = "Scala sucks"
    awesomeThread.start()
    Thread.sleep(1001)
    // the solution is to use join
    awesomeThread.join()
    println(message)
  }

}
