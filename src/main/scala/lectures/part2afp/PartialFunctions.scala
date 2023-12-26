package lectures.part2afp

object PartialFunctions extends App {

  // normal functions
  val aFunction = (x: Int) => x + 1
  // this is instance of type Function1[Int, Int]
  // this is called Total function
  // it accept any int (Not limit to some values of int)


  // this is away of limiting the args that can be sent to the function
  val aFussyFunction = (x: Int) => {
    if (x == 1) 42
    else if (x == 2) 43
    else if (x == 3) 44
    else throw new FunctionNotApplicablaException
  }
  class FunctionNotApplicablaException extends RuntimeException

  println(aFussyFunction(3))

  // another way of achieving this is using pattern
  // throws match exception if not matched
  val anotherFussyFunction = (x: Int) => x match {
    case 1 => 123
    case 2 => 34
    case 6 => 99
  }
  // this limit the args that can be sent to the function to {1, 2, 6}
  println(anotherFussyFunction(6))

  // scala introduces a better way to do this
  // the PartialFunction type

  // it is done by only defining the val with type PartialFunction
  // also throws match exception if not matched
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 123
    case 2 => 34
    case 6 => 99
  }

  println(aPartialFunction(2))


  // PF utilities
  println(aPartialFunction.isDefinedAt(33))

  // lift
  // wraps the return into option so it doesn't throw if not matched
  val lifted = aPartialFunction.lift
  println(lifted(2))
  println(lifted(20))

  // provide another partial function as fallback if not matched
  val pfchain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfchain(45))

  // partial functions extends normal functions
  // you can assign partial function to normal function
  
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }
  
  println(aTotalFunction(1))
  
  // HOFs accept partial functions as well
  
  val aMappedList = List(1, 2, 3).map {
    case 1 => 44
    case 2 => 45
    case 3 => 46
  }
  println(aMappedList)

  /**
   * Exercises
   *
   * 1 - construct a PF instance yourself (anonymous class)
   *
   *  2 - dumb chatbot as a PF
   */
  
  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 44
      case 2 => 45
      case 3 => 46
    }

    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x == 5
  }
  
  val chatBot: String => String = {
    case "hello" => "Hi, my name is HAL9000"
    case "goodbye" => "Once you start talking to me, there is no return, human!"
    case "call mom" => "Unable to find your phone without your credit card"
  }

  scala.io.Source.stdin.getLines().map(chatBot).foreach(println)


}
