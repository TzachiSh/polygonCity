package com.map.polygon.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;

import static android.R.id.list;

/**
 * Created by zahi on 18/09/2017.
 */

public class GeocoderService {
    private Context context;
    private LatLng latLng;
    private String addressName;

    public GeocoderService(Context context ,LatLng latLng ){
        this.context = context;
        this.latLng = latLng;

    }
    public  GeocoderService(Context context , String addressName){
        this.context = context;
        this.addressName = addressName;

    }
    public Address getAddress(){

        Geocoder gc = new Geocoder(context);

        List<Address> listAddresses = null;
        try {
            // find addresses by name or coordinate
            listAddresses = (latLng != null) ?  gc.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    : gc.getFromLocationName(addressName,1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = ((listAddresses.size() > 0) ? listAddresses.get(0)
                : new Address(Locale.getDefault()));

        String locality = address.getCountryName();

        Toast.makeText(context,locality,Toast.LENGTH_LONG)
                .show();

        return  address;

    }


}
