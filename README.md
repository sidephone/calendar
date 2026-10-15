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
  - Year view
  - Month view
  - Day view (with events)
  - Add event view
    - All day events
    - Recurring events
  - Delete operations
  - Controls screen (based on the current tile)
  - Options rail
    - tile override
    - zoom in
    - zoom out
- Touchscreen UI
    - do it
    - make it optional
    - add a toggle in the settings rail; reset it when the tile changes
- Notifications
- Add license file