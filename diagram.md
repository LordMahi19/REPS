# REPS — Sequence & Class Diagrams (Mermaid)

> **Project:** Renewable Energy Plant System (REPS)  
> **Authors:** Mahi, Oyshe, Nguyen  
> **Tool:** Mermaid (paste into [mermaid.live](https://mermaid.live) to render & export)

---

## UC1 — Monitor & Control Energy Sources

```mermaid
sequenceDiagram
    actor Operator
    participant Main
    participant FileIO
    participant CSV as CSV Files
    participant EnergyReading

    Operator->>Main: Start REPS application
    Main->>Main: printBanner()

    Note over Main,CSV: Load data for each source (Solar, Wind, Hydro)

    Main->>FileIO: readCsv("solar...csv", Solar)
    FileIO->>CSV: Open & read file
    CSV-->>FileIO: Raw CSV lines
    FileIO->>EnergyReading: Parse each row into EnergyReading
    FileIO-->>Main: RepsResult[List[EnergyReading]]

    Main->>FileIO: readCsv("wind...csv", Wind)
    FileIO->>CSV: Open & read file
    CSV-->>FileIO: Raw CSV lines
    FileIO->>EnergyReading: Parse each row into EnergyReading
    FileIO-->>Main: RepsResult[List[EnergyReading]]

    Main->>FileIO: readCsv("hydro...csv", Hydro)
    FileIO->>CSV: Open & read file
    CSV-->>FileIO: Raw CSV lines
    FileIO->>EnergyReading: Parse each row into EnergyReading
    FileIO-->>Main: RepsResult[List[EnergyReading]]

    Main->>Main: Combine into allReadings list
    Main->>Main: printMenuOptions()
    Main-->>Operator: Display main menu

    Operator->>Main: Select "1" (View Summary)
    Main->>Main: viewEnergySummary(solar, wind, hydro)
    Main-->>Operator: Display source status & reading counts
```

---

## UC2 — Collect & Store Energy Data to File

```mermaid
sequenceDiagram
    actor Operator
    participant Main
    participant FileIO
    participant CSV as CSV Files
    participant RepsResult
    participant EnergyReading

    Note over Operator,EnergyReading: Reading data from CSV (collect)

    Operator->>Main: Start application
    Main->>FileIO: readCsv(filePath, source)
    FileIO->>CSV: scala.io.Source.fromFile(filePath)

    alt File exists
        CSV-->>FileIO: BufferedSource (lines iterator)
        FileIO->>FileIO: Skip header line
        loop For each data line
            FileIO->>FileIO: Split by ";" delimiter
            FileIO->>FileIO: stripQuotes(fields)
            FileIO->>FileIO: parseTimestamp(startTime, endTime)
            FileIO->>EnergyReading: Create EnergyReading(source, start, end, mw)
        end
        FileIO->>RepsResult: RepsResult.success(readings)
        RepsResult-->>Main: Success(List[EnergyReading])
    else File not found
        FileIO->>RepsResult: RepsResult.failure("File not found")
        RepsResult-->>Main: Failure(errorMsg)
        Main-->>Operator: Display warning message
    end

    Note over Operator,EnergyReading: Writing data to CSV (store)

    Main->>FileIO: writeCsv(filePath, readings)
    FileIO->>CSV: new PrintWriter(filePath)
    FileIO->>CSV: Write header line
    loop For each EnergyReading
        FileIO->>FileIO: Format timestamps to ISO-8601
        FileIO->>CSV: Write "startTime";"endTime";energyMW
    end
    FileIO->>RepsResult: RepsResult.success(())
    RepsResult-->>Main: Success(())
    Main-->>Operator: Confirm data stored
```

---

## UC3 — View Energy Generation & Storage Capacity

```mermaid
sequenceDiagram
    actor Operator
    participant Main
    participant StatisticsAnalysis
    participant RepsResult

    Operator->>Main: Select "1" (View Summary)
    Main->>Main: viewEnergySummary(solar, wind, hydro)

    Note over Main,RepsResult: Currying in action — partial application

    Main->>StatisticsAnalysis: curriedAnalysis(mean) → getMean
    Main->>StatisticsAnalysis: curriedAnalysis(midrange) → getMidrange
    Main->>StatisticsAnalysis: curriedAnalysis(range) → getRange

    loop For each source [Solar, Wind, Hydro]
        Main->>Main: printSourceSummary(name, readings, getMean, getMidrange, getRange)

        alt Readings non-empty
            Main->>StatisticsAnalysis: getMean(readings)
            StatisticsAnalysis->>StatisticsAnalysis: Extract energyMW values
            StatisticsAnalysis->>StatisticsAnalysis: recursiveSum / recursiveCount
            StatisticsAnalysis-->>Main: Success(meanValue)

            Main->>StatisticsAnalysis: getMidrange(readings)
            StatisticsAnalysis->>StatisticsAnalysis: recursiveMin / recursiveMax
            StatisticsAnalysis-->>Main: Success(midrangeValue)

            Main->>StatisticsAnalysis: getRange(readings)
            StatisticsAnalysis->>StatisticsAnalysis: recursiveMax - recursiveMin
            StatisticsAnalysis-->>Main: Success(rangeValue)

            Main-->>Operator: Display readings count, mean, midrange, range
        else No data loaded
            Main-->>Operator: Display "No data loaded."
        end
    end
```

---

## UC4 — Analyze, Filter, Sort & Search Data

```mermaid
sequenceDiagram
    actor Operator
    participant Main
    participant StatisticsAnalysis
    participant DataFilter
    participant RepsResult

    Note over Operator,RepsResult: A — Statistical Analysis

    Operator->>Main: Select "2" (Analysis)
    Main-->>Operator: Prompt source selection
    Operator->>Main: Select source (All/Solar/Wind/Hydro)
    Main-->>Operator: Prompt statistic selection
    Operator->>Main: Select statistic (e.g. "6" for All)

    Main->>StatisticsAnalysis: mean(values)
    StatisticsAnalysis-->>Main: RepsResult[Double]
    Main->>StatisticsAnalysis: median(values)
    StatisticsAnalysis-->>Main: RepsResult[Double]
    Main->>StatisticsAnalysis: mode(values)
    StatisticsAnalysis-->>Main: RepsResult[Double]
    Main->>StatisticsAnalysis: range(values)
    StatisticsAnalysis-->>Main: RepsResult[Double]
    Main->>StatisticsAnalysis: midrange(values)
    StatisticsAnalysis-->>Main: RepsResult[Double]
    Main-->>Operator: Display all statistics

    Note over Operator,RepsResult: B — Filter by Time Period

    Operator->>Main: Select "3" (Filter)
    Main-->>Operator: Prompt filter type (Day / Month)
    Operator->>Main: Enter date "DD/MM/YYYY"

    Main->>RepsResult: validateDateFormat(dateStr)
    alt Valid format
        RepsResult-->>Main: Success((day, month, year))
        Main->>DataFilter: filterByDay(all, year, month, day)
        DataFilter-->>Main: List[EnergyReading]
        Main->>RepsResult: requireNonEmpty(filtered)
        alt Data found
            RepsResult-->>Main: Success(results)
            Main-->>Operator: Display filtered readings
        else No data
            RepsResult-->>Main: Failure(msg)
            Main-->>Operator: Display "No data" message
        end
    else Invalid format
        RepsResult-->>Main: Failure(errorMsg)
        Main-->>Operator: Display error with format example
    end

    Note over Operator,RepsResult: C — Search Data

    Operator->>Main: Select "4" (Search)
    Operator->>Main: Enter MW threshold
    Main->>RepsResult: attempt(thresholdInput.toDouble)

    alt Valid number
        RepsResult-->>Main: Success(threshold)
        Main->>DataFilter: search(all, _.energyMW >= threshold)
        DataFilter-->>Main: List[EnergyReading]
        Main-->>Operator: Display matching readings
    else Invalid input
        RepsResult-->>Main: Failure(msg)
        Main-->>Operator: Display "Invalid number" message
    end
```

---

## UC5 — Detect Issues & Generate Alerts

```mermaid
sequenceDiagram
    actor Operator
    participant Main
    participant AlertSystem
    participant RepsResult
    participant Alert

    Operator->>Main: Select "5" (Alerts)

    alt No data loaded
        Main-->>Operator: "No data loaded. Load CSV data first."
    else Data available
        Main-->>Operator: Prompt for low-output threshold
        Operator->>Main: Enter threshold (e.g. "10")

        Main->>RepsResult: attempt(thresholdInput.toDouble)
        RepsResult-->>Main: threshold (default 10.0 on failure)

        Main->>AlertSystem: detectIssues(allReadings, threshold)

        AlertSystem->>RepsResult: Validate non-empty readings
        RepsResult-->>AlertSystem: Success(data)

        Note over AlertSystem,Alert: Low Output Detection

        AlertSystem->>AlertSystem: detectLowOutput(data, threshold)
        loop For each reading with energyMW < threshold
            alt energyMW <= 0
                AlertSystem->>Alert: Create Alert(Critical, "Zero output...")
            else energyMW < 50% of threshold
                AlertSystem->>Alert: Create Alert(Warning, "Low output...")
            else energyMW < threshold
                AlertSystem->>Alert: Create Alert(Info, "Low output...")
            end
        end

        Note over AlertSystem,Alert: Equipment Malfunction Detection

        AlertSystem->>AlertSystem: detectEquipmentMalfunction(data)
        AlertSystem->>AlertSystem: groupBySource(readings) [tail-recursive]

        loop For each source (Solar, Wind, Hydro)
            AlertSystem->>AlertSystem: Sort readings by time
            AlertSystem->>AlertSystem: findConsecutiveZeroRuns [tail-recursive]
            alt 3+ consecutive zeros found
                AlertSystem->>Alert: Create Alert(Critical, "Malfunction suspected...")
            end
        end

        AlertSystem-->>Main: List[Alert]

        alt No alerts
            Main-->>Operator: "No alerts. All systems nominal."
        else Alerts found
            Main->>AlertSystem: formatAlerts(alerts)
            AlertSystem->>RepsResult: Validate non-empty alerts
            RepsResult-->>AlertSystem: Success(alertList)
            loop For each Alert
                AlertSystem->>AlertSystem: Match severity → icon
                AlertSystem->>AlertSystem: Format: "icon | source | time — message"
            end
            AlertSystem-->>Main: List[String]
            Main-->>Operator: Display formatted alerts
        end
    end
```

---

## Class Diagram — Full System Architecture

```mermaid
classDiagram
    direction TB

    class EnergySource {
        <<enum>>
        Solar
        Wind
        Hydro
    }

    class EnergySourceCompanion {
        <<object>>
        +fromString(name: String) Option~EnergySource~
    }

    class EnergyReading {
        <<case class>>
        +source: EnergySource
        +startTime: LocalDateTime
        +endTime: LocalDateTime
        +energyMW: Double
    }

    class RepsResult~A~ {
        <<sealed trait>>
        +map~B~(f: A => B) RepsResult~B~
        +flatMap~B~(f: A => RepsResult~B~) RepsResult~B~
        +fold~B~(onFailure, onSuccess) B
        +getOrElse~B~(default: B) B
        +isSuccess Boolean
        +isFailure Boolean
    }

    class Success~A~ {
        <<case class>>
        +value: A
    }

    class Failure {
        <<case class>>
        +message: String
    }

    class RepsResultCompanion {
        <<object>>
        +success~A~(value: A) RepsResult~A~
        +failure~A~(msg: String) RepsResult~A~
        +attempt~A~(expr: A) RepsResult~A~
        +validateDateFormat(dateStr: String) RepsResult~Tuple3~
        +requireNonEmpty~A~(values, context) RepsResult~List~
    }

    class AlertSeverity {
        <<enum>>
        Info
        Warning
        Critical
    }

    class Alert {
        <<case class>>
        +source: EnergySource
        +severity: AlertSeverity
        +message: String
        +reading: Option~EnergyReading~
    }

    class AlertSystem {
        <<object>>
        +detectIssues(readings, lowThreshold) List~Alert~
        -detectLowOutput(readings, threshold) List~Alert~
        -detectEquipmentMalfunction(readings) List~Alert~
        -groupBySource(readings) Map
        -findConsecutiveZeroRuns(sorted, min) List~List~
        +formatAlerts(alerts) List~String~
    }

    class StatisticsAnalysis {
        <<object>>
        +mean(values) RepsResult~Double~
        +median(values) RepsResult~Double~
        +mode(values) RepsResult~Double~
        +range(values) RepsResult~Double~
        +midrange(values) RepsResult~Double~
        +analyzeReadings~A~(readings, fn) RepsResult~A~
        +curriedAnalysis~A~(fn)(readings) RepsResult~A~
        -recursiveSum(values) Double
        -recursiveCount(values) Int
        -recursiveMin(values, curr) Double
        -recursiveMax(values, curr) Double
        -insertionSort(values) List~Double~
        -buildFrequencyMap(values, acc) Map
        -findMaxFrequencyKey(entries, key, count) Double
    }

    class DataFilter {
        <<object>>
        +filterByHour(readings, date, hour) List~EnergyReading~
        +filterByDay(readings, year, month, day) List~EnergyReading~
        +filterByWeek(readings, year, week) List~EnergyReading~
        +filterByMonth(readings, year, month) List~EnergyReading~
        +filterBySource(readings, source) List~EnergyReading~
        +sortByTime(readings) List~EnergyReading~
        +sortByEnergyDesc(readings) List~EnergyReading~
        +search(readings, predicate) List~EnergyReading~
        -split~A~(list) Tuple2
        -merge~A~(left, right, compare) List~A~
        -mergeSort~A~(list, compare) List~A~
    }

    class FileIO {
        <<object>>
        +readCsv(filePath, source) RepsResult~List~
        +writeCsv(filePath, readings) RepsResult~Unit~
        -stripQuotes(field) String
        -parseTimestamp(timestamp) LocalDateTime
    }

    class Main {
        <<@main>>
        +reps() Unit
        +mainMenu(all, solar, wind, hydro) Unit
        +viewEnergySummary(solar, wind, hydro) Unit
        +analysisMenu(all, solar, wind, hydro) Unit
        +filterMenu(all, solar, wind, hydro) Unit
        +searchMenu(all) Unit
        +alertMenu(all) Unit
    }

    %% Inheritance / Implementation
    Success --|> RepsResult : extends
    Failure --|> RepsResult : extends

    %% Composition & Usage
    EnergyReading *-- EnergySource : source
    Alert *-- EnergySource : source
    Alert *-- AlertSeverity : severity
    Alert o-- EnergyReading : reading (optional)

    %% Dependencies
    Main ..> FileIO : uses (readCsv)
    Main ..> StatisticsAnalysis : uses (analysis)
    Main ..> DataFilter : uses (filter/sort/search)
    Main ..> AlertSystem : uses (detectIssues)
    Main ..> RepsResult : uses (error handling)
    Main ..> EnergyReading : uses
    Main ..> EnergySource : uses

    FileIO ..> EnergyReading : creates
    FileIO ..> EnergySource : receives
    FileIO ..> RepsResult : returns

    StatisticsAnalysis ..> EnergyReading : reads energyMW
    StatisticsAnalysis ..> RepsResult : returns

    DataFilter ..> EnergyReading : filters/sorts
    DataFilter ..> EnergySource : filters by

    AlertSystem ..> EnergyReading : analyzes
    AlertSystem ..> Alert : creates
    AlertSystem ..> AlertSeverity : assigns
    AlertSystem ..> RepsResult : uses
    AlertSystem ..> EnergySource : groups by

    EnergySourceCompanion ..> EnergySource : creates

    RepsResultCompanion ..> RepsResult : creates
```

---

## How to Render & Export

1. **Online:** Paste each code block into [mermaid.live](https://mermaid.live) → download as PNG/SVG.
2. **VS Code:** Install the *Mermaid Markdown* extension → preview diagrams inline.
3. **CLI:** Use `npx @mermaid-js/mermaid-cli mmdc -i diagram.md -o output.png`.
