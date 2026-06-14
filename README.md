# 🌍 ARTEMIS Command Center

**Advanced Real-time Tracking & Emergency Mission Integrated System**

ARTEMIS is a comprehensive, JavaFX-based desktop software solution designed for Security Operations Centers (SOCs). Its primary purpose is to facilitate real-time global emergency response and intelligence gathering. The software replaces fragmented communication tools by centralizing target dossiers, dynamic mission tracking, and tactical asset deployment into a single, highly secure, high-trust digital command environment.

---

## ✨ Key Features

*   **🕵️ Digital Shadow Dossiers:** Instantly aggregates a target's travel itineraries, companion logistics, and historical incident data to provide coordinators with complete situational awareness.
*   **🚨 AI Threat Assessment:** An integrated triage engine that parses incoming SOS descriptions against a localized dictionary of high-risk keywords (e.g., "ambush", "hemorrhage"), automatically escalating mission classifications to ELEVATED or CRITICAL.
*   **🚁 Dynamic Dispatch Center:** A dual-pane deployment nexus that aggregates active crises in an "Awaiting Dispatch" queue alongside a filtered list of "Proximity Assets," allowing operators to rapidly pair specialized responder units with active missions.
*   **🗺️ Global Threat Topology Radar:** A lightweight, custom-engineered continental telemetry map built with native JavaFX shapes to visually manage global threat baselines without the rendering latency of heavy GIS data.
*   **📑 Mission Audit Ledgers:** Secure, post-mission reporting for institutional review, ensuring all operational protocols and state changes are properly documented.

---

## 🛠️ Technical Architecture & Tech Stack

*   **Language:** Java (JDK 17+)
*   **Framework:** JavaFX (Programmatic UI)
*   **Database:** SQLite / JDBC
*   **Styling:** Custom CSS (`aegis-theme.css`)

### 🎨 The "Ghost Protocol" UI/UX
To achieve an enterprise-grade tactical aesthetic and reduce operator eye strain, ARTEMIS completely bypasses the default JavaFX styling engine (Modena) and avoids FXML entirely. 
*   **100% Programmatic UI:** The interface is built purely in Java, providing superior compile-time safety, dynamic data-driven construction, and fine-grained lifecycle control.
*   **Deep CSS Integration:** Utilizes a dark `#111827` root base, custom `.surface-card` definitions, and surgical `.white-subtext` overrides to create a high-contrast, zero-glare dashboard.

---

## 🚀 Getting Started

### Prerequisites
*   [Java Development Kit (JDK) 17 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
*   [JavaFX SDK](https://openjfx.io/) (If not using a build manager like Maven/Gradle)
*   IDE of choice (VS Code, IntelliJ IDEA, Eclipse)


---

## 👥 Team Members

This project was developed by:

*   **Malhan Bin Faisal** - [GitHub: @MalhanFaisal](https://github.com/MalhanFaisal)
*   **Rehan Ayub** - [GitHub: @RehanAyub56](https://github.com/RehanAyub56)
*   **Moiz Raja** - [GitHub: @RajaMoiz608](https://github.com/RajaMoiz608)

---

> **Note:** ARTEMIS was built as a capstone Software Design & Analysis project focusing on Object-Oriented design patterns, database integration, and advanced UI/UX programmatic styling.
