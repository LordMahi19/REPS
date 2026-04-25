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
git clone <your-github-repo-url>
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

### Mahi (Core Architecture & Analysis)

- [ ] Define the immutable Scala data structures (`models/`).
    
- [ ] Implement mathematical analysis functions recursively (`analysis/`).
    
- [ ] Implement functional error-handling mechanisms.
    

### Oyshe (Data I/O, Filtering & Advanced Topic)

- [ ] Implement imperative file reading/writing for `.csv`/`.xls` (`io/`).
    
- [ ] Build hourly/daily/weekly/monthly sorting and filtering logic.
    
- [ ] Write the theoretical explanation and separate code for the Advanced Topic (Functor or Strictness/Laziness).
    
- [ ] **Submit** `GroupName_project.scala` to Moodle.
    

### Nguyen (Alerts, Diagrams & Final Submission)

- [ ] Implement the plant alert system (`alerts/`).
    
- [ ] Create all Sequence Flow Diagrams and the Class Diagram.
    
- [ ] Record the 5-minute system demonstration video and upload it to OneDrive.
    
- [ ] Assemble the Final Report (Diagrams + Theory + AI Declaration) into a PDF.
    
- [ ] **Submit** the final `GroupName_project.zip` to Moodle.
    

_(Reminder: Every team member must individually submit their Peer Evaluation Form to Moodle before the deadline!)_