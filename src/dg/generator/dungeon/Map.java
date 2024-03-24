package dg.generator.dungeon;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Map {
    private int width,height;
    private Place[][] terrain;
    private Place[][] terrainCopy;
    //[0]->Enter, [1]->Exit
    private Coordinate[] entries;
    private BufferedImage image;

    private final static int margin=3;

    public Map(int width, int height) {
        this.entries=new Coordinate[2];
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

    public void setTerrain(int x,int y,Place place) {
        if(x<0 || y<0 || x>=width || y>=height)
            throw new IllegalArgumentException();
        terrain[y][x]=place;
    }

    public void makeCopy(){
        terrainCopy=new Place[height][];
        for(int i=0;i<height;i++){
            terrainCopy[i]=Arrays.copyOf(terrain[i],width);
        }
    }

    public void backToCopy(){
        if(terrainCopy!=null) {
            terrain = terrainCopy;
            terrainCopy = null;
        }
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

    public void setEntries(Coordinate enter,Coordinate exit){
        entries[0]=enter;
        entries[1]=exit;
    }

    public Coordinate[] getEntries(){
        return entries;
    }

    public static class AlreadyTrueException extends Exception{}
}
