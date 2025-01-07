# Android Commons Library

[![Release](https://jitpack.io/v/Zain-S/android-commons-library-kotlin.svg)](https://jitpack.io/#Zain-S/android-commons-library-kotlin)

**Android Commons Library** is a lightweight Android library providing common extensions, utilities, and features like a permission launcher to simplify Android development.

## Features
- **Extensions**: Simplify repetitive tasks with helpful Kotlin extensions.
- **Utilities**: Ready-to-use utility classes and functions.
- **Permission Launcher**: A streamlined API for managing runtime permissions.
- **ViewBinding Support**: Easily bind views with built-in support for ViewBinding.

## Getting Started

### 1. Add the JitPack Repository
Add the JitPack repository to your root `build.gradle` or `settings.gradle` file:
```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
### 2. Add the Dependency
Add the library dependency in your `module-level` `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.Zain-S:android-commons-library-kotlin:1.7'
}
```

Replace 1.7 with the latest release version if available.

## Usage
### Extensions
```
// Example: View visibility extension
view.visible()  // Sets the view's visibility to VISIBLE
view.gone()  // Sets the view's visibility to GONE
```
### Utilities
```
// Example: Date formatting utility
val formattedDate = convertDateFromFormatToFormat(dateString, inputFormat, outputFormat)
```
### Permission Launcher
Simplify permission requests with a built-in launcher:
```
private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var granted = true
            permissions.entries.forEach {
                if (!it.value) granted = false
            }
            if (granted)
              // Permission granted  
        }
// function in activity
fun performAction(){
  initLocationPermissionChecks(permissionLauncher) {
    // Permission granted
  }
}
```

## Contributions
Contributions are welcome! Feel free to submit issues or pull requests. For major changes, please open an issue to discuss the changes beforehand.

Author
Developed with ❤️ by Zain S.
---
