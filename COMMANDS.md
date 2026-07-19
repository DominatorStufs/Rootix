# Rootix Launcher Development & Deployment Guide

This document summarizes the essential commands for building, installing, and managing the Rootix Linux CLI Launcher fork.

## 🛠 Building the APK

To perform a clean build of the F-Droid version (includes SMS permissions):

```bash
# Ensure gradlew is executable
chmod +x gradlew

# Build the F-Droid Debug APK
./gradlew assembleFdroidDebug
```

**Output Path:** `app/build/outputs/apk/fdroid/debug/app-fdroid-debug.apk`

---

## 📱 Emulator Deployment (ADB)

To install the APK on your Pixel 9 Pro emulator (or any physical device connected via USB):

```bash
# 1. Start the emulator (if not already running)
emulator -avd Pixel_9_Pro -gpu host -accel on &

# 2. Wait for the device and install (overwriting existing)
adb wait-for-device
adb install -r app/build/outputs/apk/fdroid/debug/app-fdroid-debug.apk
```

---

## 🐧 BusyBox Management

The launcher now features a built-in BusyBox manager to enable standard Linux commands.

### Installation
In the Rootix terminal, type:
```bash
bbman -install
```
This downloads the architecture-specific ELF binary, verifies its **SHA-256 hash**, and sets up the environment.

### Usage
Once installed, you can use any Linux command directly:
```bash
ls -la
grep "search_term" file.txt
busybox --list
```

### Removal
To clean up the environment:
```bash
bbman -remove
```

---

## 🔍 Useful ADB Debugging Commands

```bash
# View real-time logs (filtered for Rootix)
adb logcat | grep com.rootix.launcher

# Uninstall the launcher via ADB
adb uninstall com.rootix.launcher

# Push a file to the launcher's internal storage
adb push local_file.txt /data/user/0/com.rootix.launcher/files/
```

---

## 🛡 Security Note
All binaries are verified using hardcoded SHA-256 hashes found in `app/src/main/java/com/rootix/launcher/tuils/BusyBoxInstaller.java`. All network transport is forced over HTTPS.

---

---

## 📱 Android Version Support

Rootix now builds against the latest Android SDK tooling:

| | Before | Now |
|---|---|---|
| compileSdk | 34 | **36** (Android 16) |
| targetSdk | 34 | **35** (Android 15) |
| minSdk | 21 | 21 (unchanged, still runs on Android 5.0+) |
| Android Gradle Plugin | 8.2.0 | **8.13.0** |
| Gradle | 8.2 | **8.13** |

**Why targetSdk 35 and not 36:** Android 15 (API 35) is the last version where Google lets an app
temporarily opt out of forced edge-to-edge display via `windowOptOutEdgeToEdgeEnforcement` (already
applied in `styles.xml`) — that flag stops working once an app targets API 36. Since Rootix's
terminal UI wasn't built with `WindowInsets` handling, jumping straight to targetSdk 36 would
risk the status/nav bar overlapping the console on the newest phones, and there's no way for me
to test that visually in this environment. targetSdk 35 still meets Google Play's "existing apps"
requirement for 2026 and installs and runs fine on Android 16 devices.

**Please build and test this on a real device (or emulator) before relying on it** — I updated
the Gradle/AGP/SDK versions carefully, but I can't run an Android build in this environment, so
I can't guarantee a clean compile or verify the UI on an actual Android 15/16 screen.

---

## 🖥 neofetch

Type `neofetch` and press enter — prints a neofetch-style summary of your device: Android
version, host/model, kernel, uptime, launcher version, RAM, storage, screen resolution,
battery %, and CPU.

```bash
neofetch
```

No install step needed, it's a built-in command, discovered automatically like every other
Rootix command.

---

## 🚀 Manual Build & Release (GitHub Actions)

`.github/workflows/build-release.yml` builds a signed release APK and publishes it as a GitHub
Release, but only runs when you trigger it manually:

1. Go to your repo's **Actions** tab -> **Build & Release Rootix** -> **Run workflow**
2. It builds `assembleFdroidRelease`, packages it as `Rootix-<version>.apk`, and creates/updates
   a GitHub Release tagged with that version

**Version lives in the workflow file itself** — open `build-release.yml` and change this line
before each run you want published as a new release:
```yaml
env:
  RELEASE_VERSION: V1   # bump to V2, V3... next time
```

**Signing:** by default (no secrets configured) it auto-generates a throwaway keystore so the
build always succeeds — fine for testing, not for a real public release. For a real signed
release, add these secrets in **Settings -> Secrets and variables -> Actions**:
* `KEYSTORE_BASE64` — your `release.jks` run through `base64 -w0 release.jks`
* `KEYSTORE_PASSWORD`
* `KEY_ALIAS`
* `KEY_PASSWORD`

---

```bash
apps -all
```

Lists **every app installed on the device** — including system apps and apps without a
launcher icon (not just the ones shown in your app list). Each line shows the app name and its
package name.

Related, already-existing options:
* `apps -ls` -> only your visible/launchable apps
* `apps -lsh` -> your hidden apps

---

## 🤖 AI Command (`.ai`)

Rootix has a built-in AI assistant. Type `.ai` or `.AI` followed by your question anywhere on the
home screen terminal:

```bash
.ai who are you
.AI explain quantum computing in one line
```

Every query picks a **random model** from a pooled list of two OpenAI-compatible backends:

* `https://chatbot.codexapi.workers.dev` — 35+ models (GPT-5, Claude, Gemini, Grok, DeepSeek, Qwen, Kimi, Llama, Mistral, and more)
* `https://copilot-api-delta.vercel.app` — Copilot model

If the picked model errors out or returns an empty response, Rootix automatically retries with a
different random model (up to 6 attempts) before giving up. The response is shown with the model
name that answered, e.g. `[gpt-5.2]`.

**Bonus shortcut:** `.ai open <app name>` opens the named app directly (no AI call needed) — for
example:

```bash
.ai open whatsapp
```

This only opens apps that are actually installed on your device; it does the same lookup as
typing an app's name directly in Rootix.

---

## 📜 Full Command Reference

All built-in commands (type `help <command>` inside Rootix for full usage of any of these):

| Command | Description |
|---|---|
| `airplane` | Toggle airplane mode |
| `alias` | -add [aliasName] [alias content] -> add a new alias |
| `apps` | -ls -> list your apps |
| `bbman` | -install -> download and setup BusyBox |
| `beep` | Emit a "beep". |
| `bluetooth` | Toggle bluetooth |
| `brightness` | Set the brightness of your device |
| `calc` | Perform basic operations (+ - * / % ^ sqrt) |
| `call` | Call someone |
| `changelog` | Show the changelog of the current version |
| `clear` | Clear the screen |
| `cntcts` | -ls -> list your contacts |
| `config` | -set [option] [value] -> set the value of an option |
| `ctrlc` | Interrupt the current shell process and create a new one |
| `data` | Toggle mobile data |
| `devutils` | There's nothing interesting there |
| `donate` | Offer a coffee to the developer (PayPal only) |
| `exit` | Close Rootix and reset the default launcher |
| `flash` | Toggle the flashlight |
| `help` | Print the available commands, or info about a command |
| `htmlextract` | Extract text from HTML pages |
| `location` | Show your current location |
| `music` | -next -> play the next song |
| `neofetch` | Print a neofetch-style summary of your device (OS, memory, storage, battery, resolution...) |
| `notes` | -add [optional: lock {true/false}] [text] -> add a new note |
| `notifications` | -inc [appName] -> include an application |
| `open` | Open a file |
| `rate` | Leave a feedback on the Play Store page |
| `refresh` | Refresh apps, alias, music, contacts |
| `regex` | -add [ID] [regex] -> add a new regex with the given ID |
| `reply` | -to [ID or packageName] [what] -> reply to the last notification from the application bound with the given ID |
| `restart` | Restart rootix and load modified values |
| `rootix` | -rm -> uninstall rootix |
| `rootixt` | Open the text editor. |
| `rootixweather` | Manage the weather widget shown in the output field |
| `rss` | -add [ID] [update_time_in_seconds] [url] -> add a new RSS feed. rootix will try to get new contents every [update_time_in_seconds] seconds |
| `search` | -gg [search this...] -> Google Search |
| `share` | $ share [pathToFile] |
| `shortcut` | -ls [appName] -> show the shortcuts of this app |
| `status` | Get info about battery charge, wifi status and mobile data |
| `theme` | -apply [path] -> apply a theme from a local file |
| `time` | Print the current time with the given format |
| `tutorial` | Open the tutorial page on GitHub |
| `uninstall` | Uninstall an application |
| `username` | Change the username and device name: username [new_user] [new_device] |
| `vibrate` | Make your device vibrate. |
| `volume` | -set [stream] [volume: 0-100] -> set the volume for the selected stream |
| `wifi` | Toggle wifi |

