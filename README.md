# 🎬 TeleStream: Native CloudStream Telegram Bot (Pure Kotlin JVM)

**TeleStream** is a lightweight, high-performance Telegram movie & series streaming bot written in **100% Kotlin (Java 21 LTS)** using **Ktor & Coroutines**. 

It runs CloudStream provider logic **natively on the JVM without any Android dependencies or Python bridges**, allowing you to discover, search, and stream content across multiple languages, including **English** and **Persian (فارسی)**.

---

## ✨ Features

- **☕ 100% Pure Kotlin / JVM**: Built with modern Kotlin 2.0+ and Ktor on Java 21 LTS. No Android SDK or Dalvik VM required.
- **🔄 Native CloudStream Compatibility**:
  - Pure-JVM SDK Shim (`com.lagradost.cloudstream3`) matching `MainAPI`, `app.get()`, `Jsoup`, and extractor links.
  - Bundled high-value providers:
    - **KissKH** (`com.telestream.providers.KissKH`): English movies, TV series, Asian dramas, and anime with direct HLS/MP4 streams.
    - **AvaMovie** (`com.telestream.providers.AvaMovie`): Persian dubbed and soft-subbed movies & series with direct stream links.
    - **FaselHD** (`com.telestream.providers.FaselHD`): Arabic and international multi-server media.
- **🇮🇷 Multi-Lingual Support (English & Persian)**:
  - First-time language selection on `/start`.
  - Full natural Persian translation with RTL support.
  - Smart search routing (queries in Persian are routed to Persian sources; Latin/English queries to global sources).
- **⚡ Ultra-Lightweight & Fast**: Consumes only ~60MB RAM on standard JVM.
- **💾 SQLite Persistence**: Single-file SQLite database (`data/telestream.db`) for user language preferences and favorite bookmarks across restarts.
- **🏥 Embedded Health Server**: Ktor embedded server on port 7860/8080 providing `GET /health` for cloud container health checks.
- **🚀 100% Free 24/7 Hosting**: Ready for 1-click deployment on **Hugging Face Spaces** (free 2 vCPU · 16 GB RAM, never sleeps).

---

## 📁 Project Structure

```text
telestream/
├── src/
│   ├── main/kotlin/
│   │   ├── com/lagradost/cloudstream3/ # Pure JVM CloudStream SDK Shim
│   │   │   └── MainAPI.kt              # MainAPI, app.get(), Jsoup helpers, models
│   │   └── com/telestream/
│   │       ├── bot/                    # Bot Runner & Event Handler
│   │       │   └── BotRunner.kt        # Polling loop, commands, search & callbacks
│   │       ├── database/               # SQLite Persistence
│   │       │   └── Database.kt         # User settings & bookmarks
│   │       ├── i18n/                   # Multi-lingual Engine
│   │       │   └── I18n.kt             # English & Persian (فارسی) translations
│   │       ├── providers/              # Native Kotlin Scrapers
│   │       │   ├── KissKH.kt           # English & Anime provider
│   │       │   ├── AvaMovie.kt         # Persian provider (dubbed/subtitled)
│   │       │   ├── FaselHD.kt          # Arabic & Regional provider
│   │       │   └── ProviderManager.kt  # Central manager & smart routing
│   │       ├── telegram/               # Telegram Bot API Client
│   │       │   ├── TelegramClient.kt   # Async Ktor CIO client
│   │       │   └── TelegramModels.kt   # Telegram JSON models
│   │       └── Main.kt                 # Application Entrypoint & Ktor server
│   └── test/kotlin/com/telestream/     # JUnit 5 Unit Tests
│       ├── ProviderTest.kt             # Provider metadata & routing tests
│       ├── DatabaseTest.kt             # SQLite persistence tests
│       └── I18nTest.kt                 # English & Persian translation tests
├── sample/                             # Reference CloudStream repositories
├── Dockerfile                          # Multi-stage Alpine container
├── build.gradle.kts                    # Kotlin Gradle build script
├── settings.gradle.kts                 # Project settings
└── gradle.properties                   # JVM memory options
```

---

## 🚀 Quick Start (Local PC / VPS)

### Prerequisites
- **Java 21 LTS** or newer (`java -version`).
- Telegram Bot Token from [@BotFather](https://t.me/BotFather).

### 1. Set Environment Variables
Create `.env` or set in your terminal:
```bash
# On Linux/macOS:
export BOT_TOKEN="your_bot_token_here"
export PORT="7860"

# On Windows PowerShell:
$env:BOT_TOKEN="your_bot_token_here"
$env:PORT="7860"
```

### 2. Run with Gradle
```bash
# On Linux/macOS:
./gradlew run

# On Windows:
.\gradlew.bat run
```

### 3. Build Self-Contained Fat JAR
```bash
./gradlew fatJar
java -jar build/libs/telestream-all.jar
```

---

## ☁️ 100% Free 24/7 Hosting on Hugging Face Spaces

Hugging Face Spaces provides **2 vCPU and 16 GB RAM** completely free, and instances **never sleep**:

1. Create a free account at [huggingface.co](https://huggingface.co).
2. Click **Spaces** > **Create new Space**.
3. Select **Docker** (Blank) and choose **Free** (16 GB RAM).
4. Go to **Settings** > **Variables and secrets**, add a secret:
   - Name: `BOT_TOKEN`
   - Value: `your_telegram_bot_token`
5. Push this repository to your Space:
   ```bash
   git remote add space https://huggingface.co/spaces/YOUR_USERNAME/YOUR_SPACE_NAME
   git push space main
   ```
6. Hugging Face will automatically build the `Dockerfile` and your bot will be **live 24/7**!

---

## 🧪 Running Automated Tests

Run the test suite:
```bash
./gradlew test
```
All unit tests verify:
- CloudStream JVM provider registration & Persian/English language detection.
- SQLite persistence (user language preferences & bookmark storage).
- Multi-lingual English & Persian translations.
