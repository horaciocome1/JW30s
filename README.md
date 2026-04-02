# This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

## About the project

This app emulates the 30 seconds board game.

Here is a brief description of the game:

**30 Seconds** is a fast-paced team party game where one player tries to get their teammates to guess as many words as possible from a card within 30 seconds by describing them without saying the actual word or obvious variations. Each card contains a mix of general knowledge topics like famous people, places, movies, and brands, and teams score points for every correct guess while racing along a board toward the finish. The game emphasizes quick thinking, clear communication, and energy, making it especially popular in group and social settings.

What is different about this app is that it's focused on Bible and Jehovah's Witnesses related topics. The app is designed to be used in a group setting, where one person can act as the "describer" and the others can be the "guessers". The describer will have a card with a word or phrase related to the Bible or Jehovah's Witnesses, and they will have 30 seconds to describe it to the guessers without using the word itself or any obvious variations. The guessers will try to guess the word based on the describer's clues, and points will be awarded for each correct guess. The game can be played in teams or individually, and it's a fun way to test knowledge of Bible-related topics while also encouraging quick thinking and communication skills.

The cards are images that are stored in cards folder. The app uses a simple algorithm to randomly select a card for the describer to use during their turn. In this current version the app doesn't care about which team is playing, it just randomly selects a card for the describer. The app also includes a timer that counts down from 30 seconds, and it will automatically end the turn when the time runs out. The guessers can keep track of their points, and the app can be used to play multiple rounds of the game. The app is designed to be simple and easy to use, making it a great option for group gatherings.

Since the images can be of hi resolution, the app uses kotlin multiplatform's image loader library called "Kamel" to load the images efficiently and avoid memory issues. Kamel is a powerful image loading library that supports multiple platforms, including Android and iOS, and it provides features like caching and efficient memory management to ensure smooth performance when loading images in the app. By using Kamel, the app can handle high-resolution images without causing crashes or slowdowns, providing a better user experience for players.

The app also uses Jetpack Compose for the UI, which allows for a modern and responsive design. The UI is designed to be simple and intuitive, with clear buttons and a timer display. The app also includes a score tracker, so players can keep track of their points throughout the game. Overall, this app provides a fun and engaging way to play the 30 Seconds game with a Bible-related twist, while also utilizing modern technologies like Kotlin Multiplatform and Jetpack Compose for an optimal user experience.

The app uses room database to enhance the random card selection algorithm. The app stores the cards in a local database, and it keeps track of which cards have been used in previous rounds. This allows the app to avoid repeating cards until all cards have been used, providing a more varied and engaging gameplay experience. By using a database to manage the cards, the app can ensure that players are always presented with new and interesting challenges during their turns as describers.

The app has a home screen where player can start a new game. When they start a new game, they are taken to the game screen where the first card is displayed for the describer and there is a timer counting down from 30 seconds. There is also a shuffle button to show the next card.

The game screen uses a vertical pager to display the cards. Users can swipe down to see the previous card and swipe back up to see the cards that have been shown. But he cannot swipe up to see the next card, they have to click the shuffle button to see the next card. This is to prevent users from skipping cards without playing them.

When the timer runs out, the device vibrates to indicate that the turn is over. And also play the notification sound to indicate that the turn is over. The app uses the device's vibration and notification system to provide feedback to the players, enhancing the overall gaming experience.

There is also a button to go back to the home screen, allowing players to start a new game or exit the app. The home screen provides a simple and welcoming interface for players to begin their gaming session, while the game screen offers an engaging and interactive environment for playing the 30 Seconds game with Bible-related topics. Overall, the app is designed to be user-friendly and enjoyable for players of all ages, making it a great choice for group gatherings and family game nights.
When users click on this button, they are asked to confirm if they want to go back to the home screen. This is to prevent users from accidentally leaving the game screen and losing their progress. If they confirm, they are taken back to the home screen where they can start a new game or exit the app. This confirmation step adds an extra layer of user experience, ensuring that players don't unintentionally disrupt their gaming session while navigating through the app.

On game screen the device's back button is disabled to prevent users from accidentally leaving the game screen and losing their progress. This design choice ensures that players can focus on the game without worrying about unintentionally exiting the app. Instead of using the back button, players can use the provided navigation options within the app, such as the button to go back to the home screen, which includes a confirmation step to prevent accidental exits. This approach enhances the overall user experience by providing a more controlled and intentional navigation flow within the app.

## Architecture

This app follows a clean architecture approach, with a clear separation of concerns between the different layers of the app. The app is structured into three main layers: the presentation layer, the domain layer, and the data layer.
- The presentation layer is responsible for the UI and user interactions. It includes the composables that make up the game screen and home screen, as well as the view models that manage the state of the UI.
- The domain layer is responsible for the business logic of the app. It includes the use cases that define the operations that can be performed in the app, such as shuffling the cards
- The data layer is responsible for managing the data of the app. It includes the repositories that provide access to the data, as well as the database that stores the cards and their usage history.