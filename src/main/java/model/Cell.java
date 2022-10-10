package model;

public class Cell {
    public boolean hasMine;
    public boolean mineExposed;
    public boolean hasWater;
    public boolean hasWell;
    public boolean exposed;            // Has the cell been exposed (may or may not have mine)?
    public boolean marked;             // Cell marked has a mine (perhaps incorrectly)?
    public int numSurroundingMines;   // Number of mines in 8 adjacent cells;
}
