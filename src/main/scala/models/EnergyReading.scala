// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : models/EnergyReading.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Immutable case class representing a single energy reading
//              from one of the plant's renewable sources (Solar/Wind/Hydro).
//              Designed to map directly to one row of the Fingrid CSV data.
// ============================================================================

package models

import java.time.LocalDateTime

/**
 * A single, immutable energy production reading.
 *
 * All fields are vals (immutable by default in case classes).
 * The class deliberately has NO mutable state.
 *
 * @param source    the type of renewable energy source (Solar, Wind, or Hydro)
 * @param startTime the beginning of the measurement interval (ISO-8601)
 * @param endTime   the end of the measurement interval (ISO-8601)
 * @param energyMW  the energy produced during the interval, in megawatts (MW)
 */
case class EnergyReading(
  source: EnergySource,
  startTime: LocalDateTime,
  endTime: LocalDateTime,
  energyMW: Double
)
