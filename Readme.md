# Cat Breeds App

This is an Android application developed in **Kotlin** using **Jetpack Compose** and the **Cat API**. It allows users to explore different cat breeds, search by breed name, view detailed information, and mark favorites. This project was developed as part of a coding challenge from **Sword Health**.

---

##  Features

###  Functional Requirements
- [x] **Breed List Screen** with:
  - Cat image
  - Breed name
- [x] **Search Functionality** to filter by breed name
- [x] **Favorite Button** to mark/unmark breeds
- [x] **Favorites Screen** showing:
  - Favorite breeds
  - Average lifespan of all favorites
- [x] **Detailed View** with:
  - Name, origin, temperament, description
  - Add/remove from favorites
- [x] **Navigation using Jetpack Navigation Component**
- [x] **Navigation to detail screen from all entries**

---

## Technical Specifications

- Architecture: **MVVM**
- UI: **Jetpack Compose**
- Networking: **Retrofit + Coroutines**
- Pagination: Yes, using snapshotFlow and LazyColumn
- State Management: `mutableStateListOf`, `remember`, `derivedStateOf`
- Error Handling: Snackbar + Log + Try/Catch
- Offline Support: Implemented using an alternative approach (Room not used)
- Testing: Base structure prepared for Unit & Compose UI tests

---

##  Bonus Implementations

- [x] Error handling for API and connection failures
- [x] Pagination for cat breeds
- [x] Modular and clean design pattern
---

##  Technical Decisions

- Used **Jetpack Compose** for modern, reactive UI design.
- Adopted **MVVM** to separate concerns and ensure testability.
- Used **mutableStateListOf** and **derivedStateOf** for efficient recomposition.
- Applied **SnapshotFlow** for pagination observing scroll state.
- Implemented **Snackbar** for user feedback.
- Used the **The Cat API** `v1/breeds`, `v1/favourites`, and `search` endpoints.

---

##  How to Run

1. Clone the repository  
2. Build and Run the project in Android Studio

---

## API Reference

- [TheCatAPI Docs](https://thecatapi.com/)
- Authentication: API Key via Header `x-api-key`

---

##  Author

Robson Alves  
Mobile Developer | Kotlin + Compose Enthusiast  
[LinkedIn](https://www.linkedin.com/in/robson-alves04/)  [GitHub](https://github.com/robsonalves04)

---

##  License

This project is part of a private coding challenge and is not intended for commercial use.