package com.company;

public class Point {
    private double x;
    private double y;

    public Point(double userX, double userY) {
        x = userX;
        y = userY;
    }

    public double Get_X() {
        return x;
    }

    public double Get_Y() {
        return y;
    }

    public String ToString() {
        return ("{" + x + ", " + y + "}");
    }
}
