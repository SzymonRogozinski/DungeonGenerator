package com.company.dungeon;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MapTransformation {

    private final static int margin=3;
    private final static int trials=3;
    private final static int doorRange=7;
    private final static  Random random=new Random();


    public static void drawBorder(Map map){
        for(int y=1;y<map.getHeight()-1;y++){
            for(int x=1;x<map.getWidth()-1;x++){
                if((map.getTerrain(x-1,y)==Place.FLOOR ||map.getTerrain(x+1,y)==Place.FLOOR ||map.getTerrain(x,y-1)==Place.FLOOR ||map.getTerrain(x,y+1)==Place.FLOOR) && map.getTerrain(x,y)!=Place.FLOOR ){
                    try {
                        map.setTerrain(x, y,Place.WALL);
                    }catch(Exception ignore){}
                }
            }
        }
    }

    //Always drew two door, entry and exit
    public static void drawEntries(Map map){
        ArrayList<Coordinate> possibleLocation=new ArrayList<>();
        for(int y=margin-1;y<map.getHeight()-margin;y++){
            for(int x=margin-1;x<map.getWidth()-margin;x++){
                if(canBePossibleLocationNextToWall(x,y,map) && checkIfBlockingPassage(x,y,map))
                    possibleLocation.add(new Coordinate(x,y));
            }
        }
        int range=(int)(0.7 * map.getWidth());
        int count=trials;
        while(true){
            Coordinate entry=possibleLocation.get(random.nextInt(possibleLocation.size()));
            Coordinate exit=null;
            while(count>0){
                exit=possibleLocation.get(random.nextInt(possibleLocation.size()));
                int distance=(int)Math.sqrt((entry.x- exit.x)*(entry.x- exit.x)+(entry.y- exit.y)*(entry.y- exit.y));
                if(distance>=range)
                    break;
                count--;
            }
            if(count>0){
                try {
                    map.setTerrain(entry.x, entry.y, Place.ENTRIES);
                    map.setTerrain(exit.x, exit.y, Place.ENTRIES);
                }catch(Exception ignore){}
                break;
            }
            count=trials;
        }
    }

    public static void drawTreasure(int TreasureCount, Map map) throws Exception {
        //Create list of points that can be for treasure location
        //It must be done, after resize
        ArrayList<Coordinate> possibleLocation=new ArrayList<>();
        for(int y=margin-1;y<map.getHeight()-margin;y++){
            for(int x=margin-1;x<map.getWidth()-margin;x++){
                if(canBePossibleLocationNextToWall(x,y,map))
                    possibleLocation.add(new Coordinate(x,y));
            }
        }
        if(possibleLocation.size()<TreasureCount)
            throw new Exception("Cannot generate that many treasure!");
        while(TreasureCount>0 && possibleLocation.size()>0){
            int i=random.nextInt(possibleLocation.size());
            Coordinate coordinate=possibleLocation.get(i);
            if(checkIfBlockingPassage(coordinate.x,coordinate.y,map)) {
                map.setTerrain(coordinate.x,coordinate.y,Place.TREASURE);
                TreasureCount--;
            }
            possibleLocation.remove(i);
        }
    }

    public static void drawEnemies(int EnemiesCount,Map map) throws Exception {
        //Create list of points that can be for treasure location
        //It must be done, after resize
        ArrayList<Coordinate> possibleLocation=new ArrayList<>();
        ArrayList<Coordinate> entries=new ArrayList<>();
        for(int y=margin-1;y<map.getHeight()-margin;y++){
            for(int x=margin-1;x<map.getWidth()-margin;x++){
                if(map.getTerrain(x,y)==Place.FLOOR)
                    possibleLocation.add(new Coordinate(x,y));
                else if(map.getTerrain(x,y)==Place.ENTRIES)
                    entries.add(new Coordinate(x,y));
            }
        }
        if(possibleLocation.size()<EnemiesCount)
            throw new Exception("Cannot generate that many enemies!");
        while(EnemiesCount>0 && possibleLocation.size()>0){
            int i=random.nextInt(possibleLocation.size());
            Coordinate coordinate=possibleLocation.get(i);
            if(canBeEnemyLocation(coordinate.x,coordinate.y,entries)) {
                map.setTerrain(coordinate.x,coordinate.y,Place.ENEMY);
                EnemiesCount--;
            }
            possibleLocation.remove(i);
        }
    }

    //Check if distance to door is longer than see range
    private static boolean canBeEnemyLocation(int x,int y, ArrayList<Coordinate> entries){
        for(Coordinate entry: entries){
            int distance=(int)Math.sqrt((x-entry.x)*(x-entry.x)+(y-entry.y)*(y-entry.y));
            if(distance<doorRange){
                return false;
            }
        }
        return true;
    }

    private static boolean canBePossibleLocationNextToWall(int x, int y, Map map){
        //Is space free
        if(map.getTerrain(x,y)!=Place.FLOOR)
            return false;
        if(checkNeighbourhood(x,y,map))
            return false;
        int count=0;    //Count Walls
        if(map.getTerrain(x,y-1)==Place.WALL)
            count++;
        if(map.getTerrain(x-1,y)==Place.WALL)
            count++;
        if(map.getTerrain(x,y+1)==Place.WALL)
            count++;
        if(map.getTerrain(x+1,y)==Place.WALL)
            count++;
        return count>0 && count<4;
    }

    private static boolean checkIfBlockingPassage(int x, int y, Map map){
        if(checkNeighbourhood(x,y,map)){
            return false;
        }
        //Maps neighbours
        boolean[][] freeSpace={
                {map.getTerrain(x-1,y-1)==Place.FLOOR,map.getTerrain(x,y-1)==Place.FLOOR,map.getTerrain(x+1,y-1)==Place.FLOOR},
                {map.getTerrain(x-1,y)==Place.FLOOR,false,map.getTerrain(x+1,y)==Place.FLOOR},
                {map.getTerrain(x-1,y+1)==Place.FLOOR,map.getTerrain(x,y+1)==Place.FLOOR,map.getTerrain(x+1,y+1)==Place.FLOOR}
        };
        //Count free space and select starting coordinates
        int freeSpaceCount=0;
        Coordinate c=null;
        //y->i x->j
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(freeSpace[i][j]){
                    freeSpaceCount++;
                    c=new Coordinate(j,i);
                }
            }
        }

        if(c==null)
            return false;

        Stack<Coordinate> coords=new Stack<>();
        coords.add(c);
        Stack<Integer> rotation=new Stack<>();
        rotation.add(0);
        int[][] rotationTable={{-1,0},{0,1},{1,0},{0,-1}};
        //Places that are checked if there are connected
        boolean[][] checkedSpace=new boolean[3][3];
        checkedSpace[c.y][c.x]=true;
        int counter=1;
        //Deep search for checking connection
        while(!coords.empty()){
            c=coords.pop();
            int rot=rotation.pop();
            if(rot!=3) {
                rotation.add(rot+1);
                coords.add(c);
            }
            int newX,newY;
            newY=c.y+rotationTable[rot][0];
            newX=c.x+rotationTable[rot][1];
            if(newX>=0 && newX<3 && newY>=0 && newY<3){
                if(freeSpace[newY][newX] && !checkedSpace[newY][newX]){
                    checkedSpace[newY][newX]=true;
                    counter++;
                    coords.add(new Coordinate(newX,newY));
                    rotation.add(0);
                }
            }
        }
        //Check if all spaces are achievable
        return freeSpaceCount==counter;
    }

    private static boolean checkNeighbourhood(int x,int y,Map map){
        //Check if treasure in neighbourhood
        int[][] positionToCheck={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        for(int[] mod:positionToCheck)
            if(map.getTerrain(x+mod[1],y+mod[0])==Place.TREASURE)
                return true;
        return false;
    }
}
