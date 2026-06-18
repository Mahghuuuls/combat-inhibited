# Changelog

## [1.1.0] - 2026-06-18

### Added
- Added Encounter Clear, which removes Inhibited after the final matching visible hostile is defeated, including enemies killed at range.
- Added global entity allow and exclude lists shared by all entity-filter modules.
- Added independent line-of-sight settings for Near Enemy, Near Boss, and Encounter Clear.
- Added scanner optimization options for Near Enemy and Near Boss.
- Added effect modes, refresh thresholds, and reapplication limits to Near Boss.

### Changed
- Updated the development toolchain to Java 25 and Gradle 9.5.1 using CleanroomMC's ForgeDevEnv. The released mod remains compatible with Java 8.
- Changed some default configuration options.
