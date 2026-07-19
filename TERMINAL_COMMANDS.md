# Rootix — Terminal Command Reference

This is the full list of commands you can type inside the Rootix terminal itself
(not to be confused with `COMMANDS.md`, which covers build/deploy commands for
developers). Type `help` inside Rootix at any time to see this list on-device,
or `help [command]` for full details + usage examples of one command.

New in this fork: **`ai`** / **`.ai`** and **`neofetch`** — see the highlighted
section below.

## 🤖 New commands

| Command | What it does |
|---|---|
| `ai [text]` or `.ai [text]` / `.AI [text]` | Sends `[text]` to an AI model. A random model is picked first; if it errors out, Rootix automatically retries with a different model, then falls back to a secondary provider. `ai open [appName]` (e.g. `ai open whatsapp`) skips the AI entirely and just launches the named app directly, if it's installed. |
| `neofetch` | Prints a neofetch-style system info summary (device, OS, uptime, CPU, memory, storage, battery, resolution). Fully built-in — no BusyBox, no root, no separate install step required. |

## 📦 All commands

| Command | Description |
|---|---|
| `ai` | Ask an AI a question (random model, auto-fallback). Shortcut: `.ai` / `.AI` |
| `airplane` | Toggle airplane mode |
| `alias` | Create/manage command aliases |
| `apps` | Manage apps — **`apps -ls` lists every installed app** |
| `bbman` | Install/remove BusyBox (enables standard Linux shell commands) |
| `beep` | Emit a beep sound |
| `bluetooth` | Toggle bluetooth |
| `brightness` | Set screen brightness |
| `calc` | Basic calculator |
| `call` | Call a contact/number |
| `changelog` | Show the changelog for the current version |
| `clear` | Clear the screen |
| `cntcts` | Manage contacts |
| `config` | Get/set launcher configuration options |
| `ctrlc` | Interrupt the current shell process |
| `data` | Toggle mobile data |
| `devutils` | Developer utilities |
| `donate` | Support the developer |
| `exit` | Reset the default launcher and close Rootix |
| `flash` | Toggle the flashlight |
| `help` | List all commands, or show help for one command |
| `htmlextract` | Extract text from HTML pages |
| `location` | Show current location |
| `music` | Control music playback |
| `neofetch` | Built-in system info summary |
| `notes` | Add/manage notes |
| `notifications` | Manage which apps' notifications are shown |
| `open` | Open a file |
| `rate` | Leave feedback on the Play Store |
| `refresh` | Refresh apps, aliases, music, contacts |
| `regex` | Manage saved regexes (used by rss/reply/etc.) |
| `reply` | Reply to the last notification from an app |
| `restart` | Restart Rootix and reload settings |
| `rootix` | Rootix info/maintenance (`-about`, `-reset`, `-rm`, `-sourcecode`, `-folder`, ...) |
| `rootixt` | Built-in text editor |
| `rootixweather` | Weather widget settings |
| `rss` | Manage RSS feeds |
| `search` | Web/Google search |
| `share` | Share a file |
| `shortcut` | Manage app shortcuts |
| `status` | Battery, wifi, and mobile data status |
| `theme` | Apply/manage themes |
| `time` | Print the current time |
| `tutorial` | Open the tutorial page on GitHub |
| `uninstall` | Uninstall an app |
| `username` | Change username/device name |
| `vibrate` | Vibrate the device |
| `volume` | Set stream volume |
| `wifi` | Toggle wifi |

Source: [github.com/DominatorStufs/Rootix](https://github.com/DominatorStufs/Rootix)
