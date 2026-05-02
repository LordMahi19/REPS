// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : Main.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Entry point and interactive CLI menu for the REPS application.
//
//   The menu covers all five use cases from the project specification:
//     UC1 — Monitor & control renewable energy sources (view per-source summary)
//     UC2 — Data is loaded from CSV files (I/O handled by FileIO)
//     UC3 — View energy generation capacity (summary table + per-source display)
//     UC4 — Analyze, filter, sort, and search data (statistics + filtering)
//     UC5 — Detect and display alerts for equipment issues
//
// FP concepts shown here:
//   - Pattern matching  : menu dispatch, RepsResult handling
//   - Immutability      : all loaded data is stored as immutable List[EnergyReading]
//   - Higher-order fns  : curriedAnalysis used to partially apply stat functions
//   - Currying          : curriedMean, curriedRange, etc. are partially applied
// ============================================================================

import models.*
import analysis.*
import alerts.*
import io.*
import utils.*
import java.time.LocalDateTime
import scala.io.StdIn.readLine

/**
 * Main entry point for the Renewable Energy Plant System.
 *
 * Loads energy data from CSV files, then presents an interactive
 * text-based menu to the operator for monitoring, analysis, and alerts.
 */
@main def reps(): Unit =

  // ---- Banner ---------------------------------------------------------------
  printBanner()

  // ---- Load data (UC2: data collected from CSV files) ----------------------
  // Note: FileIO.readCsv is Oyshe's task. Until that is implemented, the
  //       application gracefully handles the Failure and continues with an
  //       empty dataset, so the menu still works.
  println("Loading data from CSV files...")
  val solarData  = FileIO.readCsv("data/solar-power-generation-forecast-updated-every-15-minutes.csv", EnergySource.Solar)
  val windData   = FileIO.readCsv("data/wind-power-production-real-time-data.csv",  EnergySource.Wind)
  val hydroData  = FileIO.readCsv("data/hydro-power-produciton-real-time-data.csv", EnergySource.Hydro)

  // Combine into a single immutable list, logging any load errors
  val solar  = solarData.fold(err => { println(s"  [Warning] Solar  : $err"); Nil }, identity)
  val wind   = windData.fold(err  => { println(s"  [Warning] Wind   : $err"); Nil }, identity)
  val hydro  = hydroData.fold(err => { println(s"  [Warning] Hydro  : $err"); Nil }, identity)

  val allReadings: List[EnergyReading] = solar ++ wind ++ hydro

  if allReadings.isEmpty then
    println("  Note: No data loaded yet — CSV reading is not yet implemented (Oyshe's task).")
    println("  The menu is active and all analysis functions are ready.")
  else
    println(s"  Loaded ${allReadings.size} total readings.")
  println()

  // ---- Main menu loop -------------------------------------------------------
  mainMenu(allReadings, solar, wind, hydro)


// =============================================================================
// Menu helpers
// =============================================================================

/**
 * Displays the main interactive menu and routes user choices.
 * Uses pattern matching on the user's input string for clean dispatch.
 *
 * @param all   all readings combined (Solar + Wind + Hydro)
 * @param solar solar-only readings
 * @param wind  wind-only readings
 * @param hydro hydro-only readings
 */
def mainMenu(
  all:   List[EnergyReading],
  solar: List[EnergyReading],
  wind:  List[EnergyReading],
  hydro: List[EnergyReading]
): Unit =
  printMenuOptions()
  val choice = readLine("  Enter choice: ").trim

  choice match
    case "1" =>
      // UC1 / UC3: Monitor sources & view energy generation summary
      viewEnergySummary(solar, wind, hydro)
      mainMenu(all, solar, wind, hydro)

    case "2" =>
      // UC4: Statistical analysis
      analysisMenu(all, solar, wind, hydro)
      mainMenu(all, solar, wind, hydro)

    case "3" =>
      // UC4: Filter by time period
      filterMenu(all, solar, wind, hydro)
      mainMenu(all, solar, wind, hydro)

    case "4" =>
      // UC4: Search data
      searchMenu(all)
      mainMenu(all, solar, wind, hydro)

    case "5" =>
      // UC5: Alert detection
      alertMenu(all)
      mainMenu(all, solar, wind, hydro)

    case "0" =>
      println()
      println("  Shutting down REPS. Goodbye.")
      println("=" * 60)

    case _ =>
      println("  Invalid choice. Please enter a number between 0 and 5.")
      mainMenu(all, solar, wind, hydro)


// =============================================================================
// UC1 / UC3 — View energy generation summary
// =============================================================================

/**
 * Displays a summary view of energy generation per source (UC1 & UC3).
 * Shows count of readings, plus mean, min, and max MW for each source.
 * Demonstrates: higher-order functions via curriedAnalysis partial application.
 *
 * @param solar solar readings
 * @param wind  wind readings
 * @param hydro hydro readings
 */
def viewEnergySummary(
  solar: List[EnergyReading],
  wind:  List[EnergyReading],
  hydro: List[EnergyReading]
): Unit =
  println()
  println("=" * 60)
  println("  ENERGY GENERATION SUMMARY (UC1 / UC3)")
  println("=" * 60)

  // Currying in action: partially apply mean and range to get reusable functions
  // The type annotation makes the FP concept explicit.
  val getMean: List[EnergyReading] => RepsResult[Double] =
    StatisticsAnalysis.curriedAnalysis(StatisticsAnalysis.mean)
  val getMidrange: List[EnergyReading] => RepsResult[Double] =
    StatisticsAnalysis.curriedAnalysis(StatisticsAnalysis.midrange)
  val getRange: List[EnergyReading] => RepsResult[Double] =
    StatisticsAnalysis.curriedAnalysis(StatisticsAnalysis.range)

  printSourceSummary("Solar",  solar, getMean, getMidrange, getRange)
  printSourceSummary("Wind",   wind,  getMean, getMidrange, getRange)
  printSourceSummary("Hydro",  hydro, getMean, getMidrange, getRange)
  println()

/**
 * Prints the summary row for one energy source.
 *
 * @param name       display name of the source
 * @param readings   readings for this source
 * @param getMean    curried mean function
 * @param getMidrange curried midrange function
 * @param getRange   curried range function
 */
def printSourceSummary(
  name: String,
  readings: List[EnergyReading],
  getMean: List[EnergyReading] => RepsResult[Double],
  getMidrange: List[EnergyReading] => RepsResult[Double],
  getRange: List[EnergyReading] => RepsResult[Double]
): Unit =
  println(s"\n  [$name]")
  if readings.isEmpty then
    println("    No data loaded.")
  else
    println(s"    Readings : ${readings.size}")
    println(s"    Mean     : ${formatResult(getMean(readings))} MW")
    println(s"    Midrange : ${formatResult(getMidrange(readings))} MW")
    println(s"    Range    : ${formatResult(getRange(readings))} MW")


// =============================================================================
// UC4 — Statistical analysis menu
// =============================================================================

/**
 * Submenu for statistical analysis (mean, median, mode, range, midrange).
 * Allows the operator to choose a data source and a statistic.
 *
 * @param all   all readings combined
 * @param solar solar readings
 * @param wind  wind readings
 * @param hydro hydro readings
 */
def analysisMenu(
  all:   List[EnergyReading],
  solar: List[EnergyReading],
  wind:  List[EnergyReading],
  hydro: List[EnergyReading]
): Unit =
  println()
  println("  --- Statistical Analysis (UC4) ---")
  println("  Select data source:")
  println("    1) All sources combined")
  println("    2) Solar only")
  println("    3) Wind only")
  println("    4) Hydro only")
  val srcChoice = readLine("  Source: ").trim
  val readings = srcChoice match
    case "1" => all
    case "2" => solar
    case "3" => wind
    case "4" => hydro
    case _   => println("  Invalid choice, using all sources."); all

  println()
  println("  Select statistic:")
  println("    1) Mean")
  println("    2) Median")
  println("    3) Mode")
  println("    4) Range")
  println("    5) Midrange")
  println("    6) All statistics")
  val statChoice = readLine("  Statistic: ").trim

  val values = readings.map(_.energyMW)

  statChoice match
    case "1" => printStat("Mean",     StatisticsAnalysis.mean(values))
    case "2" => printStat("Median",   StatisticsAnalysis.median(values))
    case "3" => printStat("Mode",     StatisticsAnalysis.mode(values))
    case "4" => printStat("Range",    StatisticsAnalysis.range(values))
    case "5" => printStat("Midrange", StatisticsAnalysis.midrange(values))
    case "6" =>
      printStat("Mean",     StatisticsAnalysis.mean(values))
      printStat("Median",   StatisticsAnalysis.median(values))
      printStat("Mode",     StatisticsAnalysis.mode(values))
      printStat("Range",    StatisticsAnalysis.range(values))
      printStat("Midrange", StatisticsAnalysis.midrange(values))
    case _ => println("  Invalid statistic choice.")
  println()


// =============================================================================
// UC4 — Filter by time period
// =============================================================================

/**
 * Submenu for filtering energy readings by time period.
 * Validates user date input using validateDateFormat before filtering.
 *
 * @param all   all readings
 * @param solar solar readings
 * @param wind  wind readings
 * @param hydro hydro readings
 */
def filterMenu(
  all:   List[EnergyReading],
  solar: List[EnergyReading],
  wind:  List[EnergyReading],
  hydro: List[EnergyReading]
): Unit =
  println()
  println("  --- Filter by Time Period (UC4) ---")
  println("  Filter by:")
  println("    1) Day   (enter DD/MM/YYYY)")
  println("    2) Month (enter MM/YYYY)")
  val filterChoice = readLine("  Filter type: ").trim

  filterChoice match
    case "1" =>
      val dateInput = readLine("  Enter date (DD/MM/YYYY): ").trim
      // Demonstrate functional error handling via RepsResult
      RepsResult.validateDateFormat(dateInput) match
        case Success((day, month, year)) =>
          val filtered = DataFilter.filterByDay(all, year, month, day)
          RepsResult.requireNonEmpty(filtered, s"data for $dateInput") match
            case Success(results) =>
              println(s"\n  Found ${results.size} readings for $dateInput:")
              results.take(10).foreach(r => println(s"    ${r.startTime} | ${r.source} | ${r.energyMW} MW"))
              if results.size > 10 then println(s"    ... and ${results.size - 10} more.")
            case Failure(msg) =>
              println(s"  $msg")
        case Failure(msg) =>
          println(s"  Error: $msg")

    case "2" =>
      val input = readLine("  Enter month/year (MM/YYYY): ").trim.split("/")
      if input.length == 2 then
        RepsResult.attempt { (input(0).toInt, input(1).toInt) } match
          case Success((month, year)) =>
            val filtered = DataFilter.filterByMonth(all, year, month)
            RepsResult.requireNonEmpty(filtered, s"data for month $month/$year") match
              case Success(results) =>
                println(s"  Found ${results.size} readings for $month/$year.")
              case Failure(msg) =>
                println(s"  $msg")
          case Failure(msg) =>
            println(s"  Error parsing input: $msg")
      else
        println("  Invalid format. Please enter MM/YYYY.")

    case _ => println("  Invalid filter choice.")
  println()


// =============================================================================
// UC4 — Search
// =============================================================================

/**
 * Submenu allowing the operator to search readings by energy threshold or source.
 *
 * @param all all combined readings
 */
def searchMenu(all: List[EnergyReading]): Unit =
  println()
  println("  --- Search Data (UC4) ---")
  println("  Search by:")
  println("    1) Energy output above a threshold (MW)")
  println("    2) Energy source type")
  val searchChoice = readLine("  Search type: ").trim

  searchChoice match
    case "1" =>
      val thresholdInput = readLine("  Enter MW threshold: ").trim
      RepsResult.attempt(thresholdInput.toDouble) match
        case Success(threshold) =>
          // Higher-order function: search uses a predicate function
          val results = DataFilter.search(all, _.energyMW >= threshold)
          println(s"  Found ${results.size} readings with output >= $threshold MW.")
          results.take(5).foreach(r => println(s"    ${r.startTime} | ${r.source} | ${r.energyMW} MW"))
          if results.size > 5 then println(s"    ... and ${results.size - 5} more.")
        case Failure(_) =>
          println(s"  Invalid number: '$thresholdInput'. Please enter a numeric value.")

    case "2" =>
      println("  Enter source (solar / wind / hydro):")
      val srcInput = readLine("  Source: ").trim
      EnergySource.fromString(srcInput) match
        case Some(source) =>
          val results = DataFilter.filterBySource(all, source)
          println(s"  Found ${results.size} readings for $source.")
        case None =>
          println(s"  Unknown source '$srcInput'. Use: solar, wind, or hydro.")

    case _ => println("  Invalid search choice.")
  println()


// =============================================================================
// UC5 — Alert detection
// =============================================================================

/**
 * Displays all alerts detected in the current dataset.
 * Calls AlertSystem.detectIssues (Nguyen's implementation).
 *
 * @param all all combined readings
 */
def alertMenu(all: List[EnergyReading]): Unit =
  println()
  println("  --- Alert Detection (UC5) ---")
  if all.isEmpty then
    println("  No data loaded. Load CSV data first.")
  else
    val thresholdInput = readLine("  Enter low-output alert threshold in MW (default 10): ").trim
    val threshold = RepsResult.attempt(thresholdInput.toDouble).getOrElse(10.0)
    val alerts = AlertSystem.detectIssues(all, threshold)
    if alerts.isEmpty then
      println("  No alerts detected. All systems are operating normally.")
    else
      println(s"  ${alerts.size} alert(s) detected:")
      AlertSystem.formatAlerts(alerts).foreach(a => println(s"    $a"))
  println()


// =============================================================================
// Utility display helpers
// =============================================================================

/** Prints the welcome banner. */
def printBanner(): Unit =
  println("=" * 60)
  println("  Renewable Energy Plant System (REPS)")
  println("  Functional Programming - LUT University")
  println("  Team: Mahi | Oyshe | Nguyen")
  println("=" * 60)
  println()

/** Prints the main menu options. */
def printMenuOptions(): Unit =
  println()
  println("  +-------------------------------------------+")
  println("  |           REPS - Main Menu                |")
  println("  +-------------------------------------------+")
  println("  |  1. View energy generation summary        |")
  println("  |  2. Statistical analysis                  |")
  println("  |  3. Filter data by time period            |")
  println("  |  4. Search data                           |")
  println("  |  5. Detect alerts & issues                |")
  println("  |  0. Exit                                  |")
  println("  +-------------------------------------------+")

/** Prints a single named statistic result. */
def printStat(name: String, result: RepsResult[Double]): Unit =
  result match
    case Success(value) => println(f"  $name%-10s: $value%.4f MW")
    case Failure(msg)   => println(s"  $name: Error - $msg")

/** Formats a RepsResult[Double] as a string for display. */
def formatResult(result: RepsResult[Double]): String =
  result match
    case Success(value) => f"$value%.4f"
    case Failure(msg)   => s"N/A ($msg)"
