package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {

  // declaring val
  val aCondition: Boolean = false
  val aConditionedVal = if(aCondition) 44 else 46
  println(aConditionedVal)

  // instruction vs expressions

  // the compiler will infer the type of the val
  val aCodeBlock = {
    if(aCondition) 55   //  this expression value will be ignored
    56  // code block returns the last expression value
  }
  println(aCodeBlock)

  // Unit = void
  // unit is type for expressions that doesn't return value
  // these expression is used to do side effects
  // unit has special value ()
  val theUnit = println("hello, Scala")
  println(theUnit)

  // functions
  def aFunction(x: Int) = x + 1
  println(aFunction(2))

  // Recursion
  @tailrec
  def factorial(n: Int, acc: Int = 1): Int = {
    if(n <= 0) acc
    else factorial(n - 1, n * acc)
  }

  println(factorial(5))

  // OOP
  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog

  // trait used to describe behavior
  trait Carnivore {
    def eat(a: Animal): Unit
  }


  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!")
  }

  // method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  // operators are functions that uses infix notation
  println(1.+(2))
  println(1 + 2)

  // anonymous classes
  val aCarniore = new Carnivore:
    override def eat(a: Animal): Unit = println("roar!")

  // generics
  abstract class MyList[+A] // variance problem
  // singletons and companions
  object MyList


  // case classes
  case class Person(name: String, age: Int)


  // exceptions and try/catch/finally

//  val throwsException = throw new RuntimeException // this return Nothing


  // the value returned is a super type form the return type of try and catch expression
  val aPotentialFail = try {
    throw new RuntimeException
    3
  } catch {
    case npE: NullPointerException => "I caught a NullPointerException"
    case rtE: RuntimeException => "I caught a RuntimeException"
    case e: _ => "I caught an exception"
  } finally {
    println("some logs")
  }

  // functional programming
  // anonymous class
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  println(incrementer(1))

  // another way for writing anonymous class
  val incrementer2 = new (Int => Int) {
    override def apply(v1: Int): Int = v1 + 2
  }
  println(incrementer2(1))

  // syntactic sugar for using anonymous classes as function
  // anonymous functions
  // lambda expression
  val incrementer3 = (x: Int) => x + 3
  println(incrementer3(1))

  // Higher Order Function (HOF)
  println(List(1, 2, 3).map(incrementer))
  println(List(1, 2, 3).map(incrementer2))
  println(List(1, 2, 3).map(incrementer3))

  // any class that have map, flatMap, filter
  // can be used in for-comprehension

  // this will return a list of string with all combination of the two lists
  val pairs = for {
    num <- List(1, 2, 3) // can add if here for filtering
    char <- List('a', 'b', 'c')
  } yield s"$num-$char"

  println(pairs)

  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps, Tuples
  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )
  println(aMap)

  // "collections" Options, Try
  val anOption = Some(2)

  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => s"$x th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"hi my name $n"
  }

  // all pattern




  val p1 = new Person("eslam", 25)
  val p2 = new Person("eslam", 25)


  // prints false (compares object)
  println(p1 eq p2)

  // prints true (compares values)
  println(p1 == p2)



}

case class Person(name: String, age: Int)