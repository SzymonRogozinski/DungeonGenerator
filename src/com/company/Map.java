package com.company;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class Map {
    private int width,height;
    private Place [][] terrain;

    private BufferedImage image;

    private final static int margin=3;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        terrain=new Place[height][width];
        for (Place[] places: terrain) {
            Arrays.fill(places,Place.VOID);
        }
        image=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public BufferedImage getImage(){return image;}

    public Place getTerrain(int x,int y){
        if(x<0 || y<0 || x>=width || y>=height)
            throw new IllegalArgumentException();
        return terrain[y][x];
    }

    public void setTerrain(int x,int y) throws AlreadyTrueException {
        if(x<0 || y<0 || x>=width || y>=height)
            throw new IllegalArgumentException();
        if(terrain[y][x]==Place.FLOOR)
            throw new AlreadyTrueException();
        terrain[y][x]=Place.FLOOR;
    }

    public void resize(int maxX,int minX,int maxY,int minY){
        //New width and height
        int xLen=maxX-minX+1;
        int yLen=maxY-minY+1;
        //Centering
        int xMargin=margin;
        int yMargin=margin;
        if(xLen>yLen){
            yMargin=(xLen-yLen)/2+margin;
        } else if (xLen<yLen) {
            xMargin=(yLen-xLen)/2+margin;
        }
        //Cutting height
        Place[][] newTerrain=new Place[yLen+2*yMargin][width];
        System.arraycopy(terrain,minY,newTerrain,yMargin,yLen);
        //Saving changes
        terrain=newTerrain;
        //Cutting width
        newTerrain=new Place[yLen+2*yMargin][xLen+2*xMargin];
        for (int i=0;i< newTerrain.length;i++) {
            System.arraycopy(terrain[i],minX,newTerrain[i],xMargin,xLen);
        }
        //Saving changes
        terrain=newTerrain;
        width=xLen+2*xMargin;
        height=yLen+2*yMargin;
        image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    }

    public void drawBorder(){
        for(int y=1;y<height-1;y++){
            for(int x=1;x<width-1;x++){
                if((terrain[y][x-1]==Place.FLOOR ||terrain[y][x+1]==Place.FLOOR ||terrain[y-1][x]==Place.FLOOR ||terrain[y+1][x]==Place.FLOOR) && terrain[y][x]!=Place.FLOOR ){
                    terrain[y][x]=Place.WALL;
                }
            }
        }
    }

    public void drawTreasure(int TreasureCount) throws Exception {
        //Create list of points that can be for treasure location
        //It must be done, after resize
        ArrayList<Coordinate> possibleLocation=new ArrayList<>();
        for(int y=margin-1;y<height-margin;y++){
            for(int x=margin-1;x<width-margin;x++){
                if(canBePossibleTreasure(x,y))
                    possibleLocation.add(new Coordinate(x,y));
            }
        }
        if(possibleLocation.size()<TreasureCount)
            throw new Exception("Cannot generate that many treasure!");
        Random random=new Random();
        while(TreasureCount>0 && possibleLocation.size()>0){
            int i=random.nextInt(possibleLocation.size());
            Coordinate coordinate=possibleLocation.get(i);
            if(canBeTreasure(coordinate.x,coordinate.y)) {
                terrain[coordinate.y][coordinate.x] = Place.TREASURE;
                TreasureCount--;
            }
            possibleLocation.remove(i);
        }
    }

    private boolean canBePossibleTreasure(int x,int y){
        if(terrain[y][x]!=Place.FLOOR)
            return false;
        int count=0;    //Count Walls
        //Is space free
        if(terrain[y][x]!=Place.FLOOR)
            return false;
        if(terrain[y-1][x]==Place.WALL)
            count++;
        if(terrain[y][x-1]==Place.WALL)
            count++;
        if(terrain[y+1][x]==Place.WALL)
            count++;
        if(terrain[y][x+1]==Place.WALL)
            count++;
        return count>0 && count<4;
    }

    private boolean canBeTreasure(int x,int y){
        //Maps neighbours
        boolean[][] freeSpace={
                {terrain[y-1][x-1]==Place.FLOOR,terrain[y-1][x]==Place.FLOOR,terrain[y-1][x+1]==Place.FLOOR},
                {terrain[y][x-1]==Place.FLOOR,false,terrain[y][x+1]==Place.FLOOR},
                {terrain[y+1][x-1]==Place.FLOOR,terrain[y+1][x]==Place.FLOOR,terrain[y+1][x+1]==Place.FLOOR}
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


    private class Coordinate{
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class AlreadyTrueException extends Exception{}
}
