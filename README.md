📖 Description
This single-file app demonstrates six distinct Android features — each isolated in its own Composable screen — all wired together through a central NavHost. It's designed as a practical reference for developers learning how modern Android handles media, documents, permissions, and deep linking without relying on deprecated startActivityForResult.

🗂️ Project Structure
app/
└── src/main/java/com/example/intentsday2/
    └── MainActivity.kt
        ├── MainActivity              # Activity entry point; registers TakePicture launcher & FileProvider URI
        ├── AppNavigation()           # NavHost with 6 routes including a deep link route
        ├── HomeScreen()              # Hub with buttons navigating to all feature screens
        ├── CameraScreen()            # Captures a photo using TakePicture contract + FileProvider
        ├── GalleryScreen()           # Picks an image from gallery using GetContent contract
        ├── DocumentPickerScreen()    # Opens any file using OpenDocument contract
        ├── PermissionScreen()        # Requests RECORD_AUDIO runtime permission
        ├── DetailsScreen()           # Deep link destination displaying a dynamic item ID
        └── CaptureImageScreen()      # Standalone composable (legacy/commented-out alternative)

🧠 Key Concepts Covered
ConceptAPI / ImplementationCamera CaptureActivityResultContracts.TakePictureFileProvider URIFileProvider.getUriForFile() + provider in manifestImage PickerActivityResultContracts.GetContent with "image/*" MIMEDocument PickerActivityResultContracts.OpenDocument with "*/*"Runtime PermissionActivityResultContracts.RequestPermissionPermission CheckContextCompat.checkSelfPermission()Deep LinksnavDeepLink { uriPattern = "https://example.com/details/{itemId}" }Compose NavigationNavHost + rememberNavController() + composable()Edge-to-Edge UIenableEdgeToEdge() + systemBarsPadding()Activity-level vs Composable-level launchersBoth patterns demonstrated side by side

🖥️ Screens
🏠 Home Screen
Central navigation hub with five buttons leading to each feature screen.
📷 Camera Screen

Creates a File in the external pictures directory
Converts it to a content:// URI via FileProvider
Launches the system camera using the TakePicture contract
Shows a Toast with the saved image URI on success

🖼️ Gallery Screen

Uses the GetContent contract with "image/*" MIME type
Lets the user pick any image from their gallery or file manager
Displays the selected image URI via Toast

📄 Document Picker Screen

Uses OpenDocument contract with "*/*" to allow any file type
Returns a persistent URI to the selected file

🔐 Permission Screen

Checks current status of RECORD_AUDIO permission on composition
Requests the permission at runtime using the RequestPermission contract
Tracks grant state reactively with mutableStateOf

🔗 Details Screen (Deep Link)

Reachable via in-app navigation (details/456) or an external URI
URI pattern: https://example.com/details/{itemId}
Displays the dynamic itemId argument extracted from the route


🔑 Notable Implementation Details

Dual launcher patterns: MainActivity registers TakePictureLauncher at the Activity level (lifecycle-safe), while CameraScreen also registers its own launcher at the Composable level using rememberLauncherForActivityResult — both patterns are shown for comparison.
FileProvider setup: The app uses FileProvider to securely share file URIs with the camera app, a mandatory pattern on Android 7+.
CaptureImageScreen is a commented-out standalone composable — preserved as a simpler alternative reference to the full CameraScreen.


🚀 Getting Started
Prerequisites

Android Studio Hedgehog or later
Minimum SDK: 21 (Android 5.0)
Kotlin 1.9+
Jetpack Compose BOM

Required Manifest Additions
xml<!-- FileProvider for camera URI sharing -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>

<!-- Permissions -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
Setup
bashgit clone https://github.com/your-username/android-advanced-intents-compose.git
Open in Android Studio, sync Gradle, and run on a physical device or emulator.

🛠️ Tech Stack

Language: Kotlin
UI: Jetpack Compose + Material 3
Navigation: Jetpack Navigation Compose
Activity Results: AndroidX Activity Result API (rememberLauncherForActivityResult)
File Sharing: AndroidX FileProvider
Architecture: Single Activity, multi-screen Composable


📚 What I Learned

How to use the modern Activity Result API instead of the deprecated onActivityResult
How FileProvider works to safely expose file URIs to external apps
The difference between registering launchers at the Activity level vs inside a Composable
How to handle runtime permissions reactively in Compose using mutableStateOf
How to define and trigger deep links in Compose Navigation


📄 License
This project is open source and available under the MIT License.