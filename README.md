# Android Game Project: MiniJeux

## Overview

MiniJeux is an Android application that offers fun and engaging games.


## Features

*   **Bluetooth Multiplayer:** layers can connect to nearby devices using Bluetooth and play against each other in real-time. (in work)
*   **Quiz/Question System]:** A variety of questions across different categories with varying difficulty levels. Players are challenged to answer quickly and accurately.
*   **Scoring/Progression:** Points are awarded for each correct answer. Scores are tracked throughout the session and displayed at the end.
*   **End Game Screen:** Displays the player's final score, and provides options to restart the game or return to the main menu.
*   **Other Features:** Possibility of personalized player names and simple animated transitions between activities for a better user experience. (in work)

## Technologies Used

*   **Java:** For the main structure.
*   **Android SDK:** For building the Android application.
*   **Android Jetpack:** Modern Android libraries for UI, data management, and more.
*   **Bluetooth:** For enabling multiplayer.

## Getting Started

### Prerequisites

*   **Android Studio:** The latest version of Android Studio is recommended.
*   **Android SDK:** Ensure you have the necessary Android SDK components installed.
*   **Android Device or Emulator:** To run and test the application.

### Installation

1.  **Clone the Repository:**
```bash 
git clone https://github.com/mathis-lcq/game.git
```

2.  **Open in Android Studio:** Open Android Studio and select "Open an existing Android Studio project." Navigate to the directory where you cloned the repository and select the project.
3.  **Sync Project with Gradle Files:** Android Studio will prompt you to sync the project with the Gradle files. Click "Sync Now."
4.  **Build and Run:** Once the project is synced, you can build and run the application on an Android device or emulator by clicking the "Run" button (green play icon).

## Project Structure

*   **`app/src/main/java/com/example/game/`:** Contains the main Kotlin source code for the application.
    *   `EndActivity.java`: The activity displayed at the end of the game.
    *   `FindPlayerActivity.java`: The activity used to find other players via Bluetooth.
    *   `NumericQuiz.java`: The activity used for the quiz.
*   **`app/src/main/res/`:** Contains the resource files for the application (layouts, drawables, values, etc.).
*   **`app/build.gradle`:** The Gradle build file for the app module.
*   **`build.gradle`:** The project-level Gradle build file.
*   **`AndroidManifest.xml`:** The application's manifest file.

## Bluetooth Setup

To use the Bluetooth features of this application, you will need to:

1.  **Enable Bluetooth:** Ensure that Bluetooth is enabled on your Android device.
2.  **Grant Permissions:** The application will request the necessary Bluetooth permissions (`BLUETOOTH_CONNECT`) at runtime.
3. **Pair devices:** If you want to test the multiplayer feature, you will need to pair two devices.

## Contact GitHub

[mathis-lcq](mathis-lcq)

[ykerverdo](ykerverdo)
