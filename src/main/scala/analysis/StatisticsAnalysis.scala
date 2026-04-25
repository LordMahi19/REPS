// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : analysis/StatisticsAnalysis.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Pure functions for statistical analysis of energy data.
//              All computations are implemented recursively without mutable
//              state, following the Functional Programming paradigm.
// ============================================================================

package analysis

import models.EnergyReading
import utils.RepsResult

/**
 * Object containing pure, recursive statistical analysis functions.
 *
 * Requirements from the project specification:
 *   - Mean      : average of all values
 *   - Median    : middle value when sorted
 *   - Mode      : most frequently occurring value
 *   - Range     : max − min
 *   - Midrange  : (max + min) / 2
 *
 * All functions:
 *   - Accept immutable collections (List[Double] or List[EnergyReading]).
 *   - Use recursion instead of loops.
 *   - Return RepsResult[A] (functional error handling) to handle edge cases
 *     like empty lists gracefully.
 */
object StatisticsAnalysis:

  // -------------------------------------------------------------------------
  // Mean
  // -------------------------------------------------------------------------
  /**
   * Calculates the arithmetic mean of a list of values using recursion.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the mean, or an error if the list is empty
   */
  def mean(values: List[Double]): RepsResult[Double] =
    // TODO: Mahi — implement recursively
    //   Hint: create a recursive helper `sum` then divide by length.
    RepsResult.failure("mean not yet implemented")

  // -------------------------------------------------------------------------
  // Median
  // -------------------------------------------------------------------------
  /**
   * Finds the median value (middle element of a sorted list).
   * For even-length lists, returns the average of the two middle elements.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the median, or an error if the list is empty
   */
  def median(values: List[Double]): RepsResult[Double] =
    // TODO: Mahi — implement recursively
    //   Hint: sort the list first (you may use a recursive merge-sort),
    //   then pick the middle element(s).
    RepsResult.failure("median not yet implemented")

  // -------------------------------------------------------------------------
  // Mode
  // -------------------------------------------------------------------------
  /**
   * Finds the mode (most frequently occurring value) in the list.
   * If multiple values share the highest frequency, returns any one of them.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the mode, or an error if the list is empty
   */
  def mode(values: List[Double]): RepsResult[Double] =
    // TODO: Mahi — implement recursively
    //   Hint: build a frequency map recursively, then find the max entry.
    RepsResult.failure("mode not yet implemented")

  // -------------------------------------------------------------------------
  // Range
  // -------------------------------------------------------------------------
  /**
   * Calculates the range (max − min) of the values.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the range, or an error if the list is empty
   */
  def range(values: List[Double]): RepsResult[Double] =
    // TODO: Mahi — implement recursively
    //   Hint: find min and max with recursive helpers, then subtract.
    RepsResult.failure("range not yet implemented")

  // -------------------------------------------------------------------------
  // Midrange
  // -------------------------------------------------------------------------
  /**
   * Calculates the midrange ((max + min) / 2) of the values.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the midrange, or an error if the list is empty
   */
  def midrange(values: List[Double]): RepsResult[Double] =
    // TODO: Mahi — implement recursively
    RepsResult.failure("midrange not yet implemented")

  // -------------------------------------------------------------------------
  // Convenience: extract energy values from readings
  // -------------------------------------------------------------------------
  /**
   * Higher-order helper that extracts the energyMW field from a list of
   * EnergyReading records and applies an analysis function to the result.
   *
   * Demonstrates:
   *   - Higher-order functions (takes a function as parameter)
   *   - Type parameterization
   *
   * @param readings the list of energy readings
   * @param analysisFunction the statistical function to apply
   * @tparam A the return type of the analysis function
   * @return the result of applying the analysis function to the energy values
   */
  def analyzeReadings[A](
    readings: List[EnergyReading],
    analysisFunction: List[Double] => RepsResult[A]
  ): RepsResult[A] =
    val values = readings.map(_.energyMW)
    analysisFunction(values)
