# AudioBook Reader

An Android application that converts ebooks and text files into audiobooks using Text-to-Speech (TTS) technology.

## Features

- 📚 **Multiple Format Support** - Read EPUB, PDF, and TXT files
- 🔊 **Text-to-Speech Playback** - Convert any book to audio using Android's TTS engine
- ⏯️ **Playback Controls** - Play, pause, and stop audio playback
- 🎚️ **Speed Control** - Adjust playback speed (0.5x to 2.0x)
- 📖 **Progress Tracking** - Automatically saves your reading progress
- 🔔 **Background Playback** - Continue listening with foreground service notifications
- 📱 **Modern UI** - Built with Jetpack Compose and Material 3

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture (Data, Domain, Presentation layers)
- **Dependency Injection**: Hilt
- **Database**: Room
- **Navigation**: Jetpack Navigation Compose
- **EPUB Parsing**: epub4j-core
- **PDF Parsing**: iText7

## Project Structure

```
app/src/main/java/com/example/audiobookreader/
├── core/
│   ├── parser/          # Book file parsers (EPUB, PDF, TXT)
│   ├── player/          # TTS-based audio player
│   └── tts/             # Text-to-Speech engine wrapper
├── data/
│   ├── local/           # Room database (entities, DAOs)
│   ├── mapper/          # Data to domain mappers
│   └── repository/      # Repository implementations
├── di/                  # Hilt dependency injection modules
├── domain/
│   ├── model/           # Domain models
│   ├── repository/      # Repository interfaces
│   ├── usecase/         # Business logic use cases
│   └── util/            # Utilities (Result class)
└── presentation/
    ├── library/         # Library screen and components
    ├── player/          # Player screen and components
    └── navigation/      # Navigation graph and routes
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Antismart/AudioBookReader.git
   ```

2. Open the project in Android Studio

3. Sync Gradle and build the project

4. Run on an emulator or physical device (API 24+)

## Usage

1. **Add Books**: Tap the + button on the Library screen to import EPUB, PDF, or TXT files
2. **Start Listening**: Tap on a book to open the player
3. **Control Playback**: Use play/pause/stop buttons to control audio
4. **Adjust Speed**: Select playback speed from the dropdown menu
5. **Track Progress**: Your reading position is automatically saved

## Permissions

- **POST_NOTIFICATIONS** (Android 13+): For playback notification controls
- **READ_EXTERNAL_STORAGE** (Android 12 and below): For importing book files
- **FOREGROUND_SERVICE**: For background audio playback

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Acknowledgments

- [epub4j](https://github.com/documentnode/epub4j) for EPUB parsing
- [iText7](https://itextpdf.com/) for PDF parsing
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI
- [Material 3](https://m3.material.io/) for design system
