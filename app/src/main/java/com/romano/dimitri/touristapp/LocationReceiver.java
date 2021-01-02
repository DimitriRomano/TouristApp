package com.romano.dimitri.touristapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;


public class LocationReceiver extends IntentService {
    Location location;

    public LocationReceiver() {
        super("LocationReceiver");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("LocationReceiver","Receive location");
        // @TODO extract location and compare the getLocation to see the nearest location ( Place )
        location = (Location)intent.getExtras().get("currentLocation");
        Log.d("LocationReceiver",""+ location.getLongitude() + " " +location.getLatitude());
    }


}