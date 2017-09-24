package com.map.polygon.helper;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zahi on 21/09/2017.
 */

public class PolygonCalc {
    private List<LatLng> polygon;
    private LatLng point;

    public PolygonCalc(List<LatLng> polygon, LatLng point) {
        this.polygon = polygon;
        this.point = point;
    }
    public boolean pointInPolygon() {

        double x = point.longitude, y = point.latitude;


        Boolean inside = false;
        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            double xi = polygon.get(i).longitude, yi = polygon.get(i).latitude;
            double xj = polygon.get(j).longitude, yj = polygon.get(j).latitude;

            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
        }
    public double shortestDistance()
    {
        double min = 0;
        double temp;

        // loop over all the lines of the polygon to find the low distance from the point
        for (int i = 0; i < polygon.size() - 1; i++) {
            double distance = distanceToLine(polygon.get(i).longitude, polygon.get(i).latitude,
                    polygon.get(i + 1).longitude, polygon.get(i + 1).latitude,
                    point.longitude, point.latitude);
            if (i == 0) min = distance;

            temp = distance;

            if (temp < min) min = temp;
        }
        return  min;


    }
    private double distanceToLine(double x1,double y1,double x2,double y2,double x3,double y3){
        // Return minimum distance between line SEGMENTS (x1,y1)(x2,y2) and point (x3,y3)
        double lineX = x2 - x1;
        double lineY = y2 - y1;
        double slope = (lineX * lineX) + (lineY * lineY);
        double u = ((x3 - x1) * lineX + (y3 - y1) * lineY) / (slope);

        //if this is > 0 then its outside the line segment
        u = (u>1) ?  1 : 0 ;

        double x = x1 + u * lineX;
        double y = y1 + u * lineY;

        double dx = x - x3;
        double dy = y - y3;
        double distance = Math.sqrt(dx*dx + dy*dy);
        return distance;

    }



  }


