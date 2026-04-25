name := "REPS"

version := "0.1.0-SNAPSHOT"

// Using a stable version of Scala 3. You can change this to "2.13.12" if your course requires Scala 2.
scalaVersion := "3.3.1"

// Basic library dependencies
libraryDependencies ++= Seq(
  // ScalaTest is the standard testing library for Scala (optional but recommended for your analysis functions)
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)

// Enforce strict compilation warnings to help catch non-functional code or errors early
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked"
)