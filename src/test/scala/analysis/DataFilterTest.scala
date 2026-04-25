// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : test/scala/analysis/DataFilterTest.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Unit tests for the data filtering, sorting, and search module.
// ============================================================================

package analysis

import org.scalatest.funsuite.AnyFunSuite
import models.{EnergyReading, EnergySource}
import java.time.LocalDateTime

class DataFilterTest extends AnyFunSuite:

  // -------------------------------------------------------------------------
  // Shared test data
  // -------------------------------------------------------------------------
  private val r1 = EnergyReading(
    EnergySource.Solar,
    LocalDateTime.of(2026, 1, 15, 10, 0),
    LocalDateTime.of(2026, 1, 15, 10, 15),
    100.0
  )
  private val r2 = EnergyReading(
    EnergySource.Wind,
    LocalDateTime.of(2026, 1, 15, 14, 0),
    LocalDateTime.of(2026, 1, 15, 14, 15),
    200.0
  )
  private val r3 = EnergyReading(
    EnergySource.Hydro,
    LocalDateTime.of(2026, 2, 20, 8, 0),
    LocalDateTime.of(2026, 2, 20, 8, 15),
    300.0
  )
  private val r4 = EnergyReading(
    EnergySource.Solar,
    LocalDateTime.of(2026, 1, 15, 10, 15),
    LocalDateTime.of(2026, 1, 15, 10, 30),
    50.0
  )
  private val allReadings = List(r1, r2, r3, r4)

  // -------------------------------------------------------------------------
  // filterByHour
  // -------------------------------------------------------------------------
  test("filterByHour should return readings matching the specific hour on a date"):
    val date = LocalDateTime.of(2026, 1, 15, 0, 0)
    val result = DataFilter.filterByHour(allReadings, date, 10)
    assert(result.size == 2)  // r1 and r4 are both at hour 10 on Jan 15
    assert(result.forall(_.startTime.getHour == 10))

  test("filterByHour should return empty list for non-matching hour"):
    val date = LocalDateTime.of(2026, 1, 15, 0, 0)
    val result = DataFilter.filterByHour(allReadings, date, 23)
    assert(result.isEmpty)

  // -------------------------------------------------------------------------
  // filterByDay
  // -------------------------------------------------------------------------
  test("filterByDay should return all readings for a given day"):
    val result = DataFilter.filterByDay(allReadings, 2026, 1, 15)
    assert(result.size == 3)  // r1, r2, r4

  test("filterByDay should return empty for a day with no data"):
    val result = DataFilter.filterByDay(allReadings, 2026, 6, 1)
    assert(result.isEmpty)

  // -------------------------------------------------------------------------
  // filterByWeek
  // -------------------------------------------------------------------------
  test("filterByWeek should return readings in the specified ISO week"):
    // Jan 15, 2026 falls in ISO week 3
    val result = DataFilter.filterByWeek(allReadings, 2026, 3)
    assert(result.nonEmpty)
    // Feb 20 is a different week; should not be included
    assert(result.forall(r => r.startTime.getMonthValue == 1))

  // -------------------------------------------------------------------------
  // filterByMonth
  // -------------------------------------------------------------------------
  test("filterByMonth should return readings in the specified month"):
    val janReadings = DataFilter.filterByMonth(allReadings, 2026, 1)
    assert(janReadings.size == 3)

    val febReadings = DataFilter.filterByMonth(allReadings, 2026, 2)
    assert(febReadings.size == 1)
    assert(febReadings.head == r3)

  test("filterByMonth should return empty for month with no data"):
    val result = DataFilter.filterByMonth(allReadings, 2026, 12)
    assert(result.isEmpty)

  // -------------------------------------------------------------------------
  // filterBySource
  // -------------------------------------------------------------------------
  test("filterBySource should return only readings of the given source type"):
    val solarReadings = DataFilter.filterBySource(allReadings, EnergySource.Solar)
    assert(solarReadings.size == 2)
    assert(solarReadings.forall(_.source == EnergySource.Solar))

    val hydroReadings = DataFilter.filterBySource(allReadings, EnergySource.Hydro)
    assert(hydroReadings.size == 1)

  // -------------------------------------------------------------------------
  // sortByTime
  // -------------------------------------------------------------------------
  test("sortByTime should sort readings in ascending chronological order"):
    val unsorted = List(r3, r1, r2, r4)
    val sorted = DataFilter.sortByTime(unsorted)
    // Expected order: r1 (Jan 15 10:00), r4 (Jan 15 10:15), r2 (Jan 15 14:00), r3 (Feb 20 08:00)
    assert(sorted(0) == r1)
    assert(sorted(1) == r4)
    assert(sorted(2) == r2)
    assert(sorted(3) == r3)

  test("sortByTime should handle an already-sorted list"):
    val sorted = DataFilter.sortByTime(List(r1, r4, r2, r3))
    assert(sorted == List(r1, r4, r2, r3))

  test("sortByTime should handle empty list"):
    assert(DataFilter.sortByTime(Nil).isEmpty)

  test("sortByTime should handle single element"):
    assert(DataFilter.sortByTime(List(r1)) == List(r1))

  // -------------------------------------------------------------------------
  // sortByEnergyDesc
  // -------------------------------------------------------------------------
  test("sortByEnergyDesc should sort readings by MW in descending order"):
    val sorted = DataFilter.sortByEnergyDesc(allReadings)
    assert(sorted(0).energyMW == 300.0)  // r3
    assert(sorted(1).energyMW == 200.0)  // r2
    assert(sorted(2).energyMW == 100.0)  // r1
    assert(sorted(3).energyMW == 50.0)   // r4

  test("sortByEnergyDesc should handle empty list"):
    assert(DataFilter.sortByEnergyDesc(Nil).isEmpty)

  // -------------------------------------------------------------------------
  // search (higher-order function)
  // -------------------------------------------------------------------------
  test("search should return readings matching the predicate"):
    val highOutput = DataFilter.search(allReadings, _.energyMW >= 200.0)
    assert(highOutput.size == 2)  // r2 (200) and r3 (300)

  test("search should return empty when no readings match"):
    val result = DataFilter.search(allReadings, _.energyMW > 1000.0)
    assert(result.isEmpty)

  test("search should return all readings when predicate always matches"):
    val result = DataFilter.search(allReadings, _ => true)
    assert(result.size == allReadings.size)
