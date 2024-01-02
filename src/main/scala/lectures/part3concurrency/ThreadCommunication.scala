package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  // the problem working with threads
  // can't force certain order of the executing threads

  // producer-consumer problem
  // producer and consumer are threads that run in parallel
  // the consumer is depending on value that is generated by producer thread
  // so consumer should wait for producer to finish first

  // producer -> [ x ] -> consumer
  class SimpleContainer {
    private var value: Int = 0
    def isEmpty: Boolean = value == 0
    // simulate producing
    def set(newValue: Int) = value = newValue

    // simulate consuming
    def get = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons() = {

    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while(container.isEmpty) {
        println("[consumer] actively waiting...")
      }
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    producer.start()
    consumer.start()
  }

//  naiveProdCons()


  // smarter producer-consumer
  // using wait and notify

  def  smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] smart waiting...")
      container.synchronized {
        container.wait()
      }

      // container must have some value
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] smart work!")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println("[producer] I'm producing " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()

  }

//  smartProdCons()

  // let's redesign the pattern so we can store more than one value

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting ...")
            buffer.wait()
          }

          // if the it reached here that mean that producer has value or more
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          // notify the producer to produce new values
          buffer.notify()
        }

        Thread.sleep(random.nextInt(600))
      }

    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting ...")
            buffer.wait()
          }

          // if reached here that mean that it notified by consumer
          // to produce new value

          println("[producer] producing " + i)
          buffer.enqueue(i)

          // now notify the consumer that their is new value
          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

//  prodConsLargeBuffer()


  // let's make multi producer-consumer design

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting ...")
            buffer.wait()
          }

          val x = buffer.dequeue()
          println(s"[consumer $id] consumed " + x)

          buffer.notifyAll()
        }

        Thread.sleep(random.nextInt(250))
      }

    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {

    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting ...")
            buffer.wait()
          }

          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notifyAll()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }

  }

  def multiProdCons(nConsumers: Int, nProducers: Int) = {
    val buffer = new mutable.Queue[Int]
    val capacity = 10

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

  multiProdCons(3, 6)


}
