package model;

import java.util.Timer;

import static java.lang.Math.random;

public class Board {
    private int numbMines; // total number of mines
    private int numExposedCells; // total cell exposed
    private int totalCells;
    public boolean lost;

    public int height;
    public int width;
    public static Timer timer;
    public static boolean isTimerSet;
    public boolean isMineFieldSet;
    public Cell[][] grid;

    public void createMineField(int level) {

        int w = 0;
        int h = 0;

        if (level == 1) {
            h = 9;
            w = 9;
            numbMines = 10;
        }
        if (level == 2) {
            h = 16;
            w = 16;
            numbMines = 40;
        }
        if (level == 3) {
            h = 16;
            w = 30;
            numbMines = 99;
        }

        height = h;
        width = w;

        grid = new Cell[h][w];

        int n = w * h; // total cells left
        totalCells = n;

        int m = numbMines; // Number of mines to set

        int row, col;

        numExposedCells = 0;

        for (row = 0; row < h; row++) { // place mines
            for (col = 0; col < w; col++) {
                Cell cell = new Cell();
                cell.exposed = cell.marked = cell.hasMine = false;
                double p = (double) m / (double) n; // probability of placing mine
                double g = random();
                if (g < p) {
                    cell.hasMine = true;
                    m--;
                }
                n--;
                grid[row][col] = cell;
            }
        }

        for (row = 0; row < h; row++) {// calculate surrounding mine counts
            for (col = 0; col < w; col++) {
                int i, j, count = 0;
                Cell cell = grid[row][col];
                for (j = -1; j <= +1; j++) {
                    for (i = -1; i <= +1; i++) {
                        if (i == 0 && j == 0)
                            continue;
                        int rr = row + j, cc = col + i;
                        if (rr < 0 || rr >= h || cc < 0 || cc >= w)
                            continue;
                        Cell neighbor = grid[rr][cc];
                        if (neighbor.hasMine) {
                            count++;
                        }
                    }
                }
                cell.numbSurroundingmines = count;
            }
        }
    }

    public boolean mark(int column, int row) { // mark a Cell

        Cell cell = grid[row][column];

        if (!cell.marked) { // if cell has NOT been marked, mark it
            cell.marked = true;
            return true;
        } else {// if cell is already marked, unmark it
            cell.marked = false;
            return false;
        }
    }

    public int expose(int column, int row) {
        Cell cell = grid[row][column];

        if (cell.hasMine) { // game over, exposed mine
            lost = true;
            return -1;
        }
        if (cell.exposed)
            return -2;

        cell.exposed = true;
        numExposedCells++;

        int n = cell.numbSurroundingmines;

        if (n == 0) {
            int w = width, h = height;
            boolean changed = true;
            while (changed) {
                int rr, cc;
                changed = false;
                for (rr = 0; rr < h; rr++) {
                    for (cc = 0; cc < w; cc++) {
                        if (isExposed(cc, rr)) {
                            changed = true;
                        }
                    }
                }
            }
            ;
        }
        return n;
    }

    public boolean isExposed(int column, int row) {
        Cell cell = grid[row][column];
        if (!cell.exposed && !cell.hasMine) {
            int w = width, h = height;
            int i, j;
            for (j = -1; j <= +1; j++) {
                for (i = -1; i <= +1; i++) {
                    if (i == 0 && j == 0)
                        continue;
                    int rr = row + j, cc = column + i;
                    if (rr < 0 || rr >= h || cc < 0 || cc >= w)
                        continue;
                    Cell neighbor = grid[rr][cc];
                    if (neighbor.exposed && neighbor.numbSurroundingmines == 0) {
                        cell.exposed = true;
                        numExposedCells++;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int unexposedCount() {
        int unexposedCounter = (totalCells - numExposedCells - numbMines);
        return unexposedCounter;
    }
}
