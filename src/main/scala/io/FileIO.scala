// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : io/FileIO.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Imperative file I/O operations for reading and writing CSV
//              data files. This is the ONE area where non-functional,
//              imperative code is explicitly allowed by the project spec.
//
// Note from project spec:
//   "Implement the file I/O in a non-functional way in Scala – the usual
//    way of imperative languages. Better to do I/O either with .xls or .csv
//    files; it is relatively easier."
// ============================================================================

package io

import models.{EnergyReading, EnergySource}
import utils.RepsResult
import java.time.LocalDateTime

/**
 * Object responsible for reading/writing energy data from/to CSV files.
 *
 * The Fingrid CSV format uses semicolons as delimiters:
 *   "startTime";"endTime";"<column name>"
 *   "2025-12-31T22:00:00.000Z";"2025-12-31T22:03:00.000Z";1698
 */
object FileIO:

  /**
   * Reads a CSV file and parses each row into an EnergyReading.
   *
   * This method is intentionally imperative (uses var, try-catch, etc.)
   * as permitted by the project specification for I/O operations.
   *
   * @param filePath the path to the CSV file
   * @param source   the energy source type for all readings in this file
   * @return a RepsResult containing the list of parsed readings, or an error
   */
  def readCsv(filePath: String, source: EnergySource): RepsResult[List[EnergyReading]] =
    // TODO: Oyshe — implement imperative CSV reading
    //   Steps:
    //   1. Open the file using scala.io.Source
    //   2. Skip the header line
    //   3. Parse each row (semicolon-delimited, quoted fields)
    //   4. Convert timestamps from ISO-8601 (Z) to LocalDateTime
    //   5. Build EnergyReading objects
    //   6. Return RepsResult.success(readings) or RepsResult.failure(errorMsg)
    RepsResult.failure("readCsv not yet implemented")

  /**
   * Writes a list of energy readings to a CSV file.
   *
   * @param filePath the output file path
   * @param readings the readings to write
   * @return RepsResult indicating success or failure
   */
  def writeCsv(filePath: String, readings: List[EnergyReading]): RepsResult[Unit] =
    // TODO: Oyshe — implement imperative CSV writing
    RepsResult.failure("writeCsv not yet implemented")
