package com.company.triangulation;

import java.awt.*;
import java.awt.geom.Area;

public class Triangle {
    Point p1;
    Point p2;
    Point p3;

    double centreX;
    double centreY;
    double radius;

    final Polygon collisionObject;

    public Triangle(Point p1, Point p2, Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        calculateCentre();
        collisionObject=new Polygon(new int[]{p1.x,p2.x,p3.x},new int[]{p1.y,p2.y,p3.y},3);
    }

    private void calculateCentre(){
        double X1Med=(double)(p1.x+ p2.x)/2;
        double Y1Med=(double)(p1.y+ p2.y)/2;
        double X2Med=(double)(p1.x+ p3.x)/2;
        double Y2Med=(double)(p1.y+ p3.y)/2;

    }

    public boolean isCollision(Triangle t){
        //Create new areas from triangle
        Area area=new Area(collisionObject);
        Area areaCopy= (Area) area.clone();
        //Remove shared part
        area.intersect(new Area(t.collisionObject));
        //Remove cut area from full area
        areaCopy.intersect(area);
        //Check if area was cut
        return !areaCopy.isEmpty();
    }
}
