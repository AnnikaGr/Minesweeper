package model;

import java.util.Timer;

import static java.lang.Math.random;

public class Board {
    private int numbMines; // total number of mines
    public int numExposedCells; // total cell exposed
    private int totalCells;

    public int numBombsHit=0;
    public boolean lost;

    public int height;
    public int width;
    public static Timer timer;
    public static boolean isTimerSet;
    public boolean isMineFieldSet;
    public Cell[][] grid;

    // with inspiration from https://github.com/dmaida/MineSweeper
    public Board (int numRow, int numCol, int numMines, int numWells) {

        int w = numCol;
        int h = numRow;

        height = h;
        width = w;

        grid = new Cell[h][w];

        int totalCells = w * h; // total cells left
        this.totalCells = totalCells;


        int row, col;

        numExposedCells = 0;

        //place mines
        for (row = 0; row < h; row++) {
            for (col = 0; col < w; col++) {
                Cell cell = new Cell();
                cell.exposed = cell.marked = cell.hasMine = false;
                //place mines
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
        for (row = 0; row < h; row++) {
            for (col = 0; col < w; col++) {
                double p = (double) numWells / (double) totalCells; // probability
                double g = random();
                if (g < p) {
                    if(!grid[row][col].hasMine){
                        grid[row][col].hasWell = true;
                        numWells--;
                    }
                }
                totalCells--;
            }
        }

        // calculate mine counts
        for (row = 0; row < h; row++) {
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
                cell.numSurroundingMines = count;
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
        if(column>= width || row >= height || column<0||row<0){
            return -6; //reached field edge
        }

        Cell cell = grid[row][column];

        if(unexposedCount()==1){//game won
            return -4;
        }

        if (cell.hasMine) {
            numBombsHit= numBombsHit+1;
            cell.mineExposed=true;
            if(numBombsHit>=3){
                lost=true;
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

    public boolean isExposed(int column, int row) {
        if(column>= width || row >= height || column<0||row<0){
            return false;
        }

        Cell cell = grid[row][column];
        if(cell.exposed || cell.mineExposed){
            return true;
        }
        else{
             return false;
        }
        /*if (!cell.exposed && !cell.hasMine) {
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
                    if (neighbor.exposed && neighbor.numSurroundingMines == 0) {
                        cell.exposed = true;
                        numExposedCells++;
                        return true;
                    }
                }
            }
        }
        return false;*/
    }

    public int unexposedCount() {
        int unexposedCounter = (totalCells - numExposedCells - numbMines);
        return unexposedCounter;
    }

    public int exposedCount(){
        return numExposedCells;
    }
}
