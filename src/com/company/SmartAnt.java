package com.company;

public class SmartAnt extends Ant{

    private int counter;
    private directions dir;

    public SmartAnt(int x, int y) {
        super(x, y);
        counter=0;
        dir=directions.NONE;
    }

    public void getDirection(){
        counter--;
        if(counter<=0){

        }
    }

    private enum directions{
        UP,LEFT,RIGHT,DOWN,NONE;
    }
}
