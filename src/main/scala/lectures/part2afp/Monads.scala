package lectures.part2afp

object Monads extends App {

  // let's build our monad
  // it will be like Try
  trait Attempt[+A] {
    def flatMap[B] (f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] = {
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  val attempt = Attempt {
    throw new RuntimeException("monadic behavior")
  }

  println(attempt)

  /*
    EXERCISE:
    1) implement a Lazy[T] monad = computation which will only be executed when it's needed.
      unit/apply
      flatMap

    2) Monads = unit + flatMap
       Monads = unit + map + flatten
   */

  println("----------lazy-monad----------")
  // 1-
  class Lazy[+A](value: => A) {
    // use value as laze to make it evaluate
    // once for every instance
    private lazy val internalValue = value
    def use: A = internalValue
    // make the function parameter called by name
    // so value does not get evaluated until needed
    def flatmap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("using lazy monad")
    49
  }

  println(lazyInstance.use)
  println(lazyInstance.use)

  val flatMappedInstance = lazyInstance.flatmap(x => Lazy {
    10 * x
  })

  val flatMappedInstance2 = lazyInstance.flatmap(x => Lazy {
    20 * x
  })

  println(flatMappedInstance.use)
  println(flatMappedInstance2.use)

  class Monad[T](value: T) { // List
    def flatMap[B](f: T => Monad[B]): Monad[B] = f(value)

    def map[B](f: T => B): Monad[B] = flatMap(x => Monad(f(x))) // Monad[B]

    def flatten(m: Monad[Monad[T]]): Monad[T] = m.flatMap((x: Monad[T]) => x)
    
  }
  
  object Monad {
    def apply[T](value: T): Monad[T] = new Monad(value)
  }

//  List(1, 2, 3).map(_ * 2) = List(1, 2, 3).flatMap(x => List(x * 2))
//  List(List(1, 2), List(3, 4)).flatten = List(List(1, 2), List(3, 4)).flatMap(x => x) = List(1, 2, 3, 4)

}
