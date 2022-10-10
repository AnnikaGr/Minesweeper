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

    public void increaseWaterAvailable(int maxWells) {
        this.waterAvailable++;
        if(this.waterAvailable>maxWells){
            this.waterAvailable=maxWells;
        }
    }

    public void decreaseWaterAvailable(){
        this.waterAvailable--;
        if (this.waterAvailable<0){
            throw new IllegalStateException("Number of available water is in minus");
        }
    }

    public int getResearchAvailable() {
        return researchAvailable;
    }

    public void decreaseResearchAvailable() {
        this.researchAvailable--;
        if(this.researchAvailable<0){
            this.researchAvailable=0;
        }
    }
}
