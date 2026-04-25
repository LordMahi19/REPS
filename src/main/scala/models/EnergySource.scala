// ============================================================================
// Project : Renewable Energy Plant System (REPS)
// File    : models/EnergySource.scala
// Authors : Mahi, Oyshe, Nguyen
// Description: Immutable sealed hierarchy representing the types of
//              renewable energy sources managed by the plant.
// ============================================================================

package models

/**
 * Sealed enumeration of supported renewable energy source types.
 *
 * Using a Scala 3 `enum` ensures:
 *   - Exhaustive pattern matching (compiler warns on missing cases).
 *   - Immutability by design — enum values are singleton objects.
 *   - Type-safe references throughout the codebase.
 */
enum EnergySource:
  case Solar
  case Wind
  case Hydro

object EnergySource:

  /**
   * Parses a string into an EnergySource, returning an Option.
   * Demonstrates functional error handling via Option instead of exceptions.
   *
   * @param name the string to parse (case-insensitive)
   * @return Some(source) if valid, None otherwise
   */
  def fromString(name: String): Option[EnergySource] =
    name.trim.toLowerCase match
      case "solar" => Some(EnergySource.Solar)
      case "wind"  => Some(EnergySource.Wind)
      case "hydro" => Some(EnergySource.Hydro)
      case _       => None
