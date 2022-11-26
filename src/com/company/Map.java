package com.company;

public class Map {
    private final int width,height;
    private boolean[][] terrain;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        terrain=new boolean[height][width];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean getTerrain(int x,int y){
        if(x<0 || y<0 || x>=width || y>=height)
            throw new IllegalArgumentException();
        return terrain[y][x];
    }

    public void setTerrain(int x,int y) throws AlreadyTrueException {
        if(x<0 || y<0 || x>=width || y>=height)
            throw new IllegalArgumentException();
        if(terrain[y][x])
            throw new AlreadyTrueException();
        terrain[y][x]=true;
    }

    public class AlreadyTrueException extends Exception{}
}
