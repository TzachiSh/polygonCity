package com.map.polygon;

import android.location.Address;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codemybrainsout.placesearch.PlaceSearchDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.map.polygon.Adapters.CustomInfoWindowAdapter;
import com.map.polygon.helper.FlickrUrl;
import com.map.polygon.helper.GeocoderService;
import com.map.polygon.helper.PolygonCalc;
import com.map.polygon.helper.VolleyUtils;

import org.json.JSONException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap mGoogleMap;
    MapView mMapView;
    View view;
    EditText etSearch;
    Marker marker;
    Polygon polygon;


    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.map);

        etSearch = (EditText)view.findViewById(R.id.editText_search);

        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Show the autocomplete dialog
                    showPlacePickerDialog();
                }
                return false;
            }
        });


        return view;
    }



    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mMapView != null) {

            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
         MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            // create marker on the map where the user clicking
                setMarker(latLng);

            }
        });



    }

    private void showPlacePickerDialog() {

        final PlaceSearchDialog placeSearchDialog = new PlaceSearchDialog.Builder(getActivity())

                //.setHeaderImage(R.drawable.ic_search_black_24dp)
                .setLocationNameListener(new PlaceSearchDialog.LocationNameListener() {
                    @Override
                    public void locationName(String locationName) {

                        if(!locationName.equals("")){

                            etSearch.setText(locationName);

                            String place = etSearch.getText().toString();
                            // get the id in flickr of the place And create polygon around the place.
                            getPlaceID(place);

                            // get the Latitude and the Longitude of the place
                            GeocoderService geocoderService = new GeocoderService(getActivity(),place);
                            Address address  =  geocoderService.getAddress();


                            //Zoom to the place in the UI map
                            goToLocation(address.getLatitude(),address.getLongitude(),10);
                        }



                    }
                })

                .build();
        placeSearchDialog.show();

    }

    private void setMarker(LatLng latLng) {

        if(marker != null)
        {
            marker.remove();

        }

        String locality;
        // get the place by coordination and show in the UI the address
        GeocoderService geocoderService =  new GeocoderService(getActivity(),latLng);
        locality = geocoderService.getAddress().getAddressLine(0);
        if(locality == null)
        {
            locality = "Unknown Area";
        }
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .position(latLng);

        // Custom window to show info in the UI
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity(),polygon);
        mGoogleMap.setInfoWindowAdapter(adapter);
        marker = mGoogleMap.addMarker(options);
        marker.showInfoWindow();

    }

    private void goToLocation(double lat, double lng,float zoom) {

        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mGoogleMap.moveCamera(update);
    }

    private void getPlaceID(final String place) {
        // find Place id by name in Flickr API
        String flickerUrl = FlickrUrl.findPlaceId(place,getString(R.string.flickerApiKey));
        Log.i(flickerUrl,"Url");
        //make networking call
        final VolleyUtils volleyUtils =  new VolleyUtils(getActivity());
        volleyUtils.executeRequest(flickerUrl, new VolleyUtils.VolleyCallback() {
            @Override
            public void getResponse(String response) {
                try {
                    // convert the response to json to get the place id
                    JSONObject  jsonObject = new JSONObject(response);
                    String placeID = jsonObject.getJSONObject("places")
                            .getJSONArray("place")
                            .getJSONObject(0)
                            .getString("woeid");

                    getPlaceCoordinates(placeID);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void getPlaceCoordinates(String placeID) {
        // get the Coordinates around the place.
        String flickerUrl = FlickrUrl.findPolyline(placeID,getString(R.string.flickerApiKey));
        final  VolleyUtils volleyUtils =  new VolleyUtils(getActivity());
        volleyUtils.executeRequest(flickerUrl, new VolleyUtils.VolleyCallback() {
            @Override
            public void getResponse(String response) {
                try {
                    // convert the response to json to get the Coordinates
                    JSONObject jsonObject = new JSONObject(response);

                    String coordinates = jsonObject.getJSONObject("place")
                            .getJSONObject("shapedata").getJSONObject("polylines")
                            .getJSONArray("polyline").getJSONObject(0).getString("_content");

                    // convert the Coordinates to List from string
                    String coordsArray[] = coordinates.split("\\s+"); // Results "0,0","0,1","5,2","7,4","10,5"
                    List<LatLng> points = new ArrayList<>();
                    for(String s : coordsArray)
                    {
                        String coordXY[] = s.split(",");
                        Float latitude  = Float.parseFloat(coordXY[0]);
                        Float longitude = Float.parseFloat(coordXY[1]);
                        points.add(new LatLng(latitude,longitude));
                    }
                    // create polygon around the place
                    drawPolygon(points);





                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void drawPolygon(List<LatLng> points) {

        mGoogleMap.clear();
        PolygonOptions poly = new PolygonOptions();

        // remove older polygon
        if(polygon != null)
        {
            polygon.remove();
        }
        // create polygon
        for (LatLng location : points) {
            poly.add(location);
        }
        // show the polygon on the map
        polygon = mGoogleMap.addPolygon(poly);


    }
}