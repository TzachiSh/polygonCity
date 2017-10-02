package com.map.polygon.Adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.map.polygon.R;
import com.map.polygon.helper.PolygonCalc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by zahi on 21/09/2017.
 */
// Adapter to show info about the place was marked
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    private Polygon polygon;

    public CustomInfoWindowAdapter(Activity context, Polygon polygon) {
        this.context = context;
        this.polygon = polygon;


    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // init the adapter
        View view = context.getLayoutInflater().inflate(R.layout.info_window, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_locality);
        TextView tvDistance = (TextView) view.findViewById(R.id.tv_distance);
        TextView tvInside = (TextView) view.findViewById(R.id.tv_inside);


        tvTitle.setText(marker.getTitle());
        tvDistance.setText("Please create polygon");
        tvDistance.setText("");
        tvInside.setText("");

       // Check if have a polygon on the map
        if (polygon != null) {
            List<LatLng> polyPoints = polygon.getPoints();
            // calculate if the mark inside the polygon and show in the UI
            PolygonCalc polygonCalc = new PolygonCalc(polyPoints, marker.getPosition());
            boolean isInside = polygonCalc.pointInPolygon();
            Log.d("isInside", "bool" + isInside);
            tvInside.setText("Inside: " + Boolean.toString(isInside));


            if (!isInside) {
                // calculate the distance the polygon from the mark and show in the UI
                double shortDistance = polygonCalc.shortestDistance();
                NumberFormat formatter = new DecimalFormat("distance: #0.00 Km");
                String minDistance = formatter.format(shortDistance);
                tvDistance.setText("" + minDistance);
                }



            }

        return view;
    }
}