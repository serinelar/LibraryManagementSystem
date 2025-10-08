# 📚 Library Management System  

A full-featured **Library Management System** built entirely in **Java**, showcasing clean code architecture, data persistence, and a polished Swing-based GUI.  
This project demonstrates the ability to design a modular, maintainable, and user-friendly desktop application from scratch — ideal for both learning and real-world small library use.

---

## Overview  

The **Library Management System** allows administrators to manage books, members, and borrowing operations efficiently.  
It supports multiple functional panels, advanced search, borrowing limits, overdue tracking, and a reservation queue for unavailable books.

---

## Features  

### Book Management
- Add, edit, and delete books  
- Track available and total copies  
- Advanced search by title, author, or genre  
- View real-time reservation queue per book  

### Member Management  
- Add, edit, and remove members  
- Member tiers: *Regular* and *Premium*, each with different borrowing limits  
- Borrowing and reservation history tracking  

### Borrow & Return System  
- Borrow books with date tracking  
- Countdown system for borrowing period  
- Return and renew functionality  
- Overdue reminders for books nearing deadline  

### Reservation System  
- Reserve books currently unavailable  
- Displays queue positions  
- Auto-notifies next member in line when a book is returned  

### Recommendation Panel  
- Suggests books based on genre or previous member interactions  
- Built-in logic for personalized recommendations  

### Advanced Search Filters  
- Real-time multi-field filtering (title, author, genre)  
- Case-insensitive and dynamic updates in the book list  

### Modern Swing GUI  
- 4 unified panels: Books, Members, Borrow/Return, Recommendations  
- Clean, intuitive, and responsive layout  
- Hover effects and dynamic updates  
- Consistent color palette and margin alignment  

---

## Project Architecture  
```bash
LibraryManagementSystem/
├── src/
│   ├── com/serine/library/
│   │   ├── model/
│   │   │   ├── Book.java
│   │   │   ├── Member.java
│   │   │   ├── BorrowRecord.java
│   │   │   └── Reservation.java
│   │   ├── service/
│   │   │   └── LibraryService.java
│   │   ├── ui/
│   │   │   ├── LibraryAppGUI.java
│   │   │   ├── BookPanel.java
│   │   │   ├── MemberPanel.java
│   │   │   ├── BorrowPanel.java
│   │   │   ├── ReturnPanel.java
│   │   │   ├── RecommendationPanel.java
│   │   │   └── ReservationQueueDialog.java
│   │   └── util/
│   │       └── DataStore.java
└── README.md
```

Each layer is independent and testable:
- **Model layer** → Defines entities (Book, Member, etc.)  
- **Repository layer** → Handles read/write and in-memory data storage  
- **Service layer** → Centralized logic for borrowing, returning, and reserving  
- **UI layer** → Panels (BookPanel, BorrowPanel, MemberPanel, RecommendationPanel)

---

## Core Classes & Responsibilities  

| Class | Responsibility |
|-------|----------------|
| `Book` | Represents a book with copies, genre, and reservation queue |
| `Member` | Represents a library user with membership tier |
| `LibraryService` | Core logic for borrowing, returning, reserving, and notifications |
| `BookRepository` | Persistent storage and retrieval of books |
| `MemberRepository` | Handles member data |
| `BorrowPanel`, `BookPanel`, `MemberPanel`, `RecommendationPanel` | GUI panels for each feature |
| `LibraryApp` | Main entry point, initializes the full interface |

---

## Data Persistence  

Data is stored through in-memory repositories with easy extension to file or database systems.  
All panels read/write from the same repositories, ensuring data consistency.

---

## Testing  

Unit tests confirm:
- Correct reservation queue order  
- Proper book availability updates  
- Borrowing limits enforced by membership tier  
- Accurate search and filtering results  

You can run tests directly using any Java IDE (e.g., IntelliJ, VS Code, or Eclipse).

---

## How to Run  

```bash
# Clone the repository
git clone https://github.com/serinelar/LibraryManagementSystem.git

# Navigate into the project folder
cd LibraryManagementSystem

# Compile and run
javac -d bin src/com/serine/library/ui/LibraryAppGUI.java
java -cp bin com.serine.library.ui.LibraryAppGUI
```

Or simply run it from your IDE (e.g., IntelliJ, Eclipse, or Visual Studio Code).

---

## Technologies Used
- Java 17
- Swing (GUI Framework)
- Object-Oriented Programming (OOP)
- Collections Framework
- Event Handling and Listeners
- Unit Testing

---

## Future Improvements
- Add database (e.g., MySQL) persistence layer
- Include login system with user roles
- Generate borrowing/return reports as PDFs
- Modernize the UI with JavaFX

---

## Acknowledgements
This project was built as part of a full portfolio development journey to demonstrate:

- Clean architecture design
- GUI development skills
- Problem-solving and system design abilities

---

## Author

Serine Laroui
Master’s in Engineering of Intelligent Computer Systems
📍 GitHub: @serinelar
