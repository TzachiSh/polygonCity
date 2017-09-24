package com.map.polygon;


import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        if(googleServicesAvailable()){
            Toast.makeText(this, "Perfect", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);

            initFragment();


        }


    }
    private void initFragment(){

        // Show map fragment
        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,mapFragment).commit();


    }
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);

        // Check if google service available
        if(isAvailable == ConnectionResult.SUCCESS){
            return  true;
        }else if (api.isUserResolvableError(isAvailable)){

            Dialog dialog = api.getErrorDialog(this,isAvailable,0);
            dialog.show();
        }else {

            Toast.makeText(this, "Cant connect to play service", Toast.LENGTH_LONG).show();
        }
        return  false;

    }
}
