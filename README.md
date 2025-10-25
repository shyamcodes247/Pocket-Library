Libraries Used in this Pocket Library include:
1. Open Library API
The Open Library API was chosen for this project for its external data source as it provides free, reliable and expansive access to book metadata like title, authors, publication year and cover image. Usage of this library eliminates the manual and laborious process of creating and maintaining a book database, allowing for convenience and efficient time usage. Open Library is beneficial as it ensures realism in a real world scenario and also allows for scalability. Users can search for actual books through API calls where the query yields countless results that appear dynamically - which would otherwise be limited without it. This improves usability and realism of the project while also adding educational value for future use. 

2. Firebase
Firebase Firestore was used as the cloud storage solution as it offers seamless synchronisation, high availability and easy integration with Android using Kotlin extensions. This eliminated the requirement of building and hosting a custom backend solution. It bought great value to this project by providing users with real-time data updates across devices where they can save books to persist in the cloud. Using Firebase simplified the database management aspect of this project whilst also displaying the integration of a cloud-based NoSQL backend within Android Studio.

3. Coil
Coil was chosen as the image loading library as it is lightweight, optimised for Kotlin and Compose-native. It smoothly handles image fetching, caching and rendering from URLs such as the Open Library book cover images used in this project. This library has significantly improved the performance, memory efficiency and reduced boilerplate code simultaneously. The 'AsyncImage' composable in Coil integrated efficiently with Jetpack Compose which allows for fast and responsive UI rendering with minimised configuration.

4. DataStore
DataStore was used for its persistent storage of small preferences such as the last search query or scroll position of the saved page. It was used in this project as it ensures that the user preferences and UI states are preserved across configuration changes and app relaunch. Using the library allowed for increased reliability, maintain user context and improve the user experience by letting users continue where they left off.
