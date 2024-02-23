package com.company;

import com.company.triangulation.Point;
import com.company.triangulation.TriangulationAlgorithm;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class WebOfRoomsAlgorithm implements GeneratingAlgorithm{
    //State
    private boolean roomGenerated;
    private ArrayList<Rectangle> roomList;
    private ArrayList<Rectangle> roomWithMarginList;
    private int limit;
    private Random random;
    private Map reference;
    private double dense;
    private int maxX,minX,maxY,minY;

    private final int maxSize=18;
    private final int minSize=8;

    @Override
    public void setStart(int limit, Random random, Map reference, double dense) {
        roomList=new ArrayList<>();
        roomWithMarginList=new ArrayList<>();

        this.random=random;
        this.reference=reference;
        this.dense=(double)limit/(reference.getHeight()* reference.getWidth());

        this.limit=limit;
    }

    @Override
    public int getLimit() {
        return roomGenerated?0:1;   //Ignore limit
    }

    @Override
    public boolean generate() {
        if(limit<0){
            ArrayList<Point> centres=new ArrayList<>();
            for (Rectangle room:roomList) {
                Point p=new Point((int) room.getCenterX(), (int) room.getCenterY());
                centres.add(p);
            }
            TriangulationAlgorithm trian=new TriangulationAlgorithm();
            trian.DelaunayTriangulation(centres, reference.getWidth(), reference.getHeight());
            roomGenerated=true;
            return true;
        }
        while(true){
            Rectangle roomWithMargin=new Rectangle(random.nextInt(reference.getWidth()-maxSize),random.nextInt(reference.getHeight()-maxSize),random.nextInt(minSize,maxSize),random.nextInt(minSize,maxSize));
            boolean isCollision=false;
            for (Rectangle r: roomWithMarginList) {
                if(r.intersects(roomWithMargin)){
                    isCollision=true;
                    break;
                }
            }
            if(isCollision){
                continue;
            }
            limit-=roomWithMargin.height*roomWithMargin.width;
            Rectangle room=new Rectangle(roomWithMargin.x+1,roomWithMargin.y+1,roomWithMargin.width-2,roomWithMargin.height-2);
            checkSizes(room);
            drawRoom(room);

            roomWithMarginList.add(roomWithMargin);
            roomList.add(room);
            break;
        }
        return false;
    }

    private void drawRoom(Rectangle room){
        for(int x=room.x;x<(int)room.getMaxX();x++){
            for(int y= room.y;y<(int)room.getMaxY();y++){
                try {
                    reference.setTerrain(x, y);
                }catch (Map.AlreadyTrueException e){
                    throw new InternalError("Room will collide with another room!");
                }
            }
        }
    }

    @Override
    public int[] getSize() {
        return new int[]{maxX,minX,maxY,minY};
    }

    private void checkSizes(Rectangle rectangle){
        if((int)rectangle.getMaxX()-1>maxX){
            maxX=(int)rectangle.getMaxX()-1;
        } else if ((int)rectangle.getMinX()+1<minX) {
            minX=(int)rectangle.getMinX()+1;
        }
        if((int)rectangle.getMaxY()-1>maxY){
            maxY=(int)rectangle.getMaxY()-1;
        } else if ((int)rectangle.getMinY()+1<minY) {
            minY=(int)rectangle.getMinY()+1;
        }
    }
}
