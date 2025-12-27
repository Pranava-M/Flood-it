# Flood-It Simon Style 🎮

A modern **Java Swing** implementation of the classic **Flood-It** puzzle game, inspired by **Simon-style color interaction** and clean UI principles. The goal is simple: *flood the entire grid with a single color in the minimum number of moves.*

---

## ✨ Features

* 🎨 **Color-based Flood Fill gameplay**
* 🧠 **Logical puzzle mechanics** (non-greedy optimal play encouraged)
* 🖥️ **Java Swing GUI** with responsive grid
* 🎯 **Move counter & win detection**
* 🔁 **Restart / Reset support**
* 📐 **Configurable grid size**
* 🚫 No external libraries required

---

## 🧩 How the Game Works

1. The game starts with a randomly colored grid.
2. The top-left cell is your starting region.
3. Click a color button to flood-fill the connected region.
4. Each color choice counts as **one move**.
5. The objective is to make the **entire grid a single color** within minimal moves.

---

## 🛠️ Technologies Used

* **Java (JDK 8+)**
* **Java Swing** for GUI
* **BFS / DFS Flood Fill Algorithm**

---

## 📂 Project Structure

```
FloodItSimonStyle.java   // Complete single-file implementation
README.md               // Project documentation
```

---

## ▶️ How to Run

### 1. Compile

```bash
javac FloodItSimonStyle.java
```

### 2. Run

```bash
java FloodItSimonStyle
```

> Make sure Java is installed and added to your system PATH.

---

## 🧠 Algorithm Used

* **Flood Fill (BFS / DFS)** starting from cell `(0,0)`
* Expands region only if adjacent cells match the chosen color
* Efficient traversal using a queue / recursion

**Time Complexity:** `O(N²)` per move

---

## 📸 UI Preview (Concept)

```
🟥 🟦 🟩 🟨
🟥 🟥 🟦 🟩
🟨 🟦 🟩 🟥
🟩 🟨 🟥 🟦
```

---

## 🚀 Possible Enhancements

* 🤖 AI Bot (Shortest-path solver)
* 🌓 Light / Dark mode
* ⏪ Undo / Redo moves
* 🏆 Scoreboard & best-move tracking
* 🎮 Multiplayer or timed mode

---

## 👨‍💻 Author

**Pranav Machireddy**
Computer Science Student | Java & Algorithm Enthusiast

---

## 📜 License

This project is for **educational purposes**. Feel free to modify and experiment.

---

⭐ If you like this project, consider starring the repository!
