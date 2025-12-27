# Ex2 - 2D Map & BFS Algorithms
**Introduction to Computing | Ariel University**

## 📝 Project Overview
This project implements a generic **2D Map System** with a graphical user interface (GUI), designed using strict **Object-Oriented Programming (OOP)** principles.
The system is built upon defined interfaces (`Map2D`, `Pixel2D`) to ensure modularity, reusability, and a clean code structure.
The core logic utilizes efficient graph algorithms—specifically **BFS (Breadth-First Search)**—to solve pathfinding and area-filling problems on a grid.

## ✨ Key Features
Beyond basic pathfinding, the application supports advanced map manipulations:
* **Pathfinding:** Calculation of the Shortest Path (BFS) and All-Paths distance maps.
* **Flood Fill:** A "Paint Bucket" tool to fill areas with colors.
* **Map Editing:** Draw points, lines, circles, and rectangles dynamically.
* **File Operations:** Save and Load maps from text files.
* **Advanced Tools:**
    * **Merge:** Combine two maps into one.
    * **Rescale:** Resize the map dimensions (Upscale/Downscale) dynamically.

## 📂 Project Structure & Classes

### Interfaces (The Contract)
* **`Map2D.java`**: Defines the required methods for any generic 2D map (e.g., `setPixel`, `shortestPath`, `fill`).
* **`Pixel2D.java`**: Defines the methods for a 2D coordinate/point (X, Y) and distance calculations.

### Implementation
* **`Map.java`**: The main logic class that implements `Map2D`. It manages the 2D array and executes the BFS algorithms.
* **`Index2D.java`**: Implements `Pixel2D`. Represents a specific point `(x, y)` on the grid.
* **`Ex2_GUI.java`**: The graphical interface class. Uses `StdDraw` to visualize the `Map2D` object and handle user input.

### Testing
* **`MapTest.java` & `Index2DTest.java`**: JUnit 5 test classes ensuring the correctness of the logic and algorithms.
---

## 📷 Screenshots & Capabilities

### 1. Algorithms in Action
On the left: **Shortest Path** (Green) avoiding obstacles.
On the right: **All Distances** view spreading from a center point.
![Algorithms Split View](images/algo_split_view.png)

### 2. Drawing Tools
Demonstration of the drawing capabilities: Lines, Circles, and Rectangles.
*(Menu open showing available shapes)*
![Drawing Tools](images/drawing_demo.png)

### 3. Map Operations
The system allows merging maps and rescaling.
*(Menu open showing: Save, Merge, Rescale, New)*
![Map Menu](images/map_options.png)

---

## 🚀 How to Run
1.  Clone the repository.
2.  Run `Ex2_GUI.java` as the main class.
3.  Use the bottom toolbar to navigate between **Map** operations, **Colors**, **Drawing** tools, and **Algorithms**.
