// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : analysis/StatisticsAnalysis.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Pure functions for statistical analysis of energy data.
//              All computations are implemented recursively without mutable
//              state, following the Functional Programming paradigm.
//
// FP concepts demonstrated in this file:
//   - Recursion        : all core algorithms use tail-recursive or structural recursion
//   - Higher-order fns : analyzeReadings takes a function parameter
//   - Type parameterisation : analyzeReadings[A] is generic
//   - Currying         : curriedAnalysis and filterThenAnalyze use curried functions
//   - Pattern matching : used throughout (list head/tail decomposition)
//   - Immutability     : no var, no mutation anywhere in this object
// ============================================================================

package analysis

import models.EnergyReading
import scala.annotation.tailrec
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
  // Private recursive helpers
  // -------------------------------------------------------------------------

  /**
   * Recursively sums all elements of a list (tail-recursive).
   * Uses an accumulator to avoid stack overflow on large lists.
   * Base case: empty list → accumulated sum
   * Recursive case: add head to accumulator, recurse on tail
   *
   * @param values the list to sum
   * @return the total sum
   */
  private def recursiveSum(values: List[Double]): Double =
    @tailrec
    def go(remaining: List[Double], acc: Double): Double =
      remaining match
        case Nil          => acc
        case head :: tail => go(tail, acc + head)
    go(values, 0.0)

  /**
   * Recursively counts all elements of a list (tail-recursive).
   * Uses an accumulator to avoid stack overflow on large lists.
   * Base case: empty list → accumulated count
   * Recursive case: increment accumulator, recurse on tail
   *
   * @param values the list to count
   * @return the number of elements
   */
  private def recursiveCount(values: List[Double]): Int =
    @tailrec
    def go(remaining: List[Double], acc: Int): Int =
      remaining match
        case Nil       => acc
        case _ :: tail => go(tail, acc + 1)
    go(values, 0)

  /**
   * Recursively finds the minimum value in a list.
   * Uses an accumulator to carry the current minimum through each recursive call.
   *
   * @param values the list to search (must be non-empty)
   * @param currentMin the smallest value seen so far
   * @return the minimum value
   */
  private def recursiveMin(values: List[Double], currentMin: Double): Double =
    values match
      case Nil          => currentMin
      case head :: tail =>
        if head < currentMin then recursiveMin(tail, head)
        else recursiveMin(tail, currentMin)

  /**
   * Recursively finds the maximum value in a list.
   * Uses an accumulator to carry the current maximum through each recursive call.
   *
   * @param values the list to search (must be non-empty)
   * @param currentMax the largest value seen so far
   * @return the maximum value
   */
  private def recursiveMax(values: List[Double], currentMax: Double): Double =
    values match
      case Nil          => currentMax
      case head :: tail =>
        if head > currentMax then recursiveMax(tail, head)
        else recursiveMax(tail, currentMax)

  /**
   * Recursively inserts a value into its correct position in a sorted list.
   * Used as a building block for insertionSort.
   *
   * @param value  the value to insert
   * @param sorted a list already in ascending order
   * @return a new sorted list containing value
   */
  private def recursiveInsert(value: Double, sorted: List[Double]): List[Double] =
    sorted match
      case Nil          => List(value)
      case head :: tail =>
        if value <= head then value :: sorted
        else head :: recursiveInsert(value, tail)

  /**
   * Recursively sorts a list in ascending order using insertion sort (tail-recursive).
   * Uses an accumulator (the growing sorted list) to avoid stack overflow.
   * Base case: no more elements → return the accumulated sorted list.
   * Recursive case: insert head into the sorted accumulator, recurse on tail.
   *
   * @param values the list to sort
   * @return a new list sorted in ascending order
   */
  private def insertionSort(values: List[Double]): List[Double] =
    @tailrec
    def go(remaining: List[Double], sorted: List[Double]): List[Double] =
      remaining match
        case Nil          => sorted
        case head :: tail => go(tail, recursiveInsert(head, sorted))
    go(values, Nil)

  /**
   * Recursively builds a frequency map (value → count) from a list.
   * Base case: empty list → empty map.
   * Recursive case: process the head, then recurse on the tail.
   *
   * @param values the list to count
   * @param acc    the accumulated frequency map so far
   * @return a map from each distinct value to its occurrence count
   */
  private def buildFrequencyMap(
    values: List[Double],
    acc: Map[Double, Int] = Map.empty
  ): Map[Double, Int] =
    values match
      case Nil          => acc
      case head :: tail =>
        val newCount = acc.getOrElse(head, 0) + 1
        buildFrequencyMap(tail, acc + (head -> newCount))

  /**
   * Recursively finds the key with the highest value in a frequency map.
   * Converts the map to a list of pairs and traverses recursively.
   *
   * @param entries the list of (value, frequency) pairs
   * @param bestKey the value with the highest frequency seen so far
   * @param bestCount the highest frequency seen so far
   * @return the value that occurs most often
   */
  private def findMaxFrequencyKey(
    entries: List[(Double, Int)],
    bestKey: Double,
    bestCount: Int
  ): Double =
    entries match
      case Nil                       => bestKey
      case (key, count) :: rest =>
        if count > bestCount then findMaxFrequencyKey(rest, key, count)
        else findMaxFrequencyKey(rest, bestKey, bestCount)

  // -------------------------------------------------------------------------
  // Mean
  // -------------------------------------------------------------------------
  /**
   * Calculates the arithmetic mean (average) of a list of values using recursion.
   *
   * Algorithm:
   *   1. Guard: return Failure if list is empty (division by zero would occur).
   *   2. Recursively compute the sum via recursiveSum.
   *   3. Recursively compute the count via recursiveCount.
   *   4. Divide sum by count and wrap in Success.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the mean, or a Failure if the list is empty
   */
  def mean(values: List[Double]): RepsResult[Double] =
    if values.isEmpty then
      RepsResult.failure("Cannot compute mean: the data list is empty.")
    else
      val total = recursiveSum(values)
      val count = recursiveCount(values)
      RepsResult.success(total / count)

  // -------------------------------------------------------------------------
  // Median
  // -------------------------------------------------------------------------
  /**
   * Finds the median value (middle element of a sorted list).
   * For an even-length list, returns the average of the two middle elements.
   *
   * Algorithm:
   *   1. Guard: return Failure if list is empty.
   *   2. Sort the list via insertionSort (recursive).
   *   3. Compute length via recursiveCount.
   *   4. Pick the middle element(s) by index and return Success.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the median, or a Failure if the list is empty
   */
  def median(values: List[Double]): RepsResult[Double] =
    if values.isEmpty then
      RepsResult.failure("Cannot compute median: the data list is empty.")
    else
      val sorted = insertionSort(values)
      val n      = recursiveCount(sorted)
      val mid    = n / 2
      if n % 2 != 0 then
        // Odd length: the exact middle element
        RepsResult.success(sorted(mid))
      else
        // Even length: average the two middle elements
        RepsResult.success((sorted(mid - 1) + sorted(mid)) / 2.0)

  // -------------------------------------------------------------------------
  // Mode
  // -------------------------------------------------------------------------
  /**
   * Finds the mode (most frequently occurring value) in the list.
   * If multiple values share the highest frequency, the first encountered is returned.
   *
   * Algorithm:
   *   1. Guard: return Failure if list is empty.
   *   2. Build a frequency map via buildFrequencyMap (recursive).
   *   3. Find the key with the maximum count via findMaxFrequencyKey (recursive).
   *   4. Wrap result in Success.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the mode, or a Failure if the list is empty
   */
  def mode(values: List[Double]): RepsResult[Double] =
    if values.isEmpty then
      RepsResult.failure("Cannot compute mode: the data list is empty.")
    else
      val freqMap = buildFrequencyMap(values)
      // Convert map to list of pairs for recursive traversal
      val pairs   = freqMap.toList
      pairs match
        case Nil => RepsResult.failure("Cannot compute mode: frequency map is empty.")
        case (firstKey, firstCount) :: rest =>
          val modeValue = findMaxFrequencyKey(rest, firstKey, firstCount)
          RepsResult.success(modeValue)

  // -------------------------------------------------------------------------
  // Range
  // -------------------------------------------------------------------------
  /**
   * Calculates the range (maximum − minimum) of the values.
   *
   * Algorithm:
   *   1. Guard: return Failure if list is empty.
   *   2. Use the first element as the initial min and max.
   *   3. Recursively traverse to find actual min and max.
   *   4. Return max − min wrapped in Success.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the range, or a Failure if the list is empty
   */
  def range(values: List[Double]): RepsResult[Double] =
    values match
      case Nil          =>
        RepsResult.failure("Cannot compute range: the data list is empty.")
      case head :: tail =>
        val minVal = recursiveMin(tail, head)
        val maxVal = recursiveMax(tail, head)
        RepsResult.success(maxVal - minVal)

  // -------------------------------------------------------------------------
  // Midrange
  // -------------------------------------------------------------------------
  /**
   * Calculates the midrange: the value exactly halfway between min and max.
   * Formula: (max + min) / 2
   *
   * Algorithm:
   *   1. Guard: return Failure if list is empty.
   *   2. Recursively find min and max.
   *   3. Return (max + min) / 2 wrapped in Success.
   *
   * @param values a non-empty list of Doubles
   * @return RepsResult containing the midrange, or a Failure if the list is empty
   */
  def midrange(values: List[Double]): RepsResult[Double] =
    values match
      case Nil          =>
        RepsResult.failure("Cannot compute midrange: the data list is empty.")
      case head :: tail =>
        val minVal = recursiveMin(tail, head)
        val maxVal = recursiveMax(tail, head)
        RepsResult.success((maxVal + minVal) / 2.0)

  // -------------------------------------------------------------------------
  // Higher-order function: analyzeReadings[A]
  // -------------------------------------------------------------------------
  /**
   * Higher-order function that extracts the energyMW field from a list of
   * EnergyReading records and applies any analysis function to the result.
   *
   * Demonstrates:
   *   - Higher-order functions (takes a function as a parameter)
   *   - Type parameterization (generic result type A)
   *
   * @param readings         the list of energy readings
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

  // -------------------------------------------------------------------------
  // Currying: curriedAnalysis
  // -------------------------------------------------------------------------
  /**
   * A curried version of analyzeReadings.
   *
   * Currying transforms a function of two arguments into a function of one
   * argument that returns another function. This allows partial application —
   * you can fix the analysis function first and get back a reusable transformer
   * that works on any list of EnergyReadings.
   *
   * Demonstrates:
   *   - Currying (multiple parameter lists)
   *   - Partial application
   *   - Higher-order functions
   *   - Type parameterization
   *
   * Example usage:
   *   val getMean  = StatisticsAnalysis.curriedAnalysis(mean)
   *   val getRange = StatisticsAnalysis.curriedAnalysis(range)
   *   val solarMean = getMean(solarReadings)   // reuse the same curried function
   *   val windRange = getRange(windReadings)
   *
   * @param analysisFunction the statistical function to apply (first argument)
   * @tparam A the return type of the analysis function
   * @return a function that takes a list of EnergyReadings and produces a RepsResult[A]
   */
  def curriedAnalysis[A](
    analysisFunction: List[Double] => RepsResult[A]
  )(readings: List[EnergyReading]): RepsResult[A] =
    analyzeReadings(readings, analysisFunction)
