package com.company.dungeon.algorithm;

import com.company.dungeon.Map;
import com.company.dungeon.algorithm.GeneratingAlgorithm;
import delaunay.BowyerWatson;
import mst.Kruskal;
import structure.DEdge;
import structure.DPoint;

import java.awt.*;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class WebOfRoomsAlgorithm implements GeneratingAlgorithm {
    //State
    private boolean roomGenerated;
    private ArrayList<Rectangle> roomList;
    private ArrayList<Rectangle> roomWithMarginList;
    private int limit;
    private Random random;
    private Map reference;
    private int maxX,minX,maxY,minY;

    private static final int maxSize=14;
    private static final int minSize=7;
    private static final int roomMargin =1;

    @Override
    public void setStart(int limit, Random random, Map reference, double dense) throws UnexpectedException {
        roomList=new ArrayList<>();
        roomWithMarginList=new ArrayList<>();

        this.random=random;
        this.reference=reference;

        this.limit=limit;

        //Calc margin space
        int marginSpace=reference.getHeight()* reference.getWidth()-(reference.getHeight()-(2*maxSize))* (reference.getWidth()-(2*maxSize));
        int drawingSpace=reference.getHeight()* reference.getWidth()-marginSpace/2;

        if(limit*1.8>drawingSpace){
            throw new UnexpectedException("Margin space is too large!");
        }
    }

    @Override
    public int getLimit() {
        return roomGenerated?0:1;   //Ignore limit
    }

    @Override
    public boolean generate() {
        //Draw corridors
        if(limit<0){
            //Seeking for corridors
            ArrayList<DPoint> centres=new ArrayList<>();
            for (Rectangle room:roomList) {
                DPoint p=new DPoint((int) room.getCenterX(), (int) room.getCenterY());
                centres.add(p);
            }
            ArrayList<DEdge> corridors=DelaunayTriangulation(centres, reference.getWidth(), reference.getHeight());
            //Drawing each corridor
            for(DEdge edge:corridors){
                drawCorridor(edge);
            }
            roomGenerated=true;
            return true;
        }
        //Generate rooms
        while(true){
            Rectangle roomWithMargin=new Rectangle(random.nextInt(0, reference.getWidth()-maxSize), random.nextInt(0, reference.getHeight()-maxSize), random.nextInt(minSize,maxSize),random.nextInt(minSize,maxSize));
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
            Rectangle room=new Rectangle(roomWithMargin.x+ roomMargin,roomWithMargin.y+ roomMargin,roomWithMargin.width-2* roomMargin,roomWithMargin.height-2* roomMargin);
            checkSizes(room);
            drawRoom(room);

            roomWithMarginList.add(roomWithMargin);
            roomList.add(room);
            break;
        }
        return false;
    }

    private ArrayList<DEdge> DelaunayTriangulation(ArrayList<DPoint> rectangleCentres, int maxX, int maxY){
        BowyerWatson bw=new BowyerWatson(maxX,maxY,rectangleCentres);
        HashSet<DEdge> allOfEdges=bw.getPrunEdges();
        Kruskal kruskal=new Kruskal(rectangleCentres,allOfEdges);
        HashSet<DEdge> edges=kruskal.getMST();

        int additionalEdges= (int)((allOfEdges.size()-edges.size())*0.25);
        ArrayList<DEdge> edgeList= new ArrayList<>(allOfEdges);
        Random random=new Random();
        while(additionalEdges>0){
            int r=random.nextInt(allOfEdges.size());
            DEdge edge=edgeList.get(r);
            if(!edges.contains(edge)){
                edges.add(edge);
                additionalEdges--;
            }
        }
        return new ArrayList<>(edges);
    }

    private void drawCorridor(DEdge edge) {
        //Bresenham's line algorithm
        int startX=(int)edge.p[0].x;
        int startY=(int)edge.p[0].y;
        int endX=(int)edge.p[1].x;
        int endY=(int)edge.p[1].y;

        int dx=endX-startX;
        int dy=endY-startY;

        if(Math.abs(dy)<Math.abs(dx)){
            if (startX>endX){
                plotLineLow(endX,endY,startX,startY);
            }else{
                plotLineLow(startX,startY,endX,endY);
            }
        }else{
            if (startY>endY){
                plotLineHigh(endX,endY,startX,startY);
            }else{
                plotLineHigh(startX,startY,endX,endY);
            }
        }
    }

    //y loop
    private void plotLineHigh(int startX,int startY,int endX,int endY){
        int dx=endX-startX;
        int dy=endY-startY;

        int xDir=1;
        if(dx<0){
            xDir=-1;
            dx=-dx;
        }
        int D=2*dx-dy;
        int x=startX;
        int yDir=startY>endY?-1:1;

        for(int y=startY;y!=endY;y+=yDir){
            try {
                reference.setTerrain(x, y);
            }catch (Exception ignore){}
            if(D>0){
                x+=xDir;
                try {
                    reference.setTerrain(x, y);
                }catch (Exception ignore){}
                D+=2*(dx-dy);
            }else {
                D += 2 * dx;
            }
        }
    }

    //x loop
    private void plotLineLow(int startX,int startY,int endX,int endY){
        int dx=endX-startX;
        int dy=endY-startY;

        int yDir=1;
        if(dy<0){
            yDir=-1;
            dy=-dy;
        }
        int D=2*dy-dx;
        int y=startY;
        int xDir=startX>endX?-1:1;

        for(int x=startX;x!=endX;x+=xDir){
            try {
                reference.setTerrain(x, y);
            }catch (Exception ignore){}
            if(D>0){
                y+=yDir;
                try {
                    reference.setTerrain(x, y);
                }catch (Exception ignore){}
                D+=2*(dy-dx);
            }else {
                D += 2 * dy;
            }
        }
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