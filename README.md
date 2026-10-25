# Secure Online Polling & Result Analysis System
> **College Object-Oriented Programming (OOP) Project — Iteration 2**  
> Developed using pure Java (Standard Edition) and Java Swing.

---

## 1. Project Overview

The **Secure Online Polling & Result Analysis System** is a lightweight, responsive desktop application developed for a college Object-Oriented Programming project. It provides an intuitive platform to create polls, cast secure votes, prevent duplicate ballots per poll, and analyze live electoral distributions with horizontal graphical bars and tie detection.

In **Iteration 2**, the system incorporates advanced software engineering design patterns (**Observer Pattern** and **Strategy Pattern**), cryptographic ballot protection (**Salted SHA-256 Voter Hashing** and **Ballot Receipts**), and certified **CSV and Text Report Exporting**.

> [!NOTE]
> **In-Memory Storage Notice**: This application operates in-memory using standard Java collections (`HashMap` and `HashSet`). All polls, votes, and timestamps reset when the application restarts. This design avoids external database/network dependencies while keeping data structures clean, transparent, and easy to explain during academic evaluation.

---

## 2. Key Features

1. **Poll Creation**: Create polls with a customized question and at least two distinct options (dynamic option adder and remover).
2. **Catalog Browsing & Search**: View available polls in clean modern cards, filter by "All", "Open", or "Closed", and search questions or poll IDs in real time.
3. **Ballot Submission & Cryptographic Receipt**: Voters submit their ID and option. Upon submission, the voter receives a unique cryptographic receipt code (e.g., `RCPT-7A9F...`) to verify their ballot.
4. **Salted SHA-256 Voter ID Hashing (HashSet)**: Plain-text voter IDs are never stored. The system computes a poll-salted SHA-256 hash and stores it in a `HashSet<String>`, preserving voter anonymity while enforcing $O(1)$ duplicate prevention.
5. **Real-time Observer Pattern (Event-Driven UI)**: When a vote is cast or a poll is created/closed, all open panels (`PollListPanel`, `ResultsPanel`, `AdminPanel`) update automatically in real time without manual refresh clicks.
6. **Pluggable Electoral Strategy Pattern**: Switch between electoral models on the fly in the Results tab:
   - **Plurality (First-Past-The-Post)**: Standard highest-vote winner, with tie detection.
   - **Majority Threshold (>50% Required)**: Requires strictly greater than 50% to declare an outright winner; otherwise flags "Runoff Required among leaders".
7. **CSV & Certified Text Report Exports**: Export election results as RFC-4180 compliant `.csv` spreadsheets or certified `.txt` audit reports containing SHA-256 cryptographic trails.
8. **Receipt Verification & Audit**: Administrators can enter any receipt code to verify its legitimacy or inspect the raw SHA-256 voter hash set.
9. **Polymorphic Persona Switcher**: Seamlessly toggle between "Administrator" and "Standard Voter" sessions in the UI to demonstrate runtime polymorphism and access control.
10. **Pre-Seeded Sample Polls**: The application comes pre-loaded with realistic college polls (including a 50/50 tie demonstration) for immediate evaluation without setup delay.

---

## 3. Mandatory & Advanced OOP Concepts Demonstrated

| OOP Concept | Project Implementation | Key Classes / Methods |
| :--- | :--- | :--- |
| **Encapsulation** | All entity fields are declared `private` or `final`. State is accessed exclusively through getters and validated business methods. Defensive copies are returned for mutable collections (`Collections.unmodifiableList`, `new HashMap<>`, `new HashSet<>`). | [`Poll.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/model/Poll.java), [`User.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/model/User.java) |
| **Abstraction** | Defined abstract base class `User` encapsulating common user identity while declaring abstract contracts (`getRole()`, `getPermissionsDescription()`, `canManagePolls()`) that concrete subtypes must fulfill. | [`User.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/model/User.java) |
| **Inheritance** | `Admin` and `Voter` extend `User`, inheriting core identification logic while introducing specialized attributes (e.g., administrator department) and behaviors. | [`Admin.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/model/Admin.java), [`Voter.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/model/Voter.java) |
| **Polymorphism** | Overridden methods exhibit distinct runtime behavior. Dynamic dispatch allows UI components to display role titles, descriptive permissions, and gate administrative actions dynamically based on the polymorphic reference `User`. | `user.getRole()`, `user.canManagePolls()` |
| **Observer Pattern** *(Iteration 2)* | `PollManager` acts as the Subject maintaining a thread-safe list of `PollEventListener` observers. When state changes (`VOTE_CAST`, `POLL_CREATED`, `POLL_CLOSED`, `POLL_REOPENED`), events are broadcast and UI panels auto-refresh on the Swing Event Dispatch Thread. | [`PollEventListener.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/event/PollEventListener.java), [`PollEvent.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/event/PollEvent.java), [`PollManager.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/service/PollManager.java) |
| **Strategy Pattern** *(Iteration 2)* | `WinningStrategy` interface allows pluggable electoral calculation algorithms (`PluralityWinningStrategy` vs. `MajorityWinningStrategy`). `ResultAnalyzer` evaluates outcomes based on the selected strategy. | [`WinningStrategy.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/strategy/WinningStrategy.java), [`PluralityWinningStrategy.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/strategy/PluralityWinningStrategy.java), [`MajorityWinningStrategy.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/strategy/MajorityWinningStrategy.java) |
| **Exception Handling** | Domain-specific checked exceptions handle error states gracefully with clean user feedback, instead of generic crashes. | [`DuplicateVoteException`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/exception/DuplicateVoteException.java), [`PollClosedException`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/exception/PollClosedException.java), [`InvalidVoteException`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/exception/InvalidVoteException.java), [`PollNotFoundException`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/exception/PollNotFoundException.java), [`ValidationException`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/exception/ValidationException.java) |
| **`HashMap` Collection** | 1. `pollCatalog` (`HashMap<String, Poll>`) stores polls indexed by ID for $O(1)$ retrieval.<br>2. `voteCounts` (`HashMap<String, Integer>`) stores tallies per candidate option.<br>3. `issuedReceipts` (`HashMap<String, VoteReceipt>`) stores cryptographic ballot receipts for instant lookup. | [`PollManager.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/service/PollManager.java), [`Poll.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/model/Poll.java) |
| **`HashSet` Collection** | `votedVoterHashes` (`HashSet<String>`) stores SHA-256 salted hashes of voter IDs. Guarantees anonymity while enforcing strict single-vote prevention with $O(1)$ containment lookups. | [`Poll.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/model/Poll.java), [`SecurityUtil.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/src/com/polling/security/SecurityUtil.java) |

---

## 4. Architecture & Package Structure

```
oop-gauri&aneek/
├── src/
│   └── com/
│       └── polling/
│           ├── Main.java                          # Application launcher & Look-and-Feel setup
│           ├── model/
│           │   ├── User.java                      # Abstract base user (Abstraction & Encapsulation)
│           │   ├── Admin.java                     # Admin user (Inheritance & Polymorphism)
│           │   ├── Voter.java                     # Voter user (Inheritance & Polymorphism)
│           │   ├── Poll.java                      # Poll entity (HashMap & HashSet encapsulation)
│           │   └── VoteReceipt.java               # Cryptographic ballot receipt model
│           ├── security/
│           │   └── SecurityUtil.java              # SHA-256 salted voter hashing & receipt generator
│           ├── event/
│           │   ├── PollEventType.java             # Lifecycle event types (VOTE_CAST, POLL_CREATED, etc.)
│           │   ├── PollEvent.java                 # Event payload dispatched to observers
│           │   └── PollEventListener.java         # Observer interface for real-time updates
│           ├── strategy/
│           │   ├── WinningStrategy.java           # Strategy interface for electoral models
│           │   ├── PluralityWinningStrategy.java  # First-past-the-post strategy
│           │   ├── MajorityWinningStrategy.java   # >50% threshold runoff strategy
│           │   └── StrategyEvaluationResult.java  # Strategy verdict DTO
│           ├── exception/
│           │   ├── PollingException.java          # Base custom exception
│           │   ├── DuplicateVoteException.java    # Thrown when voter attempts double voting
│           │   ├── PollClosedException.java       # Thrown when voting on inactive poll
│           │   ├── InvalidVoteException.java      # Thrown when option choice is invalid
│           │   ├── PollNotFoundException.java     # Thrown when poll ID is missing
│           │   └── ValidationException.java       # Thrown on blank input / <2 options
│           ├── service/
│           │   ├── PollManager.java               # Catalog manager & Observer Subject
│           │   ├── VotingService.java             # Core voting logic and security verification
│           │   ├── ResultAnalyzer.java            # Analytics & Strategy-aware evaluator
│           │   ├── ReportExporter.java            # CSV and Certified Plain Text Report exporter
│           │   └── PollResultSummary.java         # Immutable analytics DTO
│           └── ui/
│               ├── ModernTheme.java               # Color palette, typography, and button styling
│               ├── MainFrame.java                 # Main window, navigation tabs, status banners
│               ├── PollCardPanel.java             # Individual poll card with voting actions
│               ├── PollListPanel.java             # Poll list with search & status filters (Observer)
│               ├── CreatePollPanel.java           # Dynamic form to author new polls
│               ├── ResultsPanel.java              # Analytics, Strategy selector, CSV/Text export (Observer)
│               └── AdminPanel.java                # Persona switcher, receipt verifier, audit (Observer)
├── test/
│   └── com/
│       └── polling/
│           └── test/
│               └── VotingSystemTest.java          # Standalone automated verification test suite (10/10 tests)
└── README.md
```

---

## 5. UI/UX Design Direction

The graphical user interface follows modern, minimal desktop design principles:

- **Palette**:
  - Background: Off-white Slate (`#F8FAFC`)
  - Cards: Pure White (`#FFFFFF`) with subtle 1px border (`#E2E8F0`)
  - Typography: Dark Navy headings (`#0F172A`) and Slate body text (`#334155`)
  - Accent: Single restrained blue (`#2563EB`) with hover states (`#1D4ED8`)
  - Status Indicators: Emerald Green (`#10B981`) for Open polls and Crimson Red (`#DC2626`) for Closed polls
- **Typography**: Clean hierarchy with Sans-Serif fonts (20pt Headings, 15pt Subheadings, 13pt Body).
- **Responsiveness**: Smooth scaling from standard laptop resolutions ($1080 \times 720$) down to compact screens ($920 \times 600$) using layout managers and customized `JScrollPane` containers.
- **Feedback**: Color-coded inline banners for success and errors, and confirmation dialogs for destructive actions.

---

## 6. How to Compile & Run

### Prerequisites
- Java Development Kit (JDK 8, 11, 17, 21, or newer).

### Terminal / Command Line Instructions

1. **Navigate to the project root directory**:
   ```bash
   cd /path/to/oop-gauri&aneek
   ```

2. **Compile all source and test files**:
   ```bash
   javac -d bin $(find src test -name "*.java")
   ```

3. **Run the desktop application**:
   ```bash
   java -cp bin com.polling.Main
   ```

4. **Run the automated test suite**:
   ```bash
   java -cp bin com.polling.test.VotingSystemTest
   ```

### Running in IntelliJ IDEA / Eclipse / VS Code
1. Open the project folder in your IDE.
2. Ensure the `src` directory is marked as the **Sources Root** and `test` as the **Test Sources Root**.
3. Locate `com.polling.Main.java` and click **Run** (or `Shift + F10`).

---

## 7. Automated Test Suite (10 Comprehensive Tests)

A standalone test runner ([`VotingSystemTest.java`](file:///Users/aneekdutta/Downloads/oop-gauri%26aneek/test/com/polling/test/VotingSystemTest.java)) is included to verify all functional requirements and OOP principles without third-party dependencies:

- **Test 1**: Inheritance & Polymorphism (verifies abstract methods and permissions).
- **Test 2**: Successful Vote Recording (verifies ballot tallies and collection state).
- **Test 3**: Duplicate Vote Prevention (asserts that a duplicate voter ID throws `DuplicateVoteException`).
- **Test 4**: Closed Poll Rejection (asserts that voting on a closed poll throws `PollClosedException`).
- **Test 5**: Input Validations (asserts validation exceptions on empty question, single option, and unlisted choices).
- **Test 6**: Result Calculation & Tie Handling (verifies percentage precision, zero-vote states, and multi-candidate ties).
- **Test 7**: Cryptographic SHA-256 Hashing & Ballot Receipts (verifies salted hashes and receipt verification).
- **Test 8**: Observer Pattern Event Dispatching (verifies real-time event dispatching on poll actions).
- **Test 9**: Strategy Pattern (asserts differences between Plurality and Majority Threshold runoff mandates).
- **Test 10**: Report Exporter (verifies valid output for CSV spreadsheets and certified audit text reports).

Run the tests anytime with:
```bash
java -cp bin com.polling.test.VotingSystemTest
```

---

## 8. Security & Correctness Disclaimers

- **Educational Purpose**: This project is developed as a college demonstration of Object-Oriented Programming and Swing GUI engineering.
- **Anonymized Ballot Integrity**: Plain-text voter IDs are never permanently stored. Only a salted SHA-256 digest is maintained in the internal `HashSet`, ensuring single-vote enforcement without compromising ballot secrecy.
- **Defense-in-Depth**: Duplicate vote checks, cryptographic hashing, and closed-status validations are enforced inside `VotingService` and `Poll`, guaranteeing that disabled UI buttons are not the sole line of defense.
