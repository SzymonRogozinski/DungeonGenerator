package com.company;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    private int width,height;
    private Place [][] terrain;

    private BufferedImage image;

    private final static int margin=3;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        //terrain=new boolean[height][width];
        //border=new boolean[height][width];
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

    public static class AlreadyTrueException extends Exception{}
}
