// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : test/scala/analysis/StatisticsAnalysisTest.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Unit tests for the recursive statistical analysis functions.
// ============================================================================

package analysis

import org.scalatest.funsuite.AnyFunSuite
import utils.*

class StatisticsAnalysisTest extends AnyFunSuite:

  // -------------------------------------------------------------------------
  // Test data
  // -------------------------------------------------------------------------
  val sampleValues: List[Double]   = List(10.0, 20.0, 30.0, 40.0, 50.0)  // odd length
  val evenValues: List[Double]     = List(10.0, 20.0, 30.0, 40.0)         // even length
  val singleValue: List[Double]    = List(42.0)
  val repeatedValues: List[Double] = List(1.0, 2.0, 2.0, 3.0, 3.0, 3.0)  // mode is 3.0
  val emptyValues: List[Double]    = Nil
  val allSame: List[Double]        = List(5.0, 5.0, 5.0)

  // -------------------------------------------------------------------------
  // Mean
  // -------------------------------------------------------------------------
  test("mean of sample values [10,20,30,40,50] should return 30.0"):
    val result = StatisticsAnalysis.mean(sampleValues)
    assert(result == Success(30.0))

  test("mean of single value should return that value"):
    val result = StatisticsAnalysis.mean(singleValue)
    assert(result == Success(42.0))

  test("mean of identical values should return that value"):
    val result = StatisticsAnalysis.mean(allSame)
    assert(result == Success(5.0))

  test("mean of empty list should return Failure"):
    val result = StatisticsAnalysis.mean(emptyValues)
    assert(result.isFailure)

  // -------------------------------------------------------------------------
  // Median
  // -------------------------------------------------------------------------
  test("median of odd-length list [10,20,30,40,50] should return 30.0"):
    val result = StatisticsAnalysis.median(sampleValues)
    assert(result == Success(30.0))

  test("median of even-length list [10,20,30,40] should return average of two middles (25.0)"):
    val result = StatisticsAnalysis.median(evenValues)
    assert(result == Success(25.0))

  test("median of single-element list should return that element"):
    val result = StatisticsAnalysis.median(singleValue)
    assert(result == Success(42.0))

  test("median of unsorted list should still compute correctly"):
    val result = StatisticsAnalysis.median(List(50.0, 10.0, 30.0))
    assert(result == Success(30.0))

  test("median of empty list should return Failure"):
    val result = StatisticsAnalysis.median(emptyValues)
    assert(result.isFailure)

  // -------------------------------------------------------------------------
  // Mode
  // -------------------------------------------------------------------------
  test("mode of [1,2,2,3,3,3] should return 3.0"):
    val result = StatisticsAnalysis.mode(repeatedValues)
    assert(result == Success(3.0))

  test("mode of list where all values are the same should return that value"):
    val result = StatisticsAnalysis.mode(allSame)
    assert(result == Success(5.0))

  test("mode of empty list should return Failure"):
    val result = StatisticsAnalysis.mode(emptyValues)
    assert(result.isFailure)

  // -------------------------------------------------------------------------
  // Range
  // -------------------------------------------------------------------------
  test("range of [10,20,30,40,50] should return 40.0"):
    val result = StatisticsAnalysis.range(sampleValues)
    assert(result == Success(40.0))

  test("range of single-element list should return 0.0"):
    val result = StatisticsAnalysis.range(singleValue)
    assert(result == Success(0.0))

  test("range of identical values should return 0.0"):
    val result = StatisticsAnalysis.range(allSame)
    assert(result == Success(0.0))

  test("range of empty list should return Failure"):
    val result = StatisticsAnalysis.range(emptyValues)
    assert(result.isFailure)

  // -------------------------------------------------------------------------
  // Midrange
  // -------------------------------------------------------------------------
  test("midrange of [10,20,30,40,50] should return 30.0"):
    val result = StatisticsAnalysis.midrange(sampleValues)
    assert(result == Success(30.0))

  test("midrange of single-element list should return that element"):
    val result = StatisticsAnalysis.midrange(singleValue)
    assert(result == Success(42.0))

  test("midrange of [10, 90] should return 50.0"):
    val result = StatisticsAnalysis.midrange(List(10.0, 90.0))
    assert(result == Success(50.0))

  test("midrange of empty list should return Failure"):
    val result = StatisticsAnalysis.midrange(emptyValues)
    assert(result.isFailure)

  // -------------------------------------------------------------------------
  // analyzeReadings (higher-order + type parameterization)
  // -------------------------------------------------------------------------
  test("analyzeReadings with mean function should compute mean of energyMW values"):
    import models.{EnergyReading, EnergySource}
    import java.time.LocalDateTime
    val now = LocalDateTime.now()
    val readings = List(
      EnergyReading(EnergySource.Solar, now, now, 10.0),
      EnergyReading(EnergySource.Solar, now, now, 20.0),
      EnergyReading(EnergySource.Solar, now, now, 30.0)
    )
    val result = StatisticsAnalysis.analyzeReadings(readings, StatisticsAnalysis.mean)
    assert(result == Success(20.0))

  // -------------------------------------------------------------------------
  // curriedAnalysis (currying demonstration)
  // -------------------------------------------------------------------------
  test("curriedAnalysis partially applied with mean should produce the same result as mean"):
    import models.{EnergyReading, EnergySource}
    import java.time.LocalDateTime
    val now = LocalDateTime.now()
    val readings = List(
      EnergyReading(EnergySource.Wind, now, now, 100.0),
      EnergyReading(EnergySource.Wind, now, now, 200.0)
    )
    // Partial application: fix the analysis function, get back a readings → result fn
    val curriedMean = StatisticsAnalysis.curriedAnalysis(StatisticsAnalysis.mean)
    assert(curriedMean(readings) == StatisticsAnalysis.analyzeReadings(readings, StatisticsAnalysis.mean))
