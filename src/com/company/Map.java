package com.company;

import java.awt.image.BufferedImage;

public class Map {
    private int width,height;
    private boolean[][] terrain;

    private BufferedImage image;

    private static int margin=3;

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
        //New width and height
        int xLen=maxX-minX;
        int yLen=maxY-minY;
        //Cutting height
        boolean[][] newTerrain=new boolean[yLen+2*margin][width];
        System.arraycopy(terrain,minY,newTerrain,margin,yLen);
        //Saving changes
        terrain=newTerrain;
        //Cutting width
        newTerrain=new boolean[yLen+2*margin][xLen+2*margin];
        for (int i=0;i< newTerrain.length;i++) {
            System.arraycopy(terrain[i],minX,newTerrain[i],margin,xLen);
        }
        //Saving changes
        terrain=newTerrain;
        width=xLen+2*margin;
        height=yLen+2*margin;
        image=new BufferedImage(width,height,BufferedImage.TYPE_BYTE_BINARY);
    }

    public class AlreadyTrueException extends Exception{}
}
