// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : test/scala/utils/ErrorHandlerTest.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Unit tests for the functional error-handling module.
// ============================================================================

package utils

import org.scalatest.funsuite.AnyFunSuite

class ErrorHandlerTest extends AnyFunSuite:

  // -------------------------------------------------------------------------
  // RepsResult basics
  // -------------------------------------------------------------------------
  test("Success should contain the value"):
    val result: RepsResult[Int] = RepsResult.success(42)
    assert(result.isSuccess)
    assert(result.getOrElse(0) == 42)

  test("Failure should contain the error message"):
    val result: RepsResult[Int] = RepsResult.failure("something went wrong")
    assert(result.isFailure)
    assert(result.getOrElse(0) == 0)

  // -------------------------------------------------------------------------
  // map
  // -------------------------------------------------------------------------
  test("map on Success should transform the value"):
    val result = RepsResult.success(10).map(_ * 2)
    assert(result == Success(20))

  test("map on Failure should propagate the error"):
    val result: RepsResult[Int] = RepsResult.failure[Int]("error").map(_ * 2)
    assert(result == Failure("error"))

  // -------------------------------------------------------------------------
  // flatMap
  // -------------------------------------------------------------------------
  test("flatMap on Success should chain computations"):
    val result = RepsResult.success(10).flatMap(x => RepsResult.success(x + 5))
    assert(result == Success(15))

  test("flatMap on Failure should propagate the error"):
    val result = RepsResult.failure[Int]("error").flatMap(x => RepsResult.success(x + 5))
    assert(result == Failure("error"))

  // -------------------------------------------------------------------------
  // fold
  // -------------------------------------------------------------------------
  test("fold on Success should apply onSuccess handler"):
    val result = RepsResult.success(10).fold(_ => "fail", v => s"got $v")
    assert(result == "got 10")

  test("fold on Failure should apply onFailure handler"):
    val result = RepsResult.failure[Int]("oops").fold(msg => s"error: $msg", _ => "ok")
    assert(result == "error: oops")

  // -------------------------------------------------------------------------
  // attempt
  // -------------------------------------------------------------------------
  test("attempt should catch exceptions and return Failure"):
    val result = RepsResult.attempt(throw new RuntimeException("boom"))
    assert(result.isFailure)

  test("attempt should wrap successful expressions in Success"):
    val result = RepsResult.attempt(1 + 1)
    assert(result == Success(2))

  // -------------------------------------------------------------------------
  // requireNonEmpty
  // -------------------------------------------------------------------------
  test("requireNonEmpty should succeed for non-empty list"):
    val result = RepsResult.requireNonEmpty(List(1, 2, 3))
    assert(result.isSuccess)

  test("requireNonEmpty should fail for empty list"):
    val result = RepsResult.requireNonEmpty(Nil, "readings")
    assert(result.isFailure)

  // -------------------------------------------------------------------------
  // validateDateFormat
  // -------------------------------------------------------------------------
  test("validateDateFormat should succeed for valid DD/MM/YYYY"):
    val result = RepsResult.validateDateFormat("12/04/2024")
    assert(result == Success((12, 4, 2024)))

  test("validateDateFormat should succeed for another valid date"):
    val result = RepsResult.validateDateFormat("01/01/2026")
    assert(result == Success((1, 1, 2026)))

  test("validateDateFormat should fail for natural-language date format"):
    val result = RepsResult.validateDateFormat("April 12, 2024")
    assert(result.isFailure)

  test("validateDateFormat should fail for ISO date format without slashes"):
    val result = RepsResult.validateDateFormat("2024-04-12")
    assert(result.isFailure)

  test("validateDateFormat should fail for invalid month (13)"):
    val result = RepsResult.validateDateFormat("12/13/2024")
    assert(result.isFailure)

  test("validateDateFormat should fail for invalid day (00)"):
    val result = RepsResult.validateDateFormat("00/04/2024")
    assert(result.isFailure)

  test("validateDateFormat should fail for empty string"):
    val result = RepsResult.validateDateFormat("")
    assert(result.isFailure)
