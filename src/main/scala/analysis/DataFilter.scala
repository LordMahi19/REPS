// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : analysis/DataFilter.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Pure functions for filtering, sorting, and searching energy
//              readings by time period (hourly, daily, weekly, monthly).
// ============================================================================

package analysis

import models.{EnergyReading, EnergySource}
import java.time.LocalDateTime

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
   *
   * @param readings the full list of energy readings
   * @param date     the target date
   * @param hour     the target hour (0-23)
   * @return a filtered list of readings matching the criteria
   */
  def filterByHour(readings: List[EnergyReading], date: LocalDateTime, hour: Int): List[EnergyReading] =
    // TODO: Oyshe — implement using higher-order functions (filter)
    Nil

  /**
   * Filters readings that fall within a specific day.
   *
   * @param readings the full list of energy readings
   * @param year     the target year
   * @param month    the target month (1-12)
   * @param day      the target day (1-31)
   * @return a filtered list of readings for that day
   */
  def filterByDay(readings: List[EnergyReading], year: Int, month: Int, day: Int): List[EnergyReading] =
    // TODO: Oyshe — implement
    Nil

  /**
   * Filters readings for a specific week of the year.
   */
  def filterByWeek(readings: List[EnergyReading], year: Int, week: Int): List[EnergyReading] =
    // TODO: Oyshe — implement
    Nil

  /**
   * Filters readings for a specific month.
   */
  def filterByMonth(readings: List[EnergyReading], year: Int, month: Int): List[EnergyReading] =
    // TODO: Oyshe — implement
    Nil

  // -------------------------------------------------------------------------
  // Filtering by energy source type
  // -------------------------------------------------------------------------

  /**
   * Filters readings by energy source type (Solar, Wind, or Hydro).
   */
  def filterBySource(readings: List[EnergyReading], source: EnergySource): List[EnergyReading] =
    readings.filter(_.source == source)

  // -------------------------------------------------------------------------
  // Sorting
  // -------------------------------------------------------------------------

  /**
   * Sorts readings by their start time in ascending order.
   */
  def sortByTime(readings: List[EnergyReading]): List[EnergyReading] =
    // TODO: Oyshe — implement (may use a recursive sort or built-in sortBy)
    readings

  /**
   * Sorts readings by energy output (MW) in descending order.
   */
  def sortByEnergyDesc(readings: List[EnergyReading]): List[EnergyReading] =
    // TODO: Oyshe — implement
    readings

  // -------------------------------------------------------------------------
  // Searching
  // -------------------------------------------------------------------------

  /**
   * Searches for readings that match a given predicate.
   * Demonstrates higher-order functions.
   *
   * @param readings  the list to search
   * @param predicate a function defining the search criteria
   * @return all readings matching the predicate
   */
  def search(readings: List[EnergyReading], predicate: EnergyReading => Boolean): List[EnergyReading] =
    readings.filter(predicate)
