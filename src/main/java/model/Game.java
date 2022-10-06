package model;

public class Game {
    private Board board;
    private boolean planetBig;
    private boolean protectedBig;
    private int waterAvailable = 0;
    private int researchAvailable = 3;


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

    public int getWaterAvailable() {
        return waterAvailable;
    }

    public void setWaterAvailable() {
        this.waterAvailable++;
    }

    public int getResearchAvailable() {
        return researchAvailable;
    }

    public void increaseResearchAvailable() {
        this.researchAvailable++;
    }
}
