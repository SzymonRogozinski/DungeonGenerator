package com.company;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Map {
    private int width,height;
    private boolean[][] terrain;
    private boolean [][] border;

    private BufferedImage image;

    private final static int margin=3;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        terrain=new boolean[height][width];
        border=new boolean[height][width];
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
    public boolean getBorder(int x,int y){
        if(x<0 || y<0 || x>=width || y>=height)
            throw new IllegalArgumentException();
        return border[y][x];
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
        //Centering
        int xMargin=margin;
        int yMargin=margin;
        if(xLen>yLen){
            yMargin=(xLen-yLen)/2+margin;
        } else if (xLen<yLen) {
            xMargin=(yLen-xLen)/2+margin;
        }
        //Cutting height
        boolean[][] newTerrain=new boolean[yLen+2*yMargin][width];
        System.arraycopy(terrain,minY,newTerrain,yMargin,yLen);
        //Saving changes
        terrain=newTerrain;
        //Cutting width
        newTerrain=new boolean[yLen+2*yMargin][xLen+2*xMargin];
        for (int i=0;i< newTerrain.length;i++) {
            System.arraycopy(terrain[i],minX,newTerrain[i],xMargin,xLen);
        }
        //Saving changes
        terrain=newTerrain;
        width=xLen+2*xMargin;
        height=yLen+2*yMargin;
        border=new boolean[height][width];
        image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    }

    public void drawBorder(){
        border=new boolean[height][width];
        for(int y=1;y<height-1;y++){
            for(int x=1;x<width-1;x++){
                if((terrain[y][x-1] ||terrain[y][x+1] ||terrain[y-1][x] ||terrain[y+1][x]) && !terrain[y][x] ){
                    border[y][x]=true;
                }
            }
        }

    }

    public static class AlreadyTrueException extends Exception{}
}
