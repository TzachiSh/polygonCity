package com.map.polygon.helper;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
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

    public double shortestDistance() {
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

        return min;




    }

    private double distanceToLine(double x1, double y1, double x2, double y2, double x3, double y3) {
        // Return minimum distance between line SEGMENTS (x1,y1)(x2,y2) and point (x3,y3)

        double dLineX = x2 - x1;
        double dLineY = y2 - y1;
        double temp = (dLineX * dLineX) + (dLineY * dLineY);
        double u = ((x3 - x1) * dLineX + (y3 - y1) * dLineY) / (temp);

        //if this is > 0 then its outside the line segment

        if (u <= 0.0) {

            return circleDistanceTwoPoints(y3,x3, y1, x1);

        } else if (u >= 1.0) {
            return circleDistanceTwoPoints(y3,x3, y2, x2);

        } else {

            double x = u * dLineX;
            double y = u * dLineY;

            double dx = x3 - x1;
            double dy = y3 - y1;
            return circleDistanceTwoPoints(dy,dx,y,x);

        }
    }

    private double circleDistanceTwoPoints(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;

        double a = 0.5 - Math.cos(toRadian(lat2 - lat1))/2 +
                Math.cos(toRadian(lat1)) *  Math.cos(toRadian(lat2)) *
                        (1 -  Math.cos(toRadian(lng2 - lng1)))/2;

        return (earthRadius * 2.0) * Math.asin(Math.sqrt(a)); // distance in Km

    }
    private  double toRadian(double x) {
        return  x * 0.017453292519943295;    // Math.PI / 180
    }

}

