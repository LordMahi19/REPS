// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : analysis/DataFilter.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Pure functions for filtering, sorting, and searching energy
//              readings by time period (hourly, daily, weekly, monthly).
//
// FP concepts demonstrated:
//   - Higher-order functions: filter, sortBy, and the generic search function
//   - Immutability: all functions return new lists; no mutation
//   - Pattern matching: used in recursive merge sort
//   - Recursion: merge sort implementation uses structural recursion
// ============================================================================

package analysis

import models.{EnergyReading, EnergySource}
import java.time.LocalDateTime
import java.time.temporal.IsoFields

/**
 * Object containing pure functions for data filtering and sorting.
 *
 * From the project specification (Use Case 4):
 *   - Filter data on an hourly, daily, weekly, and monthly basis.
 *   - Sort data where possible.
 *   - Search for required data stored in the system.
 */
object DataFilter:

  // -------------------------------------------------------------------------
  // Filtering by time period
  // -------------------------------------------------------------------------

  /**
   * Filters readings that fall within a specific hour of a given date.
   * A reading matches if its startTime falls on the given date and hour.
   *
   * Demonstrates: higher-order functions (filter with a predicate).
   *
   * @param readings the full list of energy readings
   * @param date     the target date (only year, month, day are used)
   * @param hour     the target hour (0-23)
   * @return a filtered list of readings matching the criteria
   */
  def filterByHour(readings: List[EnergyReading], date: LocalDateTime, hour: Int): List[EnergyReading] =
    readings.filter { reading =>
      val st = reading.startTime
      st.getYear == date.getYear &&
      st.getMonthValue == date.getMonthValue &&
      st.getDayOfMonth == date.getDayOfMonth &&
      st.getHour == hour
    }

  /**
   * Filters readings that fall within a specific day.
   * A reading matches if its startTime has the given year, month, and day.
   *
   * @param readings the full list of energy readings
   * @param year     the target year
   * @param month    the target month (1-12)
   * @param day      the target day (1-31)
   * @return a filtered list of readings for that day
   */
  def filterByDay(readings: List[EnergyReading], year: Int, month: Int, day: Int): List[EnergyReading] =
    readings.filter { reading =>
      val st = reading.startTime
      st.getYear == year &&
      st.getMonthValue == month &&
      st.getDayOfMonth == day
    }

  /**
   * Filters readings for a specific ISO week of the year.
   * Uses the ISO-8601 week-of-year definition (IsoFields.WEEK_OF_WEEK_BASED_YEAR).
   *
   * @param readings the full list of energy readings
   * @param year     the target year
   * @param week     the target ISO week number (1-53)
   * @return a filtered list of readings for that week
   */
  def filterByWeek(readings: List[EnergyReading], year: Int, week: Int): List[EnergyReading] =
    readings.filter { reading =>
      val st = reading.startTime
      st.getYear == year &&
      st.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week
    }

  /**
   * Filters readings for a specific month.
   * A reading matches if its startTime has the given year and month.
   *
   * @param readings the full list of energy readings
   * @param year     the target year
   * @param month    the target month (1-12)
   * @return a filtered list of readings for that month
   */
  def filterByMonth(readings: List[EnergyReading], year: Int, month: Int): List[EnergyReading] =
    readings.filter { reading =>
      val st = reading.startTime
      st.getYear == year &&
      st.getMonthValue == month
    }

  // -------------------------------------------------------------------------
  // Filtering by energy source type
  // -------------------------------------------------------------------------

  /**
   * Filters readings by energy source type (Solar, Wind, or Hydro).
   */
  def filterBySource(readings: List[EnergyReading], source: EnergySource): List[EnergyReading] =
    readings.filter(_.source == source)

  // -------------------------------------------------------------------------
  // Sorting — using recursive merge sort
  // -------------------------------------------------------------------------

  /**
   * Recursively splits a list into two halves.
   * This is a helper for merge sort.
   *
   * @param list the input list
   * @tparam A the element type
   * @return a tuple of two sublists (roughly equal halves)
   */
  private def split[A](list: List[A]): (List[A], List[A]) =
    val mid = list.length / 2
    (list.take(mid), list.drop(mid))

  /**
   * Recursively merges two sorted lists into one sorted list.
   * Demonstrates: recursion, pattern matching on lists.
   *
   * @param left    the first sorted list
   * @param right   the second sorted list
   * @param compare a comparison function (returns true if first < second)
   * @tparam A the element type
   * @return a merged sorted list
   */
  private def merge[A](left: List[A], right: List[A], compare: (A, A) => Boolean): List[A] =
    (left, right) match
      case (Nil, _) => right
      case (_, Nil) => left
      case (lHead :: lTail, rHead :: rTail) =>
        if compare(lHead, rHead) then
          lHead :: merge(lTail, right, compare)
        else
          rHead :: merge(left, rTail, compare)

  /**
   * Recursive merge sort implementation.
   * Demonstrates: recursion, pattern matching, higher-order functions.
   *
   * @param list    the list to sort
   * @param compare a comparison function (returns true if first should come before second)
   * @tparam A the element type
   * @return a new sorted list
   */
  private def mergeSort[A](list: List[A], compare: (A, A) => Boolean): List[A] =
    list match
      case Nil       => Nil
      case _ :: Nil  => list   // Single element is already sorted
      case _ =>
        val (left, right) = split(list)
        merge(mergeSort(left, compare), mergeSort(right, compare), compare)

  /**
   * Sorts readings by their start time in ascending order.
   * Uses recursive merge sort.
   *
   * @param readings the list to sort
   * @return a new list sorted by startTime ascending
   */
  def sortByTime(readings: List[EnergyReading]): List[EnergyReading] =
    mergeSort(readings, (a: EnergyReading, b: EnergyReading) =>
      a.startTime.isBefore(b.startTime)
    )

  /**
   * Sorts readings by energy output (MW) in descending order.
   * Uses recursive merge sort.
   *
   * @param readings the list to sort
   * @return a new list sorted by energyMW descending
   */
  def sortByEnergyDesc(readings: List[EnergyReading]): List[EnergyReading] =
    mergeSort(readings, (a: EnergyReading, b: EnergyReading) =>
      a.energyMW > b.energyMW
    )

  // -------------------------------------------------------------------------
  // Searching
  // -------------------------------------------------------------------------

  /**
   * Searches for readings that match a given predicate.
   * Demonstrates higher-order functions (takes a function as a parameter).
   *
   * @param readings  the list to search
   * @param predicate a function defining the search criteria
   * @return all readings matching the predicate
   */
  def search(readings: List[EnergyReading], predicate: EnergyReading => Boolean): List[EnergyReading] =
    readings.filter(predicate)
