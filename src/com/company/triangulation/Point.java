package com.company.triangulation;

public class Point {
    int x;
    int y;
    PointStatus status;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        status=PointStatus.UNMARKED;
    }

    public enum PointStatus{
        MARKED,
        UNMARKED,
        SUPER,
    }
}


