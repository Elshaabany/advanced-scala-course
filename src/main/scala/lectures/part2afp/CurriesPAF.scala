package lectures.part2afp

object CurriesPAF extends App {

  // curried functions
  // created using Function Types (value)
  val superAdder: Int => Int => Int =
    x => y => x + y

  // can be called with multiple parameter list
  print(superAdder(2)(5))
  // can be called with the first parameter list
  // it will return function
  val add3 = superAdder(3)
  println(add3(2))

  // scala allows the definition of multiple param list using def
  // declared as method on the class
  def curriedAdder(x: Int)(y: Int) = x + y
  // using it as is
  println(curriedAdder(1)(2))
  // converting it to function value and use it as carried function
  // this is called lifting
  // = ETA-EXPANSION
  val add4 = curriedAdder(4)
  println(add4(1))

  // functions != methods
  // this defined as method
  def inc(x: Int) = x + 1
  // scala allow it to be passed as function val
  // thanks to ç
  List(1, 2, 3).map(inc)

   // explicit syntax for partially applying method
   // this will return function type
   val add5 = curriedAdder(5) _
   // this will get the same result
   val add5_2 = curriedAdder(5)

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  val add7 = (x: Int) => simpleAddFunction(7, x)
  // same as above
  val add7_1 = simpleAddFunction(7, _)
  val add7__1 = simpleAddFunction.curried(7)
  println(add7(1))
  println(add7_1(1))
  println(add7__1(1))

  val add7_2 = simpleAddMethod(7, _)
  println(add7_2(1))

  val add7_3 = curriedAddMethod(7)
  println(add7_3(1))

  val add7_4 = curriedAddMethod(7) _
  val add7__4 = curriedAddMethod(7)(_)
  println(add7_4(1))
  println(add7__4(1))

  // _ are powerful
  // it can create PAF from any normal method
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("hello, I'm ", _, " how are you")
  // converted to (x: String) => concatenator("hello, I'm ", x, " how are you")
  println(insertName("eslam"))

  val fillInTheBlanks = concatenator("Hello, ", _, _)
  println(fillInTheBlanks("eslam ", "scala is awsome"))

  // EXERCISES
  /*
    1.  Process a list of numbers and return their string representations with different formats
        Use the %4.2f, %8.6f and %14.12f with a curried formatter function.
   */

  def curriedFormatter(s: String)(number: Double): String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f")
  val seriousFormat = curriedFormatter("%8.6f")
  val preciseFormat = curriedFormatter("%14.12f")

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))
  /*
     2.  difference between
         - functions vs methods
         - parameters: by-name vs 0-lambda
    */

  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parentMethod(): Int = 42

  /*
    calling byName and byFunction
    - int
    - method
    - parenMethod
    - lambda
    - PAF
   */

  byName(23)
  byName(method)
  byName(parentMethod())
  //
//  byName(parentMethod) // not allowed in scala 3
//  byName(() => 42) // expect value not method

  byName((() => 42)())
//  byName(parentMethod _) // need value

//  byFunction(45)      // expect function
  // byFunction(method) // this call the method and return value

  byFunction(parentMethod)
  byFunction(() => 46)
  byFunction(parentMethod _) // no need for _

}
