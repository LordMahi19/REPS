# Renewable Energy Plant System (REPS)

**Course:** CT60A9602 Functional Programming - Blended teaching, Lahti  
**Deadline:** Sunday, May 3rd, 2026 at 23:59

## Team Members
* **Mahi**
* **Oyshe**
* **Nguyen**

## Project Overview
This project is a multi-generation renewable energy plant system that manages solar, wind, and hydropower energy production. The system reads and analyzes energy generation data, detects issues (like equipment malfunctions), and provides a robust alert system. 

The core logic is implemented in **Scala**, strictly adhering to Functional Programming paradigms:
* Immutable data structures
* Iteration via recursion
* Higher-order functions
* Functional error handling

## Project Architecture
The codebase is structured to separate pure functional logic from imperative I/O operations:

* `src/main/scala/models/` - Immutable classes/traits for energy sources (Solar, Wind, Hydro).
* `src/main/scala/analysis/` - Pure functions for data analytics (Mean, Median, Mode, Range, Midrange).
* `src/main/scala/alerts/` - Functional issue detection system.
* `src/main/scala/io/` - Imperative file reading/writing (.csv or .xls).
* `data/` - Dummy energy data sets.
* `docs/` - System diagrams (Sequence & Class diagrams).
* `advanced_topic/` - Independent implementation of the chosen advanced Scala concept.

## Setup Instructions

**Prerequisites:** You must have [Java (JDK 11 or higher)](https://adoptium.net/) and [sbt (Scala Build Tool)](https://www.scala-sbt.org/download.html) installed on your machine.

1. **Clone the repository:**
```bash
git clone <your-github-repo-url>
cd REPS
```
2. **compile the project**
```bash
sbt compile
```
3. **run the project**
```bash
sbt run
```
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