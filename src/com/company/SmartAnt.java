package com.company;

import java.util.ArrayList;
import java.util.Random;

public class SmartAnt extends Ant{

    private int counter;
    private Direction dir;
    private static final int[][] directions= new int[][]{{-1,0}, {0,-1},{1,0},{0,1}};
    private static final int randWide=10;
    private static final int randConst=2;

    public SmartAnt(int x, int y) {
        super(x, y);
        this.counter=0;
    }

    public SmartAnt(int x, int y,int counter,Direction dir) {
        super(x, y);
        this.counter=counter;
        this.dir=dir;
    }

    public SmartAnt getNextAnt(Map map, Random random){
        if (counter == 0) {
            dir = getNextDirection(map, random);
            if (dir == null) {
                return null;
            }
            counter = random.nextInt(randWide) + randConst;
        }
        return dir.nextAnt(this,counter-1);
    }

    private Direction getNextDirection(Map map,Random random){
        ArrayList<Direction> dirList=new ArrayList<>();
        for (int[] d:directions) {
            Direction dd=new Direction(d[0],d[1]);
            if(!dd.checkNextAnt(this,map)){
                dirList.add(dd);
            }
        }
        if(dirList.size()==0){
            return null;
        }else{
            return dirList.get(random.nextInt(dirList.size()));
        }
    }

    public SmartAnt getNextRandomAnt(Map map,Random random){
        ArrayList<Direction> dirList=new ArrayList<>();
        for (int[] d:directions) {
            Direction dd=new Direction(d[0],d[1]);
            if(!dd.checkNextAnt(this,map)){
                dirList.add(dd);
            }
        }
        if (dirList.isEmpty()){
            return null;
        }
        return dirList.get(random.nextInt(dirList.size())).nextAnt(this,random.nextInt(randWide) + randConst);
    }

    private class Direction{
        private int dx;
        private int dy;

        public Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public SmartAnt nextAnt(SmartAnt ant, int counter){
            return new SmartAnt(ant.getX()+dx, ant.getY()+dy,counter,this);
        }

        public boolean checkNextAnt(SmartAnt ant, Map map){
            try{
                return map.getTerrain(ant.getX()+dx,ant.getY()+dy)==Place.FLOOR;
            }catch(IllegalArgumentException e){
                return true;
            }
        }
    }
}
