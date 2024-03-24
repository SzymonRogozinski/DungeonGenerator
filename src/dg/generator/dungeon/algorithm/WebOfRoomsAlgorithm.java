package dg.generator.dungeon.algorithm;

import dg.generator.dungeon.Map;
import delaunay.BowyerWatson;
import mst.Kruskal;
import structure.DEdge;
import structure.DPoint;

import java.awt.*;
import java.rmi.UnexpectedException;
import java.util.*;

public class WebOfRoomsAlgorithm implements GeneratingAlgorithm {
    //State
    private boolean roomGenerated;
    private ArrayList<Rectangle> roomList;
    private ArrayList<Rectangle> roomWithMarginList;
    private HashMap<DPoint,Rectangle> roomCenters;
    private int limit;
    private Random random;
    private Map reference;
    private int maxX,minX,maxY,minY;

    private static final int maxSize=14;
    private static final int minSize=7;
    private static final int roomMargin =1;
    private static final double additionalCorridorsParam =0.3;

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

        if(limit*1.25>drawingSpace){
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
            roomCenters=new HashMap<>();
            for (Rectangle room:roomList) {
                DPoint p=new DPoint((int) room.getCenterX(), (int) room.getCenterY());
                roomCenters.put(p,room);
                centres.add(p);
            }
            //Making corridors web and MST
            HashSet<DEdge> allCorridorsHash=DelaunayTriangulation(centres, reference.getWidth(), reference.getHeight());
            ArrayList<DEdge> allCorridorsList=new ArrayList<>(allCorridorsHash);
            HashSet<DEdge> mstCorridors=getMinimumSpanningTree(centres,allCorridorsHash);
            //Additional corridors
            int additionalCorridorCounter= (int)((allCorridorsHash.size()-mstCorridors.size())* additionalCorridorsParam);
            HashSet<DEdge> additionalCorridors= new HashSet<>();
            while(additionalCorridorCounter>0){
                int r=random.nextInt(allCorridorsList.size());
                DEdge edge=allCorridorsList.get(r);
                //Not in MST and in additional corridors list
                if(!mstCorridors.contains(edge) && !additionalCorridors.contains(edge)){
                    additionalCorridors.add(edge);
                    additionalCorridorCounter--;
                }
            }
            //Drawing mst corridors
            for(DEdge edge:mstCorridors){
                drawCorridor(edge,true);
            }
            reference.makeCopy();
            //Drawing additional corridors
            for(DEdge edge:additionalCorridors){
                drawCorridor(edge,false);
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

    private HashSet<DEdge> DelaunayTriangulation(ArrayList<DPoint> rectangleCentres, int maxX, int maxY){
        BowyerWatson bw=new BowyerWatson(maxX,maxY,rectangleCentres);
        HashSet<DEdge> allOfEdges=bw.getPrunEdges();
        return allOfEdges;
    }

    private HashSet<DEdge> getMinimumSpanningTree(ArrayList<DPoint> rectangleCentres,HashSet<DEdge> edges){
        HashSet<DEdge> edgesCopy= (HashSet<DEdge>) edges.clone();
        Kruskal kruskal=new Kruskal(rectangleCentres,edgesCopy);
        HashSet<DEdge> edgesMST=kruskal.getMST();

        return edgesMST;
    }

    private void drawCorridor(DEdge edge,boolean collisionAccepted) {
        DPoint start=edge.p[0];
        DPoint end=edge.p[1];
        //Move point to wall
        double DX=end.x-start.x;
        double DY=end.y-start.y;
        if(Math.abs(DX)>=Math.abs(DY)){
            if(DX>=0){
                start.x = roomCenters.get(start).getMaxX();
                end.x = roomCenters.get(end).getMinX();
            }else{
                start.x = roomCenters.get(start).getMinX();
                end.x = roomCenters.get(end).getMaxX();
            }
            //Make straight line if possible
            int [] straightPoint=makeStraightLine((int)roomCenters.get(start).getMinY(),(int)roomCenters.get(start).getMaxY(),(int)roomCenters.get(end).getMinY(),(int)roomCenters.get(end).getMaxY());
            if(straightPoint==null){
                start.y = roomCenters.get(start).getCenterY();
                end.y = roomCenters.get(end).getCenterY();
            }else{
                int r=random.nextInt(straightPoint[0],straightPoint[1]);
                start.y = r;
                end.y = r;
            }
        }else{
            if(DY>=0){
                start.y = roomCenters.get(start).getMaxY();
                end.y = roomCenters.get(end).getMinY();
            }else{
                start.y = roomCenters.get(start).getMinY();
                end.y = roomCenters.get(end).getMaxY();
            }
            //Make straight line if possible
            int [] straightPoint=makeStraightLine((int)roomCenters.get(start).getMinX(),(int)roomCenters.get(start).getMaxX(),(int)roomCenters.get(end).getMinX(),(int)roomCenters.get(end).getMaxX());
            if(straightPoint==null){
                start.x = roomCenters.get(start).getCenterX();
                end.x = roomCenters.get(end).getCenterX();
            }else{
                int r=random.nextInt(straightPoint[0],straightPoint[1]);
                start.x = r;
                end.x = r;
            }
        }

        //Bresenham's line algorithm
        int startX=(int)start.x;
        int startY=(int)start.y;
        int endX=(int)end.x;
        int endY=(int)end.y;

        int dx=endX-startX;
        int dy=endY-startY;
        try {
            if (Math.abs(dy) < Math.abs(dx)) {
                if (startX > endX) {
                    plotLineLow(endX, endY, startX, startY,collisionAccepted);
                } else {
                    plotLineLow(startX, startY, endX, endY,collisionAccepted);
                }
            } else {
                if (startY > endY) {
                    plotLineHigh(endX, endY, startX, startY,collisionAccepted);
                } else {
                    plotLineHigh(startX, startY, endX, endY,collisionAccepted);
                }
            }
            //Saving changes to copy
            reference.makeCopy();
        }catch (Map.AlreadyTrueException e){
            //if corridors collide, then return to previous state
            reference.backToCopy();
        }
    }

    private int[] makeStraightLine(int a,int b,int c,int d){
        int[] sorted= {a, b, c, d};
        Arrays.sort(sorted);
        //Check intersection
        if((sorted[0]==a && sorted[1]==b)||(sorted[2]==a && sorted[3]==b)){
            return null;
        }
        return new int[]{sorted[1],sorted[2]};
    }

    //y loop
    private void plotLineHigh(int startX,int startY,int endX,int endY,boolean collisionAccepted) throws Map.AlreadyTrueException{
        int dx=endX-startX;
        int dy=endY-startY;

        int xDir=1;
        if(dx<0){
            xDir=-1;
            dx=-dx;
        }
        int D=2*dx-dy;
        int x=startX;
        int yDir=1;

        for(int y=startY;y!=endY;y+=yDir){
            try {
                reference.setTerrain(x, y);
            }catch (Exception e){
                if(!collisionAccepted){
                    throw new Map.AlreadyTrueException();
                }
            }
            if(D>0){
                x+=xDir;
                try {
                    reference.setTerrain(x, y);
                }catch (Exception e){
                    if(!collisionAccepted){
                        throw new Map.AlreadyTrueException();
                    }
                }
                D+=2*(dx-dy);
            }else {
                D += 2 * dx;
            }
        }
    }

    //x loop
    private void plotLineLow(int startX,int startY,int endX,int endY,boolean collisionAccepted) throws Map.AlreadyTrueException{

        int dx=endX-startX;
        int dy=endY-startY;

        int yDir=1;
        if(dy<0){
            yDir=-1;
            dy=-dy;
        }
        int D=2*dy-dx;
        int y=startY;
        int xDir=1;

        for(int x=startX;x!=endX;x+=xDir){
            try {
                reference.setTerrain(x, y);
            }catch (Exception e){
                if(!collisionAccepted){
                    throw new Map.AlreadyTrueException();
                }
            }
            if(D>0){
                y+=yDir;
                try {
                    reference.setTerrain(x, y);
                }catch (Exception e){
                    if(!collisionAccepted){
                        throw new Map.AlreadyTrueException();
                    }
                }
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