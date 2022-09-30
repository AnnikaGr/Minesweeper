package model;

public class Game {
    private Board board;
    private boolean planetBig;
    private boolean protectedBig;



    public boolean gameLost(){
        return board.lost;
    }


}
