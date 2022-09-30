package model;

public class Cell {
    public boolean hasMine;           // Cell contain a mine?
    public boolean exposed;            // Has the cell been exposed (may or may not have mine)?
    public boolean marked;             // Cell marked has a mine (perhaps incorrectly)?
    public int numbSurroundingmines;   // Number of mines in 8 adjacent cells;
}
