package lectures.part1as

object AdvancedPatternMatching extends App {

  // pattern matching allow us to do like:
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
    - constants
    - wildcards
    - case classes
    - tuples
    - some special magic like above
   */

  // how to make our own patten match
  // if we have a class that is not case class

  class Person(val name: String, val age: Int)
  // extractor methods
  // there is special method called unapply that is used in pattern matching
  // the extractor is an object with unapply method
  // it can be a companion object or object used only for matching
  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    // we can define multiple unapply methods
    def unapply(age: Int): Option[String] = Some (
      if (age > 18) "allowed"
      else "denied"
    )


  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    // the compiler searches for object called Person
    // that have apply method returning tuple of 2 params
    // and the result is not empty
    case Person(n, a) => s"hello my name is $n and I'm $a years old"
    case _ =>
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => status
  }

  println(legalStatus)

  // Exercise

  object even {
    def unapply(n: Int): Boolean = n % 2 == 0
  }

  object single {
    def unapply(n: Int): Boolean = n > -10 && n < 10
  }


  val x: Int = 11
  val mathProp = x match {
    case even() => "x is even"
    case single() => "x is single"
    case _ => "unmatched"
  }

  println(mathProp)

  // how to do our own infix pattern like head :: Nil
  // this will has it's own unapply methods as it's case class
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescription = either match {
    // using infix pattern
    // only works when you have 2 params
    case number Or string => s"$number is written as $string"
  }
  println(humanDescription)


  // decomposing sequences
  val vararg = numbers match {
    // matching list that start with 1 followed by zero or many of any elements
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    // will search for MyList object unapplySeq method that accept MyList[Int] and return Seq
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "anything else"
  }

  println(decomposed)

  // unapply can have other return types than Options
  // but it must be implementing:
  // isEmpty: Boolean , get: T

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false

      override def get: String = s"my name is ${person.name} and my age is ${person.age}"
    }
  }

  println(bob match
    case PersonWrapper(data) => data
    case _ => "no data"
  )






}
