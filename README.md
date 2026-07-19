# Rootix Linux CLI Launcher

Updated for compatibility with modern Android versions (API 34+) and enhanced with security hardening.

---

## 🚀 Recent Changes & Modernization

These updates ensure the launcher remains functional, secure, and performant on modern Android devices (Android 11 through Android 14+).

> **Pro Tip:** On the very first install, if background transparency does not take effect immediately, simply type \`restart\` in the terminal and press enter.

### ⌨️ New Commands
*   **`username [user] [device]`**: Instantly customize your terminal prompt. Changes both the username and device name and reloads the UI to apply.
*   **`theme -preset [name]`**: Rapidly switch between high-quality pre-configured themes.
    *   **Available Presets:** `blue`, `red`, `green`, `pink`, `bw`, `cyberpunk`.
    *   **Smart Suggestions:** Applying a preset automatically colors the suggestion bar and shortcut buttons to match the aesthetic.
*   **`bbman`**: The new BusyBox manager for installing and verifying Linux binaries.
*   **`neofetch`**: Prints a neofetch-style device summary — OS, host, kernel, uptime, memory, storage, resolution, battery, CPU.
*   **`apps -all`**: Lists every app installed on the device, including system apps and apps without a launcher icon.
*   **`.ai` / `.AI`**: Built-in AI assistant, see below.

### ✨ Enhanced Features
*   **Built-in BusyBox Manager:** Gain access to 300+ Linux commands (ls, grep, awk, top, etc.) via the new `bbman -install` command.
*   **Theme Preset Shortcut Buttons:** Enhanced the `theme -preset` command to show interactive shortcut buttons for presets.
*   **Synchronized Theme UI:** Applying a preset now automatically colors the shortcut buttons (suggestions) to match the overall theme.
*   **One-Tap Application:** Shortcut buttons for theme presets execute immediately upon clicking.

---

## 🐧 BusyBox Integration

To enable a full Linux environment, you can install BusyBox directly from the launcher:

1.  Type `bbman -install` in the terminal.
2.  The launcher will automatically detect your architecture, download the verified binary, and check its integrity.
3.  Once finished, you can run any Linux command directly (e.g., `ls`, `ping`, `vi`).
4.  To remove it at any time, use `bbman -remove`.

**Security Note:** Binaries are sourced from the trusted EXALAB repository and are verified against hardcoded SHA-256 hashes to ensure they have not been tampered with.

---

## 🛠 Modern Build System
*   **Compile SDK:** Updated to **API 36 (Android 16)**.
*   **Target SDK:** Updated to **API 35 (Android 15)**.
*   **Min SDK:** API 21 (Android 5.0).
*   **AndroidX Migration:** Fully migrated from legacy Support Libraries to **AndroidX**.
*   **Gradle & AGP:** Updated to Gradle 8.13 and Android Gradle Plugin 8.13.0.
*   **Java Compatibility:** Built with **Java 17** support.
*   **CI/CD:** Manual GitHub Actions workflow (`.github/workflows/build-release.yml`) builds a
    signed release APK and publishes it as a GitHub Release on demand — see
    [COMMANDS.md](COMMANDS.md) for how to trigger it and configure signing secrets.

---

## 🛡 Security Hardening (OWASP MASVS Compliance)

This project has been audited and hardened following the **OWASP Mobile Application Security Verification Standard (MASVS)**.

### 📦 MASVS-STORAGE: Data Storage and Privacy
*   **Scoped Storage Implementation:** All application data has been moved from public external storage (`/sdcard/rootix/`) to secure, app-private **Scoped Storage** (`Context.getExternalFilesDir()`). This prevents other applications from accessing your Rootix configuration and logs.
*   **Backup Protection:** `android:allowBackup` is set to `false` to prevent sensitive data extraction via ADB backups (MASVS-STORAGE-1).
*   **Secure File Sharing:** Uses `FileProvider` for secure, permission-based file sharing instead of vulnerable `file://` URIs.

### 🌐 MASVS-NETWORK: Network Communication
*   **Enforced TLS:** `android:usesCleartextTraffic` is disabled globally. All network communications are forced over **HTTPS** (TLS 1.2+).
*   **Hardened Service Endpoints:** Internal services (Weather API, Connectivity checks) have been upgraded to secure HTTPS endpoints (MASVS-NETWORK-1).

### ⚙️ MASVS-PLATFORM: Platform Interaction
*   **Signature-Level Protection:** Implemented a custom permission `com.rootix.launcher.permission.RECEIVE_CMD` with `protectionLevel="signature"`. This ensures only apps signed with the same developer key can programmatically send commands to the launcher.
*   **Intent Security:** All system-bound `PendingIntents` use the `FLAG_IMMUTABLE` flag to prevent intent redirection attacks (Android 12+ requirement).
*   **Receiver Security:** All Broadcast Receivers are registered with appropriate export flags (`RECEIVER_EXPORTED` or `RECEIVER_NOT_EXPORTED`) to prevent unauthorized external triggers.

### 🛠 MASVS-CODE: Code Quality & Build Settings
*   **Minification & Obfuscation:** Release builds have R8/Proguard enabled (`minifyEnabled true`) to shrink resources and obfuscate code, making reverse engineering more difficult (MASVS-RESILIENCE-1).
*   **Foreground Service Security:** Updated to comply with Android 14's strict foreground service types (`specialUse`, `mediaPlayback`).

---

## 🤖 AI Command

Rootix ships with a built-in AI assistant. On the home screen, type:

```bash
.ai who are you
.ai open whatsapp
```

It randomly picks from 35+ pooled models across two backends and automatically retries a
different model if one fails. See [COMMANDS.md](COMMANDS.md) for full details and the complete
command reference table.

## 🔗 Useful links

**Official Group**&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-->&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**[Telegram](https://t.me/DOMINATOR_XYZ)**<br>
**Wiki**&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-->&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**[GitHub.com](https://github.com/DominatorStufs/Rootix/wiki)**<br>

## 📚 Open Source Libraries
* [**CompareString2**](https://github.com/fAndreuzzi/CompareString2)
* [**OkHttp**](https://github.com/square/okhttp)
* [**HTML cleaner**](http://htmlcleaner.sourceforge.net/)
* [**JsonPath**](https://github.com/json-path/JsonPath)
* [**jsoup**](https://github.com/jhy/jsoup/)
