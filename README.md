# Sidephone Calendar
An offline calendar app for Android phones with small screens, and physical buttons.

Optimized to use on Sidephone SP-01 with all keypad tiles.

## Development Setup
Just clone the project and import it in Android Studio. It has no extra dependencies, so you can build and run it directly in the emulator or on a real device.

Tested with Android Studio Rabbit 1 | 2026.2.1

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.

## TODO
- Get rid of the "require" in Database.kt
- UI
  - View events
    - ~~in month view: show event count per day~~
    - ~~in day view: show event list for the day~~
    - single event view
  - Add/Edit event
    - All day events
    - Recurring events
  - Delete event + confirmation dialog + touch-n-hold support
  - Help screen (based on the current tile)
  - Colors
- Touchscreen support
    - swipe left/right to change pages
- Notifications
- Add license file