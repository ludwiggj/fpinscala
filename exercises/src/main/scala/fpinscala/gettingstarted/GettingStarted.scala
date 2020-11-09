package fpinscala.gettingstarted

import scala.annotation.tailrec

// A comment!
/* Another comment */
/** A documentation comment */
object MyModule {
  def abs(n: Int): Int =
    if (n < 0) -n
    else n

  private def formatAbs(x: Int) = {
    val msg = "The absolute value of %d is %d"
    msg.format(x, abs(x))
  }

  def main(args: Array[String]): Unit =
    println(formatAbs(-42))

  // A definition of factorial, using a local, tail recursive function
  def factorial(n: Int): Int = {
    @annotation.tailrec
    def go(n: Int, acc: Int): Int =
      if (n <= 0) acc
      else go(n - 1, n * acc)

    go(n, 1)
  }

  // Another implementation of `factorial`, this time with a `while` loop
  def factorial2(n: Int): Int = {
    var acc = 1
    var i = n
    while (i > 0) {
      acc *= i; i -= 1
    }
    acc
  }

  // Exercise 1: Write a function to compute the nth fibonacci number
  def fib(n: Int): Int = {
    @tailrec
    def go(n: Int, prev: Int, cur: Int): Int = {
      if (n == 0)
        prev
      else
        go(n - 1, cur, prev + cur)
    }

    go(n, 0, 1)
  }

  // This definition and `formatAbs` are very similar..
  private def formatFactorial(n: Int) = {
    val msg = "The factorial of %d is %d."
    msg.format(n, factorial(n))
  }

  // We can generalize `formatAbs` and `formatFactorial` to
  // accept a _function_ as a parameter
  def formatResult(name: String, n: Int, f: Int => Int): String = {
    val msg = "The %s of %d is %d."
    msg.format(name, n, f(n))
  }
}

object FormatAbsAndFactorial {

  import MyModule._

  // Now we can use our general `formatResult` function
  // with both `abs` and `factorial`
  def main(args: Array[String]): Unit = {
    println(formatResult("absolute value", -42, abs))
    println(formatResult("factorial", 7, factorial))
  }
}

// Functions get passed around so often in FP that it's
// convenient to have syntax for constructing a function
// *without* having to give it a name
object AnonymousFunctions {

  import MyModule._

  // Some examples of anonymous functions:
  def main(args: Array[String]): Unit = {
    println(formatResult("increment", 7, (x: Int) => x + 1))
    println(formatResult("increment2", 7, (x) => x + 1))
    println(formatResult("increment3", 7, x => x + 1))
    println(formatResult("increment4", 7, _ + 1))
    println(formatResult("increment5", 7, x => {
      val r = x + 1; r
    }))
  }
}

object MonomorphicBinarySearch {

  // First, a findFirst, specialized to `String`.
  // Ideally, we could generalize this to work for any `Array` type.
  def findFirst(ss: Array[String], key: String): Int = {
    @annotation.tailrec
    def loop(n: Int): Int =
    // If `n` is past the end of the array, return `-1`
    // indicating the key doesn't exist in the array.
      if (n >= ss.length) -1
      // `ss(n)` extracts the n'th element of the array `ss`.
      // If the element at `n` is equal to the key, return `n`
      // indicating that the element appears in the array at that index.
      else if (ss(n) == key) n
      else loop(n + 1) // Otherwise increment `n` and keep looking.
    // Start the loop at the first element of the array.
    loop(0)
  }

  def main(args: Array[String]): Unit = {
    val a = Array("the", "cat", "sat", "on", "the", "mat")

    val the = "the"
    val cat = "cat"
    val dog = "dog"

    println(s"Index of first occurrence of '$the' in [${a.mkString(", ")}] is ${findFirst(a, the)}")
    println(s"Index of first occurrence of '$cat' in [${a.mkString(", ")}] is ${findFirst(a, cat)}")
    println(s"Index of first occurrence of '$dog' in [${a.mkString(", ")}] is ${findFirst(a, dog)}")
  }

  // Next, a binary search implementation, specialized to `Double`,
  // another primitive type in Scala, representing 64-bit floating
  // point numbers
  // Ideally, we could generalize this to work for any `Array` type,
  // so long as we have some way of comparing elements of the `Array`
  def binarySearch(ds: Array[Double], key: Double): Int = {
    @annotation.tailrec
    def go(low: Int, mid: Int, high: Int): Int = {
      if (low > high) -mid - 1
      else {
        val mid2 = (low + high) / 2
        val d = ds(mid2) // We index into an array using the same syntax as function application
        if (d == key) mid2
        else if (d > key) go(low, mid2, mid2 - 1)
        else go(mid2 + 1, mid2, high)
      }
    }

    go(0, 0, ds.length - 1)
  }
}

object PolymorphicFunctions {

  // Here's a polymorphic version of `findFirst`, parameterized on
  // a function for testing whether an `A` is the element we want to find.
  // Instead of hard-coding `String`, we take a type `A` as a parameter.
  // And instead of hard-coding an equality check for a given key,
  // we take a function with which to test each element of the array.
  def findFirst[A](as: Array[A], p: A => Boolean): Int = {
    @annotation.tailrec
    def loop(n: Int): Int =
      if (n >= as.length) -1
      // If the function `p` matches the current element,
      // we've found a match and we return its index in the array.
      else if (p(as(n))) n
      else loop(n + 1)

    loop(0)
  }

  def main(args: Array[String]): Unit = {
    val a = Array("the", "cat", "sat", "on", "the", "mat")
    val the = "the"
    println(s"Index of first occurrence of '$the' in [${a.mkString(", ")}] is ${findFirst[String](a, _ == the)}")

    val b = Array(5, 7, 8)
    println(s"Index of first even number in [${b.mkString(", ")}] is ${findFirst[Int](b, _ % 2 == 0)}")

    val doubleIt = (i: Int) => i * 2
    val add5 = (i: Int) => i + 5

    def doubleItAndAdd5: Int => Int = doubleIt andThen add5

    println(s"doubleItAndAdd5(4) = ${doubleItAndAdd5(4)}")

    def add5AndDoubleIt: Int => Int = doubleIt compose add5

    println(s"add5AndDoubleIt(4) = ${add5AndDoubleIt(4)}")

    def doubleItDef(i: Int): Int = i * 2
    def add5Def(i: Int): Int = i + 5

    // Underscore is needed to ensure that method is not applied, otherwise the error message is:
    // Error: missing argument list for method doubleItDef
    // Unapplied methods are only converted to functions when a function type is expected.
    // You can make this conversion explicit by writing `doubleItDef _` or `doubleItDef(_)` instead of `doubleItDef`.
    //    def doubleItAndAdd5Def: Int => Int = doubleItDef andThen add5Def
    def doubleItAndAdd5Def: Int => Int = doubleItDef _ andThen add5Def

    println(s"doubleItAndAdd5Def(4) = ${doubleItAndAdd5Def(4)}")

    def add5AndDoubleItDef: Int => Int = doubleItDef _ compose add5Def

    println(s"add5AndDoubleItDef(4) = ${add5AndDoubleItDef(4)}")
  }

  // Here's a polymorphic version of `binarySearch`, parameterized on
  // a function for testing whether an `A` is greater than another `A`.
  def binarySearch[A](as: Array[A], key: A, gt: (A, A) => Boolean): Int = {
    @annotation.tailrec
    def go(low: Int, mid: Int, high: Int): Int = {
      if (low > high) -mid - 1
      else {
        val mid2 = (low + high) / 2
        val a = as(mid2)
        val greater = gt(a, key)
        if (!greater && !gt(key, a)) mid2
        else if (greater) go(low, mid2, mid2 - 1)
        else go(mid2 + 1, mid2, high)
      }
    }

    go(0, 0, as.length - 1)
  }

  // Exercise 2: Implement a polymorphic function to check whether
  // an `Array[A]` is sorted
  def isSorted[A](as: Array[A], gt: (A, A) => Boolean): Boolean = {
    @scala.annotation.tailrec
    def go(n: Int): Boolean = {
      if (n >= as.length - 1) true
      else if (gt(as(n), as(n+1))) false
      else go(n+1)
    }

    go(0)
  }

  // Polymorphic functions are often so constrained by their type
  // that they only have one implementation! Here's an example:

  def partial1[A, B, C](a: A, f: (A, B) => C): B => C =
    (b: B) => f(a, b)

  // Exercise 3: Implement `curry`.

  // Note that `=>` associates to the right, so we could
  // write the return type as `A => B => C`
  def curry[A, B, C](f: (A, B) => C): A => (B => C) =
    (a: A) => (b: B) => f(a, b)

  // NB: The `Function2` trait has a `curried` method already

  // Exercise 4: Implement `uncurry`
  def uncurry[A, B, C](f: A => B => C): (A, B) => C =
    (a, b) => f(a)(b)

  /*
  NB: There is a method on the `Function` object in the standard library,
  `Function.uncurried` that you can use for uncurrying.

  Note that we can go back and forth between the two forms. We can curry
  and uncurry and the two forms are in some sense "the same". In FP jargon,
  we say that they are _isomorphic_ ("iso" = same; "morphe" = shape, form),
  a term we inherit from category theory.
  */

  // Exercise 5: Implement `compose`
  def compose[A, B, C](f: B => C, g: A => B): A => C =
    a => f(g(a))
}

// Test the exercises
object TestExercises {
  import MyModule._
  import PolymorphicFunctions._

  def testExercise2_1(): Unit = {
    println("Exercise 2.1, fibonacci")
    println("Expected: 0, 1, 1, 2, 3, 5, 8")
    println("Actual:   %d, %d, %d, %d, %d, %d, %d".format(fib(0), fib(1), fib(2), fib(3), fib(4), fib(5), fib(6)))
    println()
  }

  def testExercise2_2(): Unit = {
    println("Exercise 2.2, isSorted")

    val a = Array(2, 4, 6, 9)
    println(s"[${a.mkString(", ")}] sorted numerically? ${isSorted[Int](a, _ > _)}")

    val b = Array("the", "cart", "before", "the", "horse")
    println(s"[${b.mkString(", ")}] sorted by word length? ${isSorted[String](b, _.length > _.length)}")

    println()
  }

  def testExercise2_3(): Unit = {
    println("Exercise 2.3, curry")

    def multiply(x: Int, y: Int): Int = x * y

    def curriedMultiply: Int => Int => Int = curry(multiply)

    println("multiply(6, 7) = " + multiply(6, 7))
    println("curriedMultiply(6)(7) = " + curriedMultiply(6)(7))

    println()
  }

  def testExercise2_4(): Unit = {
    println("Exercise 2.4, uncurry")

    def multiply(x: Int)(y: Int): Int = x * y

    def uncurriedMultiply: (Int, Int) => Int = uncurry(multiply)

    println("multiply(6)(7) = " + multiply(6)(7))
    println("uncurriedMultiply(6, 7) = " + uncurriedMultiply(6, 7))

    println()
  }

  def testExercise2_5(): Unit = {
    println("Exercise 2.5, compose")

    def doubleAndAdd5: Int => Int = compose[Int, Int, Int](_ + 5, _ * 2)

    println("2 * 2 + 5 = " + doubleAndAdd5(2))
    println("5 * 2 + 5 = " + doubleAndAdd5(5))

    println()
  }

  // test implementation of `fib`
  def main(args: Array[String]): Unit = {
    testExercise2_1()
    testExercise2_2()
    testExercise2_3()
    testExercise2_4()
    testExercise2_5()
  }
}