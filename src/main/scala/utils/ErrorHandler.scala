// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : utils/ErrorHandler.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Functional error-handling mechanisms for the REPS project.
//
//              Instead of throwing exceptions, the system uses an algebraic
//              data type (RepsResult[A]) to represent success or failure.
//              This ensures:
//                - Referential transparency (no side effects from exceptions)
//                - Exhaustive pattern matching on results
//                - Composability via map / flatMap / fold
//
// Key concepts demonstrated:
//   - Algebraic Data Types (sealed trait + case classes)
//   - Type parameterization (generics)
//   - Pattern matching
//   - Higher-order functions (map, flatMap, fold)
// ============================================================================

package utils

/**
 * A functional result type that represents either a successful value
 * or an error message. This is the project's primary error-handling mechanism.
 *
 * Similar in spirit to Scala's `Either[String, A]` or `Try[A]`, but
 * custom-built to demonstrate functional programming concepts.
 *
 * @tparam A the type of the successful value
 */
sealed trait RepsResult[+A]:

  /**
   * Transforms the successful value using function `f`.
   * If this is a Failure, the error propagates unchanged.
   *
   * Demonstrates: Higher-order functions, type parameterization.
   *
   * @param f the transformation function
   * @tparam B the result type after transformation
   * @return a new RepsResult with the transformed value, or the original error
   */
  def map[B](f: A => B): RepsResult[B] =
    // TODO: Mahi — implement using pattern matching
    //   Success(value) => Success(f(value))
    //   Failure(msg)   => Failure(msg)
    this match
      case Success(value) => Success(f(value))
      case Failure(msg)   => Failure(msg)

  /**
   * Chains a computation that itself may fail.
   * If this is a Failure, the error propagates unchanged.
   *
   * Demonstrates: Higher-order functions, monadic composition.
   *
   * @param f a function that returns a RepsResult
   * @tparam B the result type of the chained computation
   * @return the result of the chained computation, or the original error
   */
  def flatMap[B](f: A => RepsResult[B]): RepsResult[B] =
    // TODO: Mahi — implement using pattern matching
    this match
      case Success(value) => f(value)
      case Failure(msg)   => Failure(msg)

  /**
   * Extracts the value by handling both success and failure cases.
   *
   * Demonstrates: Pattern matching, exhaustive handling.
   *
   * @param onFailure handler for the error case
   * @param onSuccess handler for the success case
   * @tparam B the common return type
   * @return the result of the appropriate handler
   */
  def fold[B](onFailure: String => B, onSuccess: A => B): B =
    this match
      case Success(value) => onSuccess(value)
      case Failure(msg)   => onFailure(msg)

  /**
   * Returns the value if successful, or a default value if failed.
   *
   * @param default the fallback value
   * @tparam B a supertype of A
   * @return the contained value or the default
   */
  def getOrElse[B >: A](default: => B): B =
    this match
      case Success(value) => value
      case Failure(_)     => default

  /**
   * Returns true if this is a Success.
   */
  def isSuccess: Boolean =
    this match
      case Success(_) => true
      case Failure(_) => false

  /**
   * Returns true if this is a Failure.
   */
  def isFailure: Boolean = !isSuccess


/**
 * Represents a successful result containing a value.
 *
 * @param value the successful value
 * @tparam A the type of the value
 */
case class Success[+A](value: A) extends RepsResult[A]

/**
 * Represents a failed result containing an error message.
 *
 * @param message a description of what went wrong
 */
case class Failure(message: String) extends RepsResult[Nothing]


/**
 * Companion object providing factory methods and utility functions
 * for creating and working with RepsResult values.
 */
object RepsResult:

  /**
   * Creates a successful result.
   */
  def success[A](value: A): RepsResult[A] = Success(value)

  /**
   * Creates a failed result.
   */
  def failure[A](message: String): RepsResult[A] = Failure(message)

  /**
   * Wraps an unsafe (possibly exception-throwing) expression in a RepsResult.
   * Catches any NonFatal exception and converts it to a Failure.
   *
   * Demonstrates: bridging imperative (exception-based) code with
   * functional error handling.
   *
   * @param expr the expression to evaluate
   * @tparam A the type of the expression result
   * @return Success(result) or Failure(exception message)
   */
  def attempt[A](expr: => A): RepsResult[A] =
    try Success(expr)
    catch case e: Exception => Failure(e.getMessage)

  // -------------------------------------------------------------------------
  // Validation functions for user input
  // -------------------------------------------------------------------------

  /**
   * Validates that a date string matches the expected DD/MM/YYYY format.
   *
   * From the project specification (Additional Resources):
   *   - The system expects the date format "DD/MM/YYYY".
   *   - Display error message with example if format is incorrect.
   *
   * @param dateStr the date string to validate
   * @return Success with a parsed (day, month, year) tuple, or Failure with guidance
   */
  def validateDateFormat(dateStr: String): RepsResult[(Int, Int, Int)] =
    // TODO: Mahi — implement date validation
    //   - Check format matches DD/MM/YYYY pattern
    //   - Parse day, month, year
    //   - Validate ranges (day 1-31, month 1-12, year reasonable)
    //   - On error: include helpful message with example
    //     e.g. "Invalid date format. Please enter the date in the format
    //           'DD/MM/YYYY'. For example, enter '12/04/2024' for April 12, 2024."
    RepsResult.failure("validateDateFormat not yet implemented")

  /**
   * Validates that a list of values is non-empty before performing analysis.
   *
   * @param values the list to check
   * @param context a description of what was being analyzed (for error messages)
   * @tparam A the element type
   * @return Success(values) if non-empty, Failure with descriptive message otherwise
   */
  def requireNonEmpty[A](values: List[A], context: String = "data"): RepsResult[List[A]] =
    if values.nonEmpty then RepsResult.success(values)
    else RepsResult.failure(s"No available $context. Please choose another date or filter.")
