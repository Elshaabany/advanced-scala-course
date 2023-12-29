package lectures.part2afp

object LazyEvaluation extends App {

  // lazy delay the evaluation of the value

  lazy val x: Int = {
    // if x is not used this will not be printed
    println("hello, lazy!")
    42
  }

  val y = {
    // will be executed even if y is not used
    println("crazy!")
    44
  }

  // lazy val only evaluates once
  // then the value is stored and used
  println(x)
  println(x)

  def sideEffectCondition(): Boolean = {
    println("side effecting...")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition()
  println(if (simpleCondition && lazyCondition) "yes" else "no")

  def byNameMethod(n: Int): Int = {
    n + n + n + 1
  }

  def lazyByNameMethod(n: => Int): Int = {
    lazy val t = n
    t + t + t + 1
  }

  def retrieveMagicValue = {
    Thread.sleep(1000)
    println("waiting...")
    42
  }

  println(byNameMethod(retrieveMagicValue))
  println(lazyByNameMethod(retrieveMagicValue))

  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  println("---------normal-filtering---------")
  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  println("---------lazy-filtering---------")
  println(numbers)

  val lt30z = numbers.withFilter(lessThan30)
  val gt20z = lt30z.withFilter(greaterThan20)
  gt20z foreach println

  println("---------for-comprehension---------")
  val evenList = for {
    l <- List(1, 2, 3, 4) if l % 2 == 0 // if filtering uses lazy val
  } yield l + 1
  println(evenList)
  //  it will be converted to: 
  println(List(1, 2, 3, 4).withFilter(_ % 2 == 0).map(_ + 1))


  /*
      Exercise: implement a lazily evaluated, singly linked STREAM of elements.
  
      naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
      naturals.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream)
      naturals.foreach(println) // will crash - infinite!
      naturals.map(_ * 2) // stream of all even numbers (potentially infinite)
  */
  abstract class MyStream[+A] {
    
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]
    
    def #::[B >: A](element: B): MyStream[B] // prepend operator
    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenate two streams
    
    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of this stream
    def takeAsList(n: Int): List[A]
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }
  
}