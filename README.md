# Vibe Bell

Android app that vibrates three short times at each whole hour during a configurable time window. It does not play notification sounds and works offline.

## Default settings
- Disabled until the user enables it
- Time window: 08:00–22:00
- Three short vibration pulses
- No sound, no network permission, no account

## Build with GitHub Actions
1. Upload the complete contents of this ZIP to the root of a new GitHub repository.
2. Open **Actions** and enable workflows if GitHub asks.
3. The workflow runs on every push; you can also choose **Build Vibe Bell APK** → **Run workflow**.
4. Open the completed run and download the `VibeBell-debug-apk` artifact.
5. Extract the artifact ZIP and install `app-debug.apk` on your Android phone.

## Notes
- Exact alarm access may need to be granted in Android settings.
- Android battery management and manufacturer-specific background restrictions can still affect delivery.
- Vibration intensity depends on device hardware and system settings; the app requests the strongest supported amplitude.
- If start and end times are equal, the app treats the window as all day.
- The debug APK is for testing. A release APK should be signed before distribution.
