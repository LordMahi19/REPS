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
import java.time.{LocalDateTime, ZonedDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.io.{PrintWriter, File}

/**
 * Object responsible for reading/writing energy data from/to CSV files.
 *
 * The Fingrid CSV format uses semicolons as delimiters:
 *   "startTime";"endTime";"<column name>"
 *   "2025-12-31T22:00:00.000Z";"2025-12-31T22:03:00.000Z";1698
 */
object FileIO:

  // ISO-8601 formatter used by Fingrid data (timestamps end with 'Z' for UTC)
  private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  /**
   * Removes surrounding double-quote characters from a field value.
   * Fingrid CSV fields are often quoted (e.g., "2025-12-31T22:00:00.000Z").
   *
   * @param field the raw field string from the CSV
   * @return the field with leading/trailing quotes stripped
   */
  private def stripQuotes(field: String): String =
    field.trim.stripPrefix("\"").stripSuffix("\"")

  /**
   * Parses an ISO-8601 UTC timestamp string into a LocalDateTime.
   * The Fingrid timestamps look like "2025-12-31T22:00:00.000Z".
   * We parse as ZonedDateTime first and then convert to LocalDateTime
   * (keeping the UTC time, since all data is stored in UTC).
   *
   * @param timestamp the ISO-8601 timestamp string (e.g., "2025-12-31T22:00:00.000Z")
   * @return a LocalDateTime representing the same instant in UTC
   */
  private def parseTimestamp(timestamp: String): LocalDateTime =
    val cleaned = stripQuotes(timestamp)
    ZonedDateTime.parse(cleaned, isoFormatter).toLocalDateTime

  /**
   * Reads a CSV file and parses each row into an EnergyReading.
   *
   * This method is intentionally imperative (uses var, try-catch, etc.)
   * as permitted by the project specification for I/O operations.
   *
   * Steps:
   *   1. Open the file using scala.io.Source
   *   2. Skip the header line
   *   3. Parse each row (semicolon-delimited, quoted fields)
   *   4. Convert timestamps from ISO-8601 (Z) to LocalDateTime
   *   5. Build EnergyReading objects
   *   6. Return RepsResult.success(readings) or RepsResult.failure(errorMsg)
   *
   * @param filePath the path to the CSV file
   * @param source   the energy source type for all readings in this file
   * @return a RepsResult containing the list of parsed readings, or an error
   */
  def readCsv(filePath: String, source: EnergySource): RepsResult[List[EnergyReading]] =
    // Imperative I/O: use var for accumulation and try-catch for error handling
    var bufferedSource: scala.io.BufferedSource = null
    try
      bufferedSource = scala.io.Source.fromFile(filePath)
      val lines = bufferedSource.getLines()

      // Skip the header line (e.g., "startTime";"endTime";"Solar power ...")
      if lines.hasNext then lines.next()

      // Accumulator for the parsed readings (imperative: using var)
      var readings: List[EnergyReading] = List.empty
      var lineNumber = 1 // for error reporting (1 = first data line after header)
      var skippedLines = 0

      // Iterate through each data line
      while lines.hasNext do
        val line = lines.next().trim
        lineNumber += 1

        if line.nonEmpty then
          // Split by semicolons — expected format: "startTime";"endTime";value
          val fields = line.split(";")
          if fields.length >= 3 then
            try
              val startTime = parseTimestamp(fields(0))
              val endTime   = parseTimestamp(fields(1))
              val energyMW  = stripQuotes(fields(2)).toDouble

              val reading = EnergyReading(source, startTime, endTime, energyMW)
              readings = readings :+ reading
            catch
              case e: Exception =>
                // Skip malformed rows silently; count them for the summary
                skippedLines += 1
          else
            skippedLines += 1

      if skippedLines > 0 then
        println(s"  [Info] Skipped $skippedLines malformed line(s) in $filePath")

      RepsResult.success(readings)

    catch
      case e: java.io.FileNotFoundException =>
        RepsResult.failure(s"File not found: $filePath")
      case e: Exception =>
        RepsResult.failure(s"Error reading file '$filePath': ${e.getMessage}")
    finally
      // Always close the file resource (imperative cleanup)
      if bufferedSource != null then
        bufferedSource.close()

  /**
   * Writes a list of energy readings to a CSV file in the Fingrid format.
   *
   * The output format matches the input format:
   *   "startTime";"endTime";"Energy (MW)"
   *   "2025-12-31T22:00:00.000Z";"2025-12-31T22:03:00.000Z";1698.0
   *
   * @param filePath the output file path
   * @param readings the readings to write
   * @return RepsResult indicating success or failure
   */
  def writeCsv(filePath: String, readings: List[EnergyReading]): RepsResult[Unit] =
    var writer: PrintWriter = null
    try
      writer = new PrintWriter(new File(filePath))

      // Write the header line
      writer.println("\"startTime\";\"endTime\";\"Energy (MW)\"")

      // Write each reading as a CSV row (imperative: use a while-loop-style via foreach)
      var lineCount = 0
      readings.foreach { reading =>
        val startStr = reading.startTime.atOffset(ZoneOffset.UTC).format(isoFormatter)
        val endStr   = reading.endTime.atOffset(ZoneOffset.UTC).format(isoFormatter)
        writer.println(s"\"$startStr\";\"$endStr\";${reading.energyMW}")
        lineCount += 1
      }

      println(s"  [Info] Wrote $lineCount reading(s) to $filePath")
      RepsResult.success(())

    catch
      case e: Exception =>
        RepsResult.failure(s"Error writing file '$filePath': ${e.getMessage}")
    finally
      if writer != null then
        writer.close()
