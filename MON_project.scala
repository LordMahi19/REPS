// ============================================================================
// Project : Renewable Energy Plant System (REPS) — Part II: Advanced Topic
// File    : advanced_topic/Functor.scala
// Authors : Mahi, Oyshe, Nguyen
// Topic   : Functor
//
// Description:
//   A small but meaningful implementation of the Functor type class in Scala 3.
//
//   A Functor is a type class that abstracts over type constructors (F[_])
//   which support a mapping operation. In category theory, a functor is a
//   mapping between categories that preserves the structure (composition and
//   identity). In programming, this translates to the ability to transform
//   the contents of a container/context without changing the container's
//   structure.
//
//   The Functor type class must satisfy two laws:
//     1. Identity law   : fa.map(identity) == fa
//        Mapping the identity function over a functor should produce the
//        same functor.
//     2. Composition law: fa.map(f).map(g) == fa.map(f andThen g)
//        Mapping f and then g is the same as mapping the composition of
//        f and g. This ensures that structure is preserved across
//        sequential transformations.
//
//   This file demonstrates:
//     - Trait-based type class definition using Scala 3 syntax
//     - Type parameterization (higher-kinded types: F[_])
//     - Extension methods for ergonomic syntax
//     - Multiple Functor instances (Box, Pair, custom Tree data type)
//     - Practical use case: Functor for energy data transformation
//     - Law verification via assertions
//
// References:
//   - Chiusano, P. & Bjarnason, R. (2014). "Functional Programming in Scala."
//     Manning Publications, Chapter 11 – Monads.
//   - Scala 3 documentation on Type Classes:
//     https://docs.scala-lang.org/scala3/book/types-type-classes.html
//   - Typelevel Cats library Functor documentation:
//     https://typelevel.org/cats/typeclasses/functor.html
// ============================================================================

// ---- Functor Type Class Definition ----------------------------------------

/**
 * The Functor type class.
 *
 * A Functor provides the ability to map a function over a value in a context F[_],
 * transforming the inner value while preserving the outer structure.
 *
 * @tparam F a type constructor (e.g., List, Option, Box) that takes one type parameter
 */
trait Functor[F[_]]:

  /**
   * Applies a function to the value(s) inside the functor, producing a new functor
   * with the transformed value(s).
   *
   * @param fa the functor containing value(s) of type A
   * @param f  the transformation function from A to B
   * @tparam A the input type
   * @tparam B the output type
   * @return a new functor containing value(s) of type B
   */
  def map[A, B](fa: F[A])(f: A => B): F[B]

// ---- Extension methods for nicer syntax -----------------------------------

/**
 * Extension methods that allow calling .map directly on any type
 * that has a Functor instance in scope (e.g., box.map(f) instead of
 * Functor[Box].map(box)(f)).
 *
 * Demonstrates: Scala 3 extension methods + context parameters (using keyword).
 */
extension [F[_], A](fa: F[A])
  def map[B](f: A => B)(using functor: Functor[F]): F[B] =
    functor.map(fa)(f)


// ============================================================================
// Functor Instances — concrete implementations for specific types
// ============================================================================

// ---- 1. Box: the simplest possible functor --------------------------------

/**
 * A simple container holding exactly one value.
 * This is the "identity functor" — the simplest possible functor.
 *
 * @param value the contained value
 * @tparam A the type of the contained value
 */
case class Box[A](value: A)

/**
 * Functor instance for Box.
 * Mapping over a Box applies the function to its single contained value.
 */
given boxFunctor: Functor[Box] with
  def map[A, B](fa: Box[A])(f: A => B): Box[B] =
    Box(f(fa.value))


// ---- 2. Pair: a functor that maps over both elements ----------------------

/**
 * A container holding exactly two values of the same type.
 * Demonstrates that Functor can apply to types with multiple elements.
 *
 * @param first  the first element
 * @param second the second element
 * @tparam A the type of both elements
 */
case class Pair[A](first: A, second: A)

/**
 * Functor instance for Pair.
 * Maps the function over both elements, preserving the pair structure.
 */
given pairFunctor: Functor[Pair] with
  def map[A, B](fa: Pair[A])(f: A => B): Pair[B] =
    Pair(f(fa.first), f(fa.second))


// ---- 3. Tree: a recursive algebraic data type functor ---------------------

/**
 * A binary tree ADT. Demonstrates Functor for a recursive data structure.
 *
 * A Tree[A] is either:
 *   - Leaf(value: A)              — a terminal node containing a value
 *   - Branch(left, right)         — an internal node with two subtrees
 */
enum Tree[+A]:
  case Leaf(value: A)
  case Branch(left: Tree[A], right: Tree[A])

/**
 * Functor instance for Tree.
 * Recursively maps the function over every Leaf in the tree,
 * preserving the branching structure.
 *
 * Demonstrates: recursion, pattern matching, higher-order functions.
 */
given treeFunctor: Functor[Tree] with
  def map[A, B](fa: Tree[A])(f: A => B): Tree[B] =
    fa match
      case Tree.Leaf(value)         => Tree.Leaf(f(value))
      case Tree.Branch(left, right) => Tree.Branch(map(left)(f), map(right)(f))


// ---- 4. Practical use case: EnergyData functor ----------------------------

/**
 * A simplified energy data container for demonstrating Functor in the
 * context of the REPS project. Holds a source name and a value of type A
 * (which could be raw MW, a processed statistic, a formatted string, etc.)
 *
 * @param sourceName the name of the energy source (e.g., "Solar", "Wind")
 * @param value      the data value associated with this source
 * @tparam A the type of the data value
 */
case class EnergyData[A](sourceName: String, value: A)

/**
 * Functor instance for EnergyData.
 * Maps the function over the value while preserving the source name.
 * This is useful for transforming energy readings through a pipeline
 * (e.g., raw MW → scaled kW → formatted string) without losing the
 * metadata (source name).
 */
given energyDataFunctor: Functor[EnergyData] with
  def map[A, B](fa: EnergyData[A])(f: A => B): EnergyData[B] =
    EnergyData(fa.sourceName, f(fa.value))


// ============================================================================
// Main — Demonstration and Law Verification
// ============================================================================

/**
 * Entry point that demonstrates Functor usage with all four instances
 * and verifies the two Functor laws (identity and composition).
 */
@main def functorDemo(): Unit =

  println("=" * 60)
  println("  Functor — Advanced Topic Demonstration")
  println("  Renewable Energy Plant System (REPS)")
  println("=" * 60)
  println()

  // ---- Box Functor ----
  println("--- 1. Box Functor ---")
  val intBox: Box[Int] = Box(42)
  val doubledBox: Box[Int] = intBox.map(_ * 2)
  val stringBox: Box[String] = intBox.map(n => s"Value is $n")
  println(s"  Original   : $intBox")
  println(s"  Doubled    : $doubledBox")
  println(s"  To String  : $stringBox")
  println()

  // ---- Pair Functor ----
  println("--- 2. Pair Functor ---")
  val temps: Pair[Double] = Pair(22.5, 18.3)
  val tempsInF: Pair[Double] = temps.map(c => c * 9.0 / 5.0 + 32.0)
  println(s"  Celsius    : $temps")
  println(s"  Fahrenheit : $tempsInF")
  println()

  // ---- Tree Functor ----
  println("--- 3. Tree Functor ---")
  val tree: Tree[Int] = Tree.Branch(
    Tree.Branch(Tree.Leaf(1), Tree.Leaf(2)),
    Tree.Leaf(3)
  )
  val treeSquared: Tree[Int] = tree.map(x => x * x)
  println(s"  Original   : $tree")
  println(s"  Squared    : $treeSquared")
  println()

  // ---- EnergyData Functor (REPS use case) ----
  println("--- 4. EnergyData Functor (REPS Use Case) ---")
  val solarMW: EnergyData[Double] = EnergyData("Solar", 150.0)
  val solarKW: EnergyData[Double] = solarMW.map(_ * 1000.0)
  val solarLabel: EnergyData[String] = solarMW.map(mw => f"$mw%.1f MW")
  println(s"  Raw (MW)   : $solarMW")
  println(s"  Converted  : $solarKW")
  println(s"  Labelled   : $solarLabel")
  println()

  // A pipeline of transformations on energy data
  println("  Pipeline: MW → kW → formatted string")
  val windMW: EnergyData[Double] = EnergyData("Wind", 320.5)
  val result: EnergyData[String] = windMW.map(_ * 1000.0).map(kw => f"$kw%.0f kW")
  println(s"  Input      : $windMW")
  println(s"  Output     : $result")
  println()

  // ---- Functor Law Verification ----
  println("--- Functor Law Verification ---")
  println()

  // --- Identity Law: fa.map(identity) == fa ---
  println("  Law 1: Identity — fa.map(identity) == fa")
  val identityTest = Box(99).map(identity)
  assert(identityTest == Box(99), "Identity law failed for Box!")
  println(s"    Box(99).map(identity) = $identityTest  ✓")

  val treeIdentity = tree.map(identity)
  assert(treeIdentity == tree, "Identity law failed for Tree!")
  println(s"    tree.map(identity) == tree  ✓")

  val pairIdentity = temps.map(identity)
  assert(pairIdentity == temps, "Identity law failed for Pair!")
  println(s"    pair.map(identity) == pair  ✓")

  val energyIdentity = solarMW.map(identity)
  assert(energyIdentity == solarMW, "Identity law failed for EnergyData!")
  println(s"    energyData.map(identity) == energyData  ✓")
  println()

  // --- Composition Law: fa.map(f).map(g) == fa.map(f andThen g) ---
  println("  Law 2: Composition — fa.map(f).map(g) == fa.map(f andThen g)")
  val f: Int => Int = _ + 10
  val g: Int => String = _.toString

  val composed1 = Box(5).map(f).map(g)
  val composed2 = Box(5).map(f andThen g)
  assert(composed1 == composed2, "Composition law failed for Box!")
  println(s"    Box(5).map(f).map(g) = $composed1")
  println(s"    Box(5).map(f andThen g) = $composed2  ✓")

  val fd: Double => Double = _ * 2.0
  val gd: Double => String = d => f"$d%.1f"

  val treeC1 = tree.map(x => x.toDouble).map(fd).map(gd)
  val treeC2 = tree.map(x => x.toDouble).map(fd andThen gd)
  assert(treeC1 == treeC2, "Composition law failed for Tree!")
  println(s"    tree composition law  ✓")
  println()

  println("  All Functor laws verified successfully!")
  println()
  println("=" * 60)
  println("  Demonstration complete.")
  println("=" * 60)
