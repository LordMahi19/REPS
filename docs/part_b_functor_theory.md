# Part B — Advanced Topic: Functor

## Theoretical Explanation

### What Is a Functor?

In functional programming, a **Functor** is a type class that abstracts over type constructors (written as `F[_]`) which support a *mapping operation*. Informally, a Functor represents any "container" or "context" whose inner values can be transformed without altering the container's structure. The core operation is `map`:

```scala
trait Functor[F[_]]:
  def map[A, B](fa: F[A])(f: A => B): F[B]
```

Given a value of type `F[A]` and a function `A => B`, the `map` operation produces a new value of type `F[B]` by applying the function to every contained element while preserving the shape of `F`. For example, mapping a function over a `List` transforms each element but keeps the list's length and ordering; mapping over an `Option` transforms the contained value if present, or leaves `None` unchanged.

### Origin in Category Theory

The concept originates from **category theory**, where a functor is a mapping between two categories that preserves categorical structure — specifically, it maps objects to objects and morphisms (functions) to morphisms while respecting composition and identity. In programming, this mathematical foundation translates directly into two laws that every valid Functor implementation must satisfy.

### The Functor Laws

1. **Identity Law** — Mapping the identity function over a functor must return the original functor unchanged:

   ```
   fa.map(identity) == fa
   ```

   This ensures that `map` does not introduce unintended side effects or structural changes when the transformation is trivial.

2. **Composition Law** — Mapping two functions sequentially must be equivalent to mapping their composition in a single step:

   ```
   fa.map(f).map(g) == fa.map(f andThen g)
   ```

   This guarantees that the functor preserves the structure of function composition, allowing optimisations (fusing two traversals into one) without changing the result.

Together, these laws ensure that a Functor is a *well-behaved* abstraction: it faithfully transforms content without corrupting context.

### Implementation in Our Project

In our REPS implementation (`advanced_topic/Functor.scala`), we defined the Functor type class using Scala 3 traits and `given` instances, along with extension methods for ergonomic `.map(f)` syntax. We created four Functor instances to demonstrate versatility:

| Functor Instance | Description |
|------------------|-------------|
| **`Box[A]`** | The simplest possible functor — a single-value container (identity functor). |
| **`Pair[A]`** | A two-element container; `map` transforms both elements uniformly. |
| **`Tree[A]`** | A recursive binary tree ADT; `map` recursively transforms every leaf while preserving the branching structure. |
| **`EnergyData[A]`** | A REPS-specific container pairing a source name with a data value; `map` transforms the value while preserving the metadata. Demonstrates a practical use case (e.g., converting MW → kW → formatted strings). |

All four instances were verified against both Functor laws using assertions in the demonstration program.

### Why Functors Matter

Functors provide a uniform interface for transforming data across different structures. Instead of writing separate transformation logic for lists, trees, options, and domain-specific types, a single generic function parameterised by `Functor[F]` works across all of them. This promotes **code reuse**, **composability**, and **reasoning by laws** — three pillars of functional programming.

## References

1. Chiusano, P. & Bjarnason, R. (2014). *Functional Programming in Scala*. Manning Publications, Chapter 11 – Monads.

2. Scala 3 Documentation — Type Classes. https://docs.scala-lang.org/scala3/book/types-type-classes.html

3. Typelevel Cats — Functor. https://typelevel.org/cats/typeclasses/functor.html
