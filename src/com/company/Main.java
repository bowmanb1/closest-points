package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.random.*;

public class Main {
    //keeps track of x and y components (two for generation, other two for reading data)
    private static ArrayList<Integer> x_component = new ArrayList<>();
    private static ArrayList<Integer> y_component = new ArrayList<>();
    private static ArrayList<Integer> x_components = new ArrayList<>();
    private static ArrayList<Integer> y_components = new ArrayList<>();

    private static double ClosestDistance = 3.4028235E38; //initialized with max float value
    private static Point[] ClosestPoints = new Point[2]; //array to store two closest points

    private static ArrayList<Point> points = new ArrayList<>(); //list to store points

    private static Integer max = 101; //number of points to be generated

    //Used in generation of points
    public static Boolean Check_X(Integer n) {
        for (Integer x : x_component) {
            if (Objects.equals(n, x)) {
                return true;
            }
        }
        return false;
    }

    //Used in generation of points
    public static Boolean Check_Y(Integer n) {
        for (Integer y : y_component) {
            if (Objects.equals(n, y)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
	    System.out.println("Please input a file of points: ");
        String fileName;
        Scanner input = new Scanner(System.in);

        fileName = input.nextLine();
//        GeneratePoints(); //a function that generates random points
        while (!fileName.contains(".txt")) {
            System.out.println("ERROR: MUST INPUT A TXT FILE");
            fileName = input.nextLine();
        }

        if (ReadData(fileName)) {
            GetClosestPoints();
        } else {
            System.out.println("ERROR WITH FILE");
        }
    }

    //This function generates a set of random x-y points given a max numebr
    public static void GeneratePoints() {
        Random rand = new Random();
        int rand_x;
        int rand_y;
        System.out.print("POINTS: ");
        for (int i = 0; i < max; i++) { //run until max is met
            rand_x = rand.nextInt(max);
            rand_y = rand.nextInt(max);
            while (Check_X(rand_x)) { //ensures no duplicates
                rand_x = rand.nextInt(max);
            }
            while (Check_Y(rand_y)) { //ensures no duplicates
                rand_y = rand.nextInt(max);
            }
            x_component.add(rand_x);
            y_component.add(rand_y);
        }
        for (int i = 0; i  < max; i++) { //output is format for txt file
            System.out.println("{ " + x_component.get(i) + " , " + y_component.get(i) + " } ");
        }
    }

    //This function finds a pair of the closest points from a set of 100
    public static void GetClosestPoints() {
        double closestOfHalves =  ClosestOfHalves(points, 0, points.size() - 1); //Find closest point from each half
        ReducePoints(points, closestOfHalves); //get rid of points that are further from midline than shortest distance found already
        MergeSort(points, 0, points.size() - 1, false); //sort points by y-coordinates
        ScanYPoints(points, closestOfHalves); //go through points in y-order to find a possible new smallest distance

        System.out.println("CLOSEST POINTS: " + ClosestPoints[0].ToString() + " " + ClosestPoints[1].ToString() + "\nDistance: " + Distance(ClosestPoints[0], ClosestPoints[1])); //output solution!
    }

    //Calculates distance between two points
    public static double Distance(Point a, Point b) {
        return Math.sqrt((Math.pow((a.Get_X() - b.Get_X()), 2)) + (Math.pow((a.Get_Y() - b.Get_Y()), 2)));
    }

    //Iterates through points sorted in y-order close to the midline to find the smallest distance
    public static double ScanYPoints(ArrayList<Point> pointList, double closest) {
        for (int i = 0; i < pointList.size(); i++) { //for each point remaining
            for (int j = i + 1; j < i + 11; j++) { //check next 11 points
                if (j < pointList.size()) { //check that we are not out of bounds
                    if (Distance(pointList.get(i), pointList.get(j)) < closest) {
                        ClosestPoints[0] = pointList.get(i); //store point
                        ClosestPoints[1] = pointList.get(j); //store point
                        return Distance(pointList.get(i), pointList.get(j)); //return new smallest distance
                    }
                }
            }
        }
        return closest; //closest distance remains same
    }

    //Gets rid of any points further than the closest distance found so far from the midline
    public static ArrayList<Point> ReducePoints(ArrayList<Point> pointList, double closest) {
        double mid = Distance(pointList.get(0), pointList.get(pointList.size() - 1)) / 2; //average distance between points
        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.get(i);
            Point midPoint = new Point(mid, point.Get_Y()); //use same y-value to ensure straight line
            if (Distance(point, midPoint) > closest) {
                pointList.remove(point);
                i--; //point removed, so iteration must be shifted down one to avoid skipping
            }
        }
        return pointList; //adjusted list of points
    }

    public static double ClosestOfHalves(ArrayList<Point> pointList, int start, int end) {
        if ((end - start == 1) || (end - start == 2)) { //if there are two or three points left
            if (end - start == 1) { //two points left, return distance between
                Point first = pointList.get(start);
                Point second = pointList.get(end);
                return Distance(first, second);
            } else { //three points left, combination with shortest distance
                Point first = pointList.get(start);
                Point second = pointList.get(start + 1);
                Point third = pointList.get(end);
                if (Distance(first, second) < Distance(first, third) && (Distance(first, second) < Distance(second, third))) {
                    if (Distance(first, second) < ClosestDistance) { //checks if this is the new overall closest distance
                        ClosestDistance = Distance(first, second);
                        ClosestPoints[0] = first;
                        ClosestPoints[1] = second;
                    }
                    return Distance(first, second);
                } else if (Distance(first, third) < Distance(first, second) && Distance(first, third) < Distance(second, third)) {
//                    System.out.println("Adding points" + first.ToString() + third.ToString());
                    if (Distance(first, third) < ClosestDistance) { //checks if this is the new overall closest distance
                        ClosestDistance = Distance(first, third);
                        ClosestPoints[0] = first;
                        ClosestPoints[1] = third;
                    }
                    return Distance(first, third);
                }
                if (Distance(second, third) < ClosestDistance) { //checks if this is the new overall closest distance
                    ClosestDistance = Distance(second, third);
                    ClosestPoints[0] = second;
                    ClosestPoints[1] = third;
                }
                return Distance(second, third);
            }
        }
        int separate = start + (end - start) / 2; //separation line
        //get the closest distance for each side
        double left = ClosestOfHalves(pointList, start, separate);
        double right = ClosestOfHalves(pointList, separate + 1, end);

        return Math.min(left, right); //return minimum of found value
    }

    //Reads values from file
    public static Boolean ReadData(String fileName) {
        int x = 0;
        int y = 0;
        try {
            File data = new File(fileName);
            Scanner getData = new Scanner(data);

            while (getData.hasNext()) {
                String element = getData.next();
                if (element.contains("{")) {
                    element = getData.next();
                    try {
                        x = Integer.parseInt(element); //convert to integer
                        x_components.add(x);
                    } catch (NumberFormatException nfe) { //check if input is in valid number format
                        System.out.println("ERROR: INVALID NUMBER IN FILE");
                        return false;
                    }
                } else if (element.contains(",")) {
                    element = getData.next();
                    try {
                        y = Integer.parseInt(element); //convert to integer
                        y_components.add(y);
                    } catch (NumberFormatException nfe) { //check if input is in valid number format
                        System.out.println("ERROR: INVALID NUMBER IN FILE");
                        return false;
                    }
                } else if (element.contains("}")) {
                    Point point = new Point(x, y);
                    points.add(point);
                }
            }
        } catch (FileNotFoundException error) {
            System.out.println("ERROR: FILE NOT FOUND");
            return false;
        }
        return true;
    }

    //Merges while considering whether it should be done by x or y-values
    public static void Merge(ArrayList<Point> pointList, int start, int middle, int end, Boolean sortX) {
        ArrayList<Point> sorted_components = new ArrayList<>();
        int trackStart = start;
        int trackMid = middle + 1;

        if (sortX) { //sort for x-values
            while (trackStart <= middle && trackMid <= end) {
                if (pointList.get(trackStart).Get_X() <= pointList.get(trackMid).Get_X()) {
                    sorted_components.add(pointList.get(trackStart));
                    trackStart++;
                } else {
                    sorted_components.add(pointList.get(trackMid));
                    trackMid++;
                }
            }
        } else { //sort for y-values
            while (trackStart <= middle && trackMid <= end) {
                if (pointList.get(trackStart).Get_Y() <= pointList.get(trackMid).Get_Y()) {
                    sorted_components.add(pointList.get(trackStart));
                    trackStart++;
                } else {
                    sorted_components.add(pointList.get(trackMid));
                    trackMid++;
                }
            }
        }

        while (trackStart <= middle || trackMid <= end) { //finish up any values not yet reached
            if (trackStart <= middle) {
                sorted_components.add(pointList.get(trackStart));
                trackStart++;
            } else {
                sorted_components.add(pointList.get(trackMid));
                trackMid++;
            }
        }

        int i = start;
        for (Point point : sorted_components) { //copy values to arraylist
            pointList.set(i, point);
            i++;
        }
    }

    //Merge sort function that can sort by x-values or y-values depending on boolean value
    public static void MergeSort(ArrayList<Point> pointList, int start, int end, Boolean sortX) {
        if (pointList.size() == 1 || start >= end || (end - start) <= 0) { //base case for when dividing should end
            return;
        }
        int middle = start + (end - start) / 2; //get middle

        MergeSort(pointList, start, middle, sortX); //first half
        MergeSort(pointList, middle + 1, end, sortX); //second half

        Merge(pointList, start, middle, end, sortX); //merge!
    }
}
