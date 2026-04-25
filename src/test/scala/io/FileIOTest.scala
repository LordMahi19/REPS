// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : test/scala/io/FileIOTest.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Unit tests for the imperative CSV I/O module.
// ============================================================================

package io

import org.scalatest.funsuite.AnyFunSuite
import models.{EnergyReading, EnergySource}
import utils.*
import java.time.LocalDateTime
import java.io.{File, PrintWriter}

class FileIOTest extends AnyFunSuite:

  // -------------------------------------------------------------------------
  // Helper: create a temporary CSV file with Fingrid format
  // -------------------------------------------------------------------------
  private def createTempCsv(content: String): String =
    val tmpFile = File.createTempFile("reps_test_", ".csv")
    tmpFile.deleteOnExit()
    val writer = new PrintWriter(tmpFile)
    writer.print(content)
    writer.close()
    tmpFile.getAbsolutePath

  // -------------------------------------------------------------------------
  // readCsv tests
  // -------------------------------------------------------------------------
  test("readCsv should parse a valid Fingrid CSV with 3 rows"):
    val csv =
      """"startTime";"endTime";"Solar power generation forecast - updated every 15 minutes"
"2026-01-15T10:00:00.000Z";"2026-01-15T10:15:00.000Z";120
"2026-01-15T10:15:00.000Z";"2026-01-15T10:30:00.000Z";135
"2026-01-15T10:30:00.000Z";"2026-01-15T10:45:00.000Z";142"""
    val path = createTempCsv(csv)
    val result = FileIO.readCsv(path, EnergySource.Solar)
    assert(result.isSuccess)
    val readings = result.getOrElse(Nil)
    assert(readings.size == 3)
    assert(readings.head.source == EnergySource.Solar)
    assert(readings.head.energyMW == 120.0)
    assert(readings.last.energyMW == 142.0)

  test("readCsv should parse timestamps correctly"):
    val csv =
      """"startTime";"endTime";"Wind"
"2026-03-20T14:30:00.000Z";"2026-03-20T14:45:00.000Z";500"""
    val path = createTempCsv(csv)
    val result = FileIO.readCsv(path, EnergySource.Wind)
    val readings = result.getOrElse(Nil)
    assert(readings.size == 1)
    val r = readings.head
    assert(r.startTime.getYear == 2026)
    assert(r.startTime.getMonthValue == 3)
    assert(r.startTime.getDayOfMonth == 20)
    assert(r.startTime.getHour == 14)
    assert(r.startTime.getMinute == 30)

  test("readCsv should return Failure for non-existent file"):
    val result = FileIO.readCsv("non_existent_file.csv", EnergySource.Hydro)
    assert(result.isFailure)

  test("readCsv should handle empty data (header only)"):
    val csv = """"startTime";"endTime";"value"
"""
    val path = createTempCsv(csv)
    val result = FileIO.readCsv(path, EnergySource.Solar)
    assert(result.isSuccess)
    assert(result.getOrElse(List(null)).isEmpty)

  test("readCsv should skip malformed lines gracefully"):
    val csv =
      """"startTime";"endTime";"Hydro"
"2026-01-01T00:00:00.000Z";"2026-01-01T00:15:00.000Z";100
this line is garbage
"2026-01-01T00:15:00.000Z";"2026-01-01T00:30:00.000Z";200"""
    val path = createTempCsv(csv)
    val result = FileIO.readCsv(path, EnergySource.Hydro)
    assert(result.isSuccess)
    val readings = result.getOrElse(Nil)
    assert(readings.size == 2)

  // -------------------------------------------------------------------------
  // readCsv with real project data files
  // -------------------------------------------------------------------------
  test("readCsv should parse the actual solar CSV data file"):
    val path = "data/solar-power-generation-forecast-updated-every-15-minutes.csv"
    val file = new File(path)
    if file.exists() then
      val result = FileIO.readCsv(path, EnergySource.Solar)
      assert(result.isSuccess)
      val readings = result.getOrElse(Nil)
      assert(readings.nonEmpty, "Solar CSV file should contain data")
      assert(readings.head.source == EnergySource.Solar)
      assert(readings.head.energyMW >= 0.0, "MW values should be non-negative")

  test("readCsv should parse the actual wind CSV data file"):
    val path = "data/wind-power-production-real-time-data.csv"
    val file = new File(path)
    if file.exists() then
      val result = FileIO.readCsv(path, EnergySource.Wind)
      assert(result.isSuccess)
      val readings = result.getOrElse(Nil)
      assert(readings.nonEmpty, "Wind CSV file should contain data")

  test("readCsv should parse the actual hydro CSV data file"):
    val path = "data/hydro-power-produciton-real-time-data.csv"
    val file = new File(path)
    if file.exists() then
      val result = FileIO.readCsv(path, EnergySource.Hydro)
      assert(result.isSuccess)
      val readings = result.getOrElse(Nil)
      assert(readings.nonEmpty, "Hydro CSV file should contain data")

  // -------------------------------------------------------------------------
  // writeCsv tests
  // -------------------------------------------------------------------------
  test("writeCsv should write readings and readCsv should read them back"):
    val now = LocalDateTime.of(2026, 2, 15, 12, 0, 0)
    val later = LocalDateTime.of(2026, 2, 15, 12, 15, 0)
    val readings = List(
      EnergyReading(EnergySource.Solar, now, later, 100.0),
      EnergyReading(EnergySource.Solar, later, later.plusMinutes(15), 200.0)
    )

    val tmpFile = File.createTempFile("reps_write_test_", ".csv")
    tmpFile.deleteOnExit()
    val path = tmpFile.getAbsolutePath

    val writeResult = FileIO.writeCsv(path, readings)
    assert(writeResult.isSuccess)

    // Read it back and verify
    val readResult = FileIO.readCsv(path, EnergySource.Solar)
    assert(readResult.isSuccess)
    val readBack = readResult.getOrElse(Nil)
    assert(readBack.size == 2)
    assert(readBack.head.energyMW == 100.0)
    assert(readBack.last.energyMW == 200.0)
