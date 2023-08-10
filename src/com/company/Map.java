package com.company;

import java.awt.image.BufferedImage;

public class Map {
    private final int width,height;
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

    public class AlreadyTrueException extends Exception{}
}
