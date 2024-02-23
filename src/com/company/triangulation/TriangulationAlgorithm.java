package com.company.triangulation;

import delaunay.BowyerWatson;
import mst.Kruskal;
import structure.DEdge;
import structure.DPoint;
import structure.DTriangle;

import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class TriangulationAlgorithm {

    private static final String filename="input.txt";
    private ArrayList<Triangle> triangles;
    private ArrayList<Triangle> newTriangles;
    private ArrayList<Line> lines;

    public void DelaunayTriangulation(ArrayList<Point> rectangleCentres, int maxX, int maxY){
        ArrayList<DPoint> points=new ArrayList<>();
        for(Point p:rectangleCentres){
            points.add(new DPoint(p.x,p.y));
        }
        BowyerWatson bw=new BowyerWatson(maxX,maxY,points);
        //DTriangle t=new DTriangle();
        HashSet<DEdge> edges=bw.getPrunEdges();
        Kruskal kruskal=new Kruskal(points,edges);
        edges=kruskal.getMST();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for(DEdge edge:edges){
                String s=edge.p[0].x+","+edge.p[0].y+","+edge.p[1].x+","+edge.p[1].y+"\n";
                writer.write(s);
            }
            writer.close();
        }catch(IOException e){
            System.err.println(e.getMessage());
        }


        /*initialization(maxX,maxY);

        for(Point p:rectangleCentres){
            //Draw new triangles. Only not colliding with others.
            newTriangles=new ArrayList<>();
            for(Line line:lines){
                Triangle triangle=new Triangle(p,line.start,line.end);
                if(!checkTriangleCollision(triangle)){
                    newTriangles.add(triangle);
                }
            }
            //Check if circumcircle contains points
            int iter= newTriangles.size()-1;
            for(;iter>=0;iter--){

            }
        }*/
    }

    private void initialization(int maxX, int maxY){
        lines=new ArrayList<>();
        triangles=new ArrayList<>();

        //Define super triangle
        //It will be isosceles right triangle
        Point topPoint=new Point(0,-maxY);
        topPoint.status= Point.PointStatus.SUPER;
        Point bottomPoint=new Point(0,maxY);
        bottomPoint.status= Point.PointStatus.SUPER;
        Point rightPoint=new Point(2*maxX,maxY);
        rightPoint.status= Point.PointStatus.SUPER;

        Line l=new Line(topPoint,bottomPoint);
        l.isSuper=true;
        lines.add(l);

        l=new Line(topPoint,rightPoint);
        l.isSuper=true;
        lines.add(l);

        l=new Line(rightPoint,bottomPoint);
        l.isSuper=true;
        lines.add(l);
    }

    private boolean checkTriangleCollision(Triangle triangle){
        for(Triangle t:triangles){
            if(t.isCollision(triangle)){
                return true;
            }
        }
        return false;
    }
}
