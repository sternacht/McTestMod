---
name: run-client
description: Launch the Minecraft Forge dev client for this mod (firstmod, MC 1.19.3) so the user can manually test in-game. Use whenever the user asks to "啟動客戶端", "run the client", "launch the game", or similar, for this project.
---

# Run Client

Launch the Forge userdev Minecraft client so the user can manually test mod changes in-game.

## How to launch

Run in the background (the user will play for a while and close the window themselves):

```
./gradlew runClient
```

Always use `run_in_background: true` — this command blocks until the game window is closed and can run for many minutes.

## Interpreting the result

This command almost always reports `status: failed` / non-zero exit code **even on a fully successful run**, because the single-use Gradle daemon (`org.gradle.daemon=false` in `gradle.properties`) reports "daemon disappeared unexpectedly" once the game's JVM exits. This is cosmetic and NOT a real failure.

When the background task notification arrives:

1. Read the output file.
2. Check for the line `[Render thread/INFO] [minecraft/Minecraft]: Stopping!` — if present, the game launched, ran, and was closed normally (by the user). Treat this as a successful run regardless of the reported exit code or the trailing `FAILURE: Build failed with an exception` / "daemon disappeared" message at the end of the log — do not report this as an error to the user.
3. Only if `Stopping!` is absent should you treat the failure as real — search the log for `Exception`, `ERROR`, or `FAILURE` lines that occur *before* any `Stopping!`/shutdown sequence, and report those.

## Good practice

- Briefly suggest 1-3 concrete things worth testing in-game based on what was just changed (e.g. "check the new recipe", "feed the wolf and confirm the pack gets angry"), but don't block on it.
- Don't re-run the client speculatively — only when the user asks.
