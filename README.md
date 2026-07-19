# Rootix — Console Launcher

A terminal-style Android launcher. Fork/rebrand maintained at
**[github.com/DominatorStufs/Rootix](https://github.com/DominatorStufs/Rootix)**.

Updated for compatibility with modern Android versions (API 34+), with security
hardening, a built-in `neofetch`, and a new AI assistant command.

---

## 🤖 AI Command (new)

Ask an AI anything, right from the terminal:

```
ai What is the capital of France?
.ai What is the capital of France?      (shortcut, also works as .AI)
```

* A random model is picked from the configured provider for every request.
* If that model errors out, Rootix automatically retries with a **different**
  random model, and if that also fails, falls back to a secondary provider —
  so a single flaky model never fully blocks the feature.
* **`ai open [appName]`** is a special case — instead of asking the AI, Rootix
  looks for an installed app matching `[appName]` and launches it directly.
  Example: `ai open whatsapp` or `.ai open whatsapp`.

## 🖥 neofetch (new, built-in)

```
neofetch
```

Prints a neofetch-style summary of your device: model, Android version,
uptime, CPU, memory, storage, battery, and screen resolution. This is
implemented natively inside Rootix — there's nothing to install, no BusyBox
or root required, it works the moment you type it.

## 📋 Full command list

See **[TERMINAL_COMMANDS.md](TERMINAL_COMMANDS.md)** for every command you can
type inside the Rootix terminal. To list every app installed on your device,
use `apps -ls`.

---

## ⌨️ Other notable commands
*   **`username [user] [device]`**: Customize your terminal prompt — changes both the username and device name and reloads the UI.
*   **`theme -preset [name]`**: Quickly switch between pre-configured themes (`blue`, `red`, `green`, `pink`, `bw`, `cyberpunk`). Applying a preset also colors the suggestion bar and shortcut buttons to match.
*   **`bbman -install`**: Installs BusyBox, unlocking 300+ standard Linux commands (`ls`, `grep`, `awk`, `top`, etc.) directly in the terminal.

> **Pro Tip:** On the very first install, if background transparency doesn't take effect immediately, type `restart` and press enter.

---

## 🐧 BusyBox Integration

1.  Type `bbman -install` in the terminal.
2.  Rootix detects your architecture, downloads the verified binary, and checks its integrity.
3.  Run any Linux command directly (e.g., `ls`, `ping`, `vi`).
4.  Remove it any time with `bbman -remove`.

Binaries are sourced from the EXALAB repository and verified against hardcoded SHA-256 hashes.

---

## 🛠 Build System
*   **Target SDK:** API 34 (Android 14)
*   **Min SDK:** API 21 (Android 5.0)
*   **AndroidX:** fully migrated
*   **Gradle / AGP:** Gradle 8.2, Android Gradle Plugin 8.2.0
*   **Java:** built with Java 17

### Building locally
```bash
chmod +x gradlew
./gradlew assembleFdroidDebug
```
Output: `app/build/outputs/apk/fdroid/debug/app-fdroid-debug.apk`

### Building via GitHub Actions
This repo includes `.github/workflows/build.yml`, a manual-only workflow:

1. Go to the **Actions** tab on GitHub.
2. Select **"Build & Release Rootix"**.
3. Click **"Run workflow"**.

It builds the release APKs and uploads them to a GitHub Release tagged with
the version set inside `build.yml` (currently `v1`). Bump `RELEASE_VERSION` in
that file for future releases. See `COMMANDS.md` for more build/deploy commands.

---

## 🛡 Security Hardening (OWASP MASVS)

### 📦 Data Storage and Privacy
*   **Scoped Storage:** all app data lives in app-private Scoped Storage (`Context.getExternalFilesDir()`), not public external storage.
*   **Backup Protection:** `android:allowBackup` is `false`.
*   **Secure File Sharing:** uses `FileProvider` instead of raw `file://` URIs.

### 🌐 Network Communication
*   **Enforced TLS:** `android:usesCleartextTraffic` is disabled globally; all network traffic goes over HTTPS.
*   **Hardened Endpoints:** internal services (weather, connectivity checks, AI) use HTTPS endpoints.

### ⚙️ Platform Interaction
*   **Signature-Level Protection:** custom permission `ohi.andre.consolelauncher.permission.RECEIVE_CMD` with `protectionLevel="signature"` — only apps signed with the same key can send commands to the launcher.
*   **Intent Security:** system-bound `PendingIntents` use `FLAG_IMMUTABLE`.
*   **Receiver Security:** broadcast receivers are registered with explicit `RECEIVER_EXPORTED` / `RECEIVER_NOT_EXPORTED` flags.

### 🛠 Code Quality & Build Settings
*   **Minification & Obfuscation:** release builds run R8/Proguard (`minifyEnabled true`).
*   **Foreground Service Security:** complies with Android 14's foreground service types (`specialUse`, `mediaPlayback`).

---

## 🔗 Links

**Repository** → **[github.com/DominatorStufs/Rootix](https://github.com/DominatorStufs/Rootix)**

## 📚 Open Source Libraries
* [**CompareString2**](https://github.com/fAndreuzzi/CompareString2)
* [**OkHttp**](https://github.com/square/okhttp)
* [**HTML cleaner**](http://htmlcleaner.sourceforge.net/)
* [**JsonPath**](https://github.com/json-path/JsonPath)
* [**jsoup**](https://github.com/jhy/jsoup/)
