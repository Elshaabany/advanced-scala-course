package playground

object play extends App {

  def get43 = {println("getting 43") ; 43}
  def get42 = {println("getting 42") ; 42}
  def get41 = {println("getting 41") ; 41}
  def get40 = {println("getting 40") ; 40}


  def byName(value: => Int): Int = {
    println("before using value")
    println(s"get $value by name")
    println(s"remember the $value")
    value + 1
  }

  println("----------calling-byName----------")
  println(byName(get43))
  println(byName(get42))


  def byValue(value: Int): Int = {
    println("before using value")
    println(s"get $value by value")
    println(s"remember the $value")

    value * 2
  }

  println("----------calling-byValue----------")
  println(byValue(get41))
  println(byValue(get40))


  def byNameNLazy(value: => Int): Int = {
    println("before declaring lazy value")
    lazy val lazyValue = value
    println("before using value")
    println(s"get $lazyValue by name")
    println(s"remember the $lazyValue")
    lazyValue + 1
  }

  println("----------calling-byName----------")
  println(byNameNLazy(43))
  println(byNameNLazy(42))

}
