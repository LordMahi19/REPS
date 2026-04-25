// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : test/scala/analysis/StatisticsAnalysisTest.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Unit tests for the statistical analysis functions.
// ============================================================================

package analysis

import org.scalatest.funsuite.AnyFunSuite
import utils.*

class StatisticsAnalysisTest extends AnyFunSuite:

  // -------------------------------------------------------------------------
  // Test data
  // -------------------------------------------------------------------------
  val sampleValues: List[Double] = List(10.0, 20.0, 30.0, 40.0, 50.0)
  val singleValue: List[Double] = List(42.0)
  val emptyValues: List[Double] = Nil

  // -------------------------------------------------------------------------
  // Mean tests
  // -------------------------------------------------------------------------
  test("mean of sample values should return 30.0"):
    // TODO: Mahi — uncomment when mean is implemented
    // val result = StatisticsAnalysis.mean(sampleValues)
    // assert(result == Success(30.0))
    pending

  test("mean of empty list should return Failure"):
    // TODO: Mahi — uncomment when mean is implemented
    // val result = StatisticsAnalysis.mean(emptyValues)
    // assert(result.isFailure)
    pending

  // -------------------------------------------------------------------------
  // Median tests
  // -------------------------------------------------------------------------
  test("median of odd-length list should return middle element"):
    // TODO: Mahi — uncomment when median is implemented
    // val result = StatisticsAnalysis.median(sampleValues)
    // assert(result == Success(30.0))
    pending

  test("median of even-length list should return average of two middles"):
    // TODO: Mahi — uncomment when median is implemented
    // val result = StatisticsAnalysis.median(List(10.0, 20.0, 30.0, 40.0))
    // assert(result == Success(25.0))
    pending

  // -------------------------------------------------------------------------
  // Mode tests
  // -------------------------------------------------------------------------
  test("mode should return the most frequent value"):
    // TODO: Mahi — uncomment when mode is implemented
    // val result = StatisticsAnalysis.mode(List(1.0, 2.0, 2.0, 3.0, 3.0, 3.0))
    // assert(result == Success(3.0))
    pending

  // -------------------------------------------------------------------------
  // Range tests
  // -------------------------------------------------------------------------
  test("range of sample values should return 40.0"):
    // TODO: Mahi — uncomment when range is implemented
    // val result = StatisticsAnalysis.range(sampleValues)
    // assert(result == Success(40.0))
    pending

  // -------------------------------------------------------------------------
  // Midrange tests
  // -------------------------------------------------------------------------
  test("midrange of sample values should return 30.0"):
    // TODO: Mahi — uncomment when midrange is implemented
    // val result = StatisticsAnalysis.midrange(sampleValues)
    // assert(result == Success(30.0))
    pending
