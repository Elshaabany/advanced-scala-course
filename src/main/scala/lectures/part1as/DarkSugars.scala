package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // list of syntax sugars
  // syntax sugar #1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  // calling single arg methods using code block instead of parentheses
  val description = singleArgMethod {
    // some code
    42 // last values will be send as argument
  }

  // example of single arg method
  val aTryInstance = Try {
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method
  trait Action {
    def act(x: Int): Int
  }

  // can be implemented using anonymous function
  val anInstance: Action = new Action:
    override def act(x: Int): Int = x + 1

  // same result with lambda functions
  val aFunkyInstance: Action = (x: Int) => x + 1

  // example using Runnable trait
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("hello, scala")
  })

  // it can also be done using lambda functions
  val aSweeterThread = new Thread(() => println("hello, scala from lambda"))


  // this can be done also with abstract types that has only one unimplemented method
  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")

  // syntax sugar #3: the :: and #:: methods are special

  // last char of the method name decides the associativity
  // if it is ":" then the associativity is right to left
  val prependedList = 2 :: List(3, 4)
  // 2.::(List(3, 4))
  // List(3, 4).::(2)

  1 :: 2 :: 3 :: List(4, 5)
  List(4, 5).::(3).::(2).::(1)


  // another example of right to left associativity
  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]
  // new MyStream[Int].-->:(3).-->:(2).-->:(1)


  // syntax sugar #4: multi-word method naming

  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  // calling the multi-word method using infix notation
  lilly `and then said` "scala is sweet"


  // syntax sugar #5: infix types
  class Composite[A, B]
  // normal use
  val composite: Composite[Int, String] = new Composite[Int, String]
  // using infix types notation
  val composite2: Int Composite String = new Composite[Int, String]
  // this make the type more readable in some cases
  // ex

  class -->[A, B]
  val toward: Int --> String = new -->[Int, String]


  // syntax sugar #6: update() : special method like apply()
  // used with mutable types
  val anArray = Array(1, 2, 3)
  anArray(2) = 7
  // syntax sugar for
  // anArray.update(2, 7)


  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var innerVar: Int = 0

    // getter
    def useVar = innerVar

    // setter
    def useVar_=(value: Int): Unit =
      innerVar = value
  }

  val aMutableContainer = new Mutable
  // using setter method as assigning value
  aMutableContainer.useVar = 43
  // rewritten to
  // aMutableContainer.useVar_=(43)


  println(aMutableContainer.useVar)

}
