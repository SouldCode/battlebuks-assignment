# Scoreboard Assignment Setup

Follow these simple steps to set up and run the project:

## Setup Instructions

1. **Clone or Open Project**
   - Open the project folder in Android Studio.
   - Wait for Gradle to finish syncing.

2. **Add Firebase configuration**
   - Go to Firebase Console and create a new Android App in your project.
   - Use package name: `com.souldcode.assignment`
   - Download the `google-services.json` file.
   - Copy/Paste the `google-services.json` file into the `app/` folder of this project directory.

3. **Configure Firestore**
   In your Firebase Console, enable **Cloud Firestore**.
   Create a database in test mode (or configure security rules to allow read/write access for development).
   Create a collection named `players`.

4. **Run the App**
   - Build and run the app on an Android Emulator or a physical device.

5. **Interact with the Scoreboard**
   - Click **"Add Players"** to write 1,000 dummy players into your Firestore collection.
   - Click **"Update Score"** to start real-time updates and see rankings dynamically shift with slide animations and border glow pulses.
   - Scroll down the scoreboard to trigger infinite scroll pagination (loads 20 items per page).



## Design Note
   - **How you avoid**
   --I have perform database operations in the background using Kotlin Coroutines with Dispatchers.IO. This keeps the Main Thread free and helps the UI remain smooth and responsive.

   - **Unnecessary Recompositions**
   -- I have use stable keys in LazyColumn and derivedStateOf where needed, so Compose updates only the necessary UI elements and avoids unnecessary recompositions.
   
   - **Memory Leaks**
   -- I have use viewModelScope to manage coroutines based on the ViewModel lifecycle. I have also remove active database listeners using awaitClose when they are no longer needed, which helps prevent memory leaks.

   - **How This Behaves** 
   -- On Screen Rotation: I have used a retained ViewModel to preserve the state in memory and keep the Firestore flow collection active, preventing any data re-fetching or loading states on rotation.When App goes to Background: I have used collectAsStateWithLifecycle() to pause UI flow collection, which is a standard Compose practice to safely stop observing changes while the app is in the background.

   - **How To Scale**
     --1K Users: I have already implemented logic for the 1k User. I have used Firestore queries and direct document updates, as Firestore handles this scale natively out of the box.
     --100K Users: I have implemented cursor-based pagination (loading 20 items on-demand) and would add Firestore indexes on the score field while ensuring each user writes only to their own document to prevent concurrency bottlenecks.











