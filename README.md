This project implements a simple spreadsheet application in Java that allows users to manage a grid of cells with rows and columns. Users can input text, numbers, and formulas to perform calculations, similar to Excel.
Ex2Sheet Class, This class manages the spreadsheet grid and handles cell operations.

Constructor
Ex2Sheet(int width, int height): Initializes the spreadsheet with the given width and height.
Ex2Sheet(): Initializes the spreadsheet with default size.
Key Methods
boolean isIn(int x, int y): Checks if the given cell coordinates are within bounds.
int width(): Returns the number of columns.
int height(): Returns the number of rows.
void set(int x, int y, String value): Sets the value in a cell, validates and stores text, numbers, or formulas.
Cell get(int x, int y): Retrieves the cell at specific coordinates.
String value(int x, int y): Evaluates and returns the value of a cell.
void eval(): Evaluates all formulas in the spreadsheet.
void save(String fileName): Saves the spreadsheet to a file.
void load(String fileName): Loads spreadsheet data from a file.
SCell Class
Represents a single cell in the spreadsheet, capable of storing text, numbers, or formulas.
Constructor
SCell(String s): Creates a cell with the given data.
Features:
Text and Numbers: Input plain text or numbers in any cell.
Formulas: Perform calculations using formulas like =A1+B2.
Error Detection:
ERR_FORM! for invalid formulas.
ERR_CYCLE! for circular dependencies.
The main objective of this project was to practice Object-Oriented Programming (OOP) concepts in Java by building a basic spreadsheet tool that handles formulas, errors, and data storage.

This project improved my understanding of data management and real-world application development.

![image](https://github.com/user-attachments/assets/d2e93fe5-9e92-4d28-9025-6b67894d19f0)
