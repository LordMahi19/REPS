// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : Main.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Entry point for the REPS application.
//              Provides a simple text-based menu for interacting with the
//              system: loading data, viewing statistics, checking alerts, etc.
// ============================================================================

import models.*
import analysis.*
import alerts.*
import io.*
import utils.*

/**
 * Main entry point for the Renewable Energy Plant System.
 *
 * This function will eventually:
 *   1. Load energy data from CSV files (Solar, Wind, Hydro).
 *   2. Present a menu to the operator.
 *   3. Allow filtering, analysis, and alert detection.
 */
@main def reps(): Unit =
  println("=" * 60)
  println("  Renewable Energy Plant System (REPS)")
  println("  Functional Programming — LUT University")
  println("=" * 60)
  println()
  println("  Energy Sources: Solar | Wind | Hydro")
  println("  Data Provider : Fingrid (fingrid.fi)")
  println()
  println("  Status: Project scaffolding complete.")
  println("  TODO  : Implement data loading, analysis, and alerts.")
  println()
  println("=" * 60)
