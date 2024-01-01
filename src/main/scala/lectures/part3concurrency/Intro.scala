package lectures.part3concurrency

import java.util.concurrent.{Executor, Executors}

object Intro extends App {

//  val aThread = new Thread(new Runnable {
//    override def run(): Unit = {
//      Thread.sleep(1000)
//      println("multi threading")
//    }
//  })
//
//
//  println("before start")
//  aThread.start() // start another thread
//  println("after start")
//  println("--------------------")
//  println("before run")
//  aThread.run() // execute it on the current thread
//  println("after run")
//  println("--------------------")
//  Thread.sleep(5000)
//  println("before join")
//  aThread.join() // block the current thread until it done
//  println("after join")
//  Thread.sleep(5000)
//  println("--------------------")


//  val thread1 = new Thread(() => println("hello form thread 1"))
//  val thread2 = new Thread(() => println("hello form thread 2"))
//
//  (1 to 5).foreach( x => {
//    println(x)
//  })

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodBye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))

  // runs with different results every time
  threadHello.start()
  threadGoodBye.start()

  // creating thread pool using executors library

  // this creates pool with 10 threads
  val pool = Executors.newFixedThreadPool(10)

  //
  pool.execute(() => println("executing using thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
    println("done after 1 sec")
  })

  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 sec")
  })

  // will shutdown the pool after the treads complete their work
  pool.shutdown()

  // will force shutdown even if their is some thread does not completed their job
  // threads that didn't finished their actions will throw exception
//  pool.shutdownNow()

  // any usage of the pool after shutdown will throw exception
//  pool.execute(() => println("hello"))


}
