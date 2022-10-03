package model;

public class Game {
    private Board board;
    private boolean planetBig;
    private boolean protectedBig;

    public boolean gameLost(){
        return board.lost;
    }


    public Game (boolean planetBig, boolean protectedBig){
        this.planetBig=planetBig;
        this.protectedBig=protectedBig;
    }

    public boolean isPlanetBig() {
        return planetBig;
    }

    public boolean isProtectedBig() {
        return protectedBig;
    }

}
