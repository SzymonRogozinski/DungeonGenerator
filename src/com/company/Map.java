package com.company;

import java.awt.image.BufferedImage;

public class Map {
    private int width,height;
    private boolean[][] terrain;

    private BufferedImage image;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        terrain=new boolean[height][width];
        image=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public BufferedImage getImage(){return image;}

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

    public void resize(int maxX,int minX,int maxY,int minY){
        boolean[][] newTerrain=terrain.clone();
        System.arraycopy(terrain,minY,newTerrain,0,maxY-minY+1);
        int xLen=maxX-minX+1;
        newTerrain=new boolean[newTerrain.length][xLen];
        for (int i=0;i< newTerrain.length;i++) {
            System.arraycopy(terrain,minX,newTerrain,0,xLen);
        }
        terrain=newTerrain;
        width=xLen;
        height=maxY-minY+1;
        image=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
    }

    public class AlreadyTrueException extends Exception{}
}
