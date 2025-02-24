# FitFormAR - AR Fitness Form Checker

An Android application that uses Augmented Reality to help users maintain proper form during exercises. The app uses local AR processing through ARCore to analyze and provide real-time feedback on exercise form.

## Features

- Real-time form analysis for various exercises:
  - Core and Abdominal Exercises
  - Upper Body Strength Exercises
  - Lower Body Strength Exercises
  - Flexibility and Balance Exercises
- Local AR processing (no cloud dependencies)
- Visual feedback and form correction guidance
- Exercise progress tracking
- Built-in tutorial mode

## Technical Requirements

- Android Studio Arctic Fox or newer
- Minimum SDK: Android 7.0 (API Level 24)
- Target SDK: Android 13 (API Level 33)
- ARCore compatible device
- Kotlin programming language
- AndroidX libraries
- Google ML Kit for pose detection

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/fitformar/
│   │   │   ├── ar/           # AR processing and pose detection
│   │   │   ├── data/         # Exercise data models
│   │   │   ├── ui/           # UI components
│   │   │   └── utils/        # Utility classes
│   │   └── res/             # Resources
├── build.gradle
└── proguard-rules.pro
```

## License

MIT License
