package model;

import java.util.Timer;

import static java.lang.Math.random;

public class Board {
    private int numMines; // total number of mines
    public int numExposedCells; // total cell exposed
    private int totalCells;

    public int numBombsHit = 0;
    public boolean lost;

    public int height;
    public int width;
    public static Timer timer;
    public static boolean isTimerSet;
    public Cell[][] grid;

    // with input from https://github.com/dmaida/MineSweeper
    public Board(int numRow, int numCol, int numMines, int numWells) {

        height = numRow;
        width = numCol;
        this.numMines = numMines;
        numExposedCells = 0;

        grid = new Cell[numRow][numCol];
        int totalCells = numCol * numRow;
        this.totalCells = totalCells;

        //place mines
        int row, col;
        for (row = 0; row < numRow; row++) {
            for (col = 0; col < numCol; col++) {
                Cell cell = new Cell();
                cell.exposed = cell.marked = cell.hasMine = false;
                double p = (double) numMines / (double) totalCells; // probability
                double g = random();
                if (g < p) {
                    cell.hasMine = true;
                    numMines--;
                }
                totalCells--;
                grid[row][col] = cell;
            }
        }

        //place wells
        totalCells = this.totalCells;
        for (row = 0; row < numRow; row++) {
            for (col = 0; col < numCol; col++) {
                double p = (double) numWells / (double) totalCells; // probability
                double g = random();
                if (g < p) {
                    if (!grid[row][col].hasMine) {
                        grid[row][col].hasWell = true;
                        numWells--;
                    }
                }
                totalCells--;
            }
        }

        // calculate mine counts
        for (row = 0; row < numRow; row++) {
            for (col = 0; col < numCol; col++) {
                int i, j, count = 0;
                Cell cell = grid[row][col];
                for (j = -1; j <= +1; j++) {
                    for (i = -1; i <= +1; i++) {
                        if (i == 0 && j == 0)
                            continue;
                        int rr = row + j, cc = col + i;
                        if (rr < 0 || rr >= numRow || cc < 0 || cc >= numCol)
                            continue;
                        Cell neighbor = grid[rr][cc];
                        if (neighbor.hasMine) {
                            count++;
                        }
                    }
                }
                cell.numSurroundingMines = count;
            }
        }
    }

    // mark a cell
    public boolean mark(int column, int row) {
        Cell cell = grid[row][column];

        if (!cell.marked) {
            cell.marked = true;
            return true;
        } else {
            cell.marked = false;
            return false;
        }
    }

    // expose a cell
    public int expose(int column, int row) {
        if (unexposedCount() == 1) {//game won
            return -4;
        }

        if (column >= width || row >= height || column < 0 || row < 0) {
            return -6; //reached field edge
        }

        Cell cell = grid[row][column];

        if (cell.hasMine) {
            numBombsHit = numBombsHit + 1;
            cell.mineExposed = true;
            if (numBombsHit >= 3) {
                lost = true;
                return -3; //game lost
            }
            return -1; //bomb hit
        }

        if (cell.exposed)
            return -2; //cell already exposed

        cell.exposed = true;
        numExposedCells++;

        return cell.numSurroundingMines;
    }

    // check if cell is exposed
    public boolean isExposed(int column, int row) {
        if (column >= width || row >= height || column < 0 || row < 0) {
            return false;
        }

        Cell cell = grid[row][column];
        if (cell.exposed || cell.mineExposed) {
            return true;
        } else {
            return false;
        }
    }

    // get number of unexposed cells
    public int unexposedCount() {
        return (totalCells - numExposedCells - numMines);
    }

}
