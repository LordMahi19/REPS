# Renewable Energy Plant System (REPS)

**Course:** CT60A9602 Functional Programming - Blended teaching, Lahti  
**Deadline:** Sunday, May 3rd, 2026 at 23:59

## Team Members
* **Mahi**
* **Oyshe**
* **Nguyen**

## Project Overview
This project is a multi-generation renewable energy plant system that manages solar, wind, and hydropower energy production. The system reads and analyzes energy generation data, detects issues (like equipment malfunctions), and provides a robust alert system. 

The core logic is implemented in **Scala 3**, strictly adhering to Functional Programming paradigms:
* Immutable data structures
* Iteration via recursion
* Higher-order functions
* Functional error handling (custom `RepsResult[A]` ADT)

## Project Architecture
The codebase is structured to separate pure functional logic from imperative I/O operations:

```
REPS/
├── build.sbt                         # Scala 3.3.1, ScalaTest dependency
├── project/build.properties          # sbt version lock (1.9.7)
├── src/
│   ├── main/scala/
│   │   ├── Main.scala                # Application entry point (@main)
│   │   ├── models/                   # Immutable data structures
│   │   │   ├── EnergySource.scala    #   Sealed enum: Solar, Wind, Hydro
│   │   │   └── EnergyReading.scala   #   Case class for one data reading
│   │   ├── analysis/                 # Pure statistical analysis functions
│   │   │   ├── StatisticsAnalysis.scala  # Mean, Median, Mode, Range, Midrange
│   │   │   └── DataFilter.scala      #   Filtering, sorting, searching
│   │   ├── alerts/                   # Issue detection & alert system
│   │   │   └── AlertSystem.scala     #   Alert generation & formatting
│   │   ├── io/                       # Imperative file I/O (CSV)
│   │   │   └── FileIO.scala          #   CSV reading & writing
│   │   └── utils/                    # Cross-cutting utilities
│   │       └── ErrorHandler.scala    #   RepsResult[A] ADT & validation
│   └── test/scala/
│       ├── analysis/
│       │   └── StatisticsAnalysisTest.scala
│       └── utils/
│           └── ErrorHandlerTest.scala
├── data/                             # Fingrid energy datasets (.csv)
│   ├── solar-power-generation-forecast-updated-every-15-minutes.csv
│   ├── wind-power-production-real-time-data.csv
│   └── hydro-power-produciton-real-time-data.csv
├── docs/                             # Sequence & class diagrams
└── advanced_topic/                   # Part II: Functor or Laziness
```

## Data Format (Fingrid CSV)
All data files are downloaded from [Fingrid.fi](https://www.fingrid.fi/) and use **semicolons** as delimiters with quoted fields:

```csv
"startTime";"endTime";"<measurement name>"
"2025-12-31T22:00:00.000Z";"2025-12-31T22:03:00.000Z";1698
```

| Column | Description |
|--------|-------------|
| `startTime` | Start of the measurement interval (ISO-8601 UTC) |
| `endTime` | End of the measurement interval (ISO-8601 UTC) |
| Value column | Energy output in **MW** (megawatts) |

Data covers the period **2025-12-31** to **2026-04-24**.

## Setup Instructions

**Prerequisites:** You must have [Java (JDK 11 or higher)](https://adoptium.net/) and [sbt (Scala Build Tool)](https://www.scala-sbt.org/download.html) installed on your machine.

1. **Clone the repository:**
```bash
git clone https://github.com/LordMahi19/REPS.git
cd REPS
```
2. **Compile the project:**
```bash
sbt compile
```
3. **Run the project:**
```bash
sbt run
```
4. **Run tests:**
```bash
sbt test
```

## Functional Programming Concepts Used

| Concept | Where |
|---------|-------|
| **Immutable data** | `models/` — case classes, sealed enum |
| **Recursion** | `analysis/StatisticsAnalysis` — all stats computed recursively |
| **Higher-order functions** | `analysis/DataFilter.search`, `StatisticsAnalysis.analyzeReadings` |
| **Type parameterization** | `RepsResult[A]`, `analyzeReadings[A]` |
| **Pattern matching** | `RepsResult.map/flatMap/fold`, `EnergySource.fromString` |
| **ADT error handling** | `utils/ErrorHandler` — `RepsResult[A]` sealed trait |
| **Currying** | _(to be demonstrated in implementations)_ |

## Task Division Checklist

> **Point breakdown:** Part I: 32 pts (Code 20 + Video 5 + Report A 7) | Part II: 8 pts (Theory 3.5 + References 1 + Code 3.5) | **Total: 40 pts**

---

### Mahi (Core Architecture & Analysis) — *GitHub Codebase*

#### UC1 & UC4 — Models & Statistical Analysis
- [x] Define the immutable `EnergySource` sealed enum (`Solar`, `Wind`, `Hydro`) with safe `fromString` parser.
- [x] Define the immutable `EnergyReading` case class (startTime, endTime, energyMW).
- [x] Implement `mean` recursively in `StatisticsAnalysis.scala`.
- [x] Implement `median` recursively in `StatisticsAnalysis.scala`.
- [x] Implement `mode` recursively in `StatisticsAnalysis.scala`.
- [x] Implement `range` recursively in `StatisticsAnalysis.scala`.
- [x] Implement `midrange` recursively in `StatisticsAnalysis.scala`.

#### Functional Programming Paradigm (required for full marks)
- [x] Implement `RepsResult[A]` ADT with `map`, `flatMap`, `fold`, `getOrElse`, `attempt` (`utils/ErrorHandler.scala`).
- [x] Implement `validateDateFormat` — validate DD/MM/YYYY input with helpful error messages.
- [x] Implement at least one example of **currying** (`curriedAnalysis` in `StatisticsAnalysis.scala`).
- [x] Ensure **type parameterization** is demonstrated (`analyzeReadings[A]`, `curriedAnalysis[A]`, `RepsResult[A]`).

#### CLI / View (UC1 & UC3)
- [x] Build the interactive text-based **main menu** in `Main.scala`:
  - Load data (Solar / Wind / Hydro) from CSV.
  - Display a summary view of current energy generation per source (UC3).
  - Trigger analysis (mean, median, mode, range, midrange) from the menu.
  - Trigger alert check from the menu (calls `AlertSystem`).

#### Code Quality (mandatory deliverable)
- [x] Ensure **all source files** include a header comment listing all team member names (Mahi, Oyshe, Nguyen).
- [x] Ensure all functions and classes are well-commented with descriptions.


---

### Oyshe (Data I/O, Filtering & Search) — *GitHub Codebase*

#### UC2 — File I/O (imperative, as allowed by spec)
- [x] Implement `readCsv` in `FileIO.scala` — parse Fingrid semicolon-delimited CSV into `List[EnergyReading]`.
- [x] Implement `writeCsv` in `FileIO.scala` — write a list of readings back to a CSV file (UC2: storing data).

#### UC4 — Filtering, Sorting & Searching
- [x] Implement `filterByHour` in `DataFilter.scala`.
- [x] Implement `filterByDay` in `DataFilter.scala`.
- [x] Implement `filterByWeek` in `DataFilter.scala`.
- [x] Implement `filterByMonth` in `DataFilter.scala`.
- [x] Implement `sortByTime` (ascending) in `DataFilter.scala`.
- [x] Implement `sortByEnergyDesc` (descending by output) in `DataFilter.scala`.
- [x] Implement `search` with a user-facing interface — allow operators to search by date, source, or MW threshold.

#### Part II — Advanced Topic (Functor or Strictness/Laziness)
- [x] Choose one topic: **Functor** or **Strictness/Laziness** (pick one concept within Strictness/Laziness).
- [x] Write a **small but meaningful standalone Scala implementation** of the chosen topic in `advanced_topic/`.
- [x] Write the **Part B theory section** of the report (max 1 page): clear theoretical explanation + references.
- [x] **Export** the standalone implementation as a single file named `MON_project.scala`.
- [ ] **Submit** `MON_project.scala` to Moodle (only one group member submits).

#### Code Quality
- [x] Ensure all source files include the team member names header comment.

---

### Nguyen (Alerts, Diagrams & Final Submission) — *Report + Video + Submission*

#### UC5 — Alert System
- [x] Implement `detectIssues` in `AlertSystem.scala`:
  - Detect **low energy output** (below configurable MW threshold).
  - Detect **equipment malfunction** (e.g., consecutive zero readings).
  - Generate `Alert` objects with appropriate `AlertSeverity` (Info / Warning / Critical).
- [x] Implement `formatAlerts` — display alerts clearly to operators.
- [x] Ensure error handling uses `RepsResult` where applicable.

#### Report Part A — Diagrams (7 pts)
- [x] Draw **Sequence Diagram for UC1** — Monitor & control energy sources.
- [x] Draw **Sequence Diagram for UC2** — Collect & store energy data to file.
- [x] Draw **Sequence Diagram for UC3** — View energy generation & storage capacity.
- [x] Draw **Sequence Diagram for UC4** — Analyze, filter, sort & search data.
- [x] Draw **Sequence Diagram for UC5** — Detect issues & generate alerts.
- [x] Draw **Class Diagram** — showing connections between all entities (models, analysis, alerts, io, utils).
- [x] Use proper diagram tools (no paper/pen drawings).

#### Report Structure (mandatory for grading)
- [ ] **Title page** — project name, all team member names, group number.
- [ ] **Part A section** — all 5 sequence diagrams + class diagram.
- [ ] **Part B section** — Oyshe's advanced topic theory + references (max 1 page).
- [ ] **References list** — all sources used throughout the project.
- [ ] **AI Declaration** — list all AI tools used, where/how they were used. *(Mandatory — omitting this means the work will NOT be graded.)*
- [ ] Export report as PDF named `GroupName_report.pdf`.

#### Demonstration Video (5 pts)
- [ ] Record a **maximum 5-minute** demonstration of the running REPS system (all 5 use cases).
- [ ] Upload video to **OneDrive**.
- [ ] Create `GroupName_link.txt` containing the OneDrive video link.

#### Final Submission Package
- [ ] Compress `GroupName_report.pdf` + `GroupName_link.txt` into `GroupName_project.zip`.
- [ ] **Submit** `GroupName_project.zip` to Moodle (only one group member submits).
- [ ] **Push** final codebase to GitHub.

#### Code Quality
- [ ] Ensure all source files include the team member names header comment.

---

### Everyone — Individual Submissions

- [ ] **Mahi** — Submit own `GroupName_YourName_PeerEvaluation` form to Moodle by May 3rd 23:59.
- [ ] **Oyshe** — Submit own `GroupName_YourName_PeerEvaluation` form to Moodle by May 3rd 23:59.
- [ ] **Nguyen** — Submit own `GroupName_YourName_PeerEvaluation` form to Moodle by May 3rd 23:59.

> ⚠️ Peer evaluation affects **20% of your project points**. Every member must submit individually.