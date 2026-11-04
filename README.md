# Sidephone Calendar
An offline calendar app for Android phones with small screens, and physical buttons.

Optimized to use on Sidephone SP-01 with all keypad tiles.

## Development Setup
Just clone the project and import it in Android Studio. It depends on standard AndroidX components and RoomDB 3. The proper versions that work together are in `libs.versions.toml`, so Android Studio should download and use them without any extra configuration.

Tested with Android Studio Rabbit 2 | 2026.2.2

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE.txt) file for details.

Calendar icons created by [Icon home - Flaticon](https://www.flaticon.com/free-icons/calendar).

## TODO
- Recurring events (+ make sure to edit and delete each occurrence separately)
- Multi-day events (+ display them correctly on the day grid)
- Sync with Proton?
- Sync with Google?