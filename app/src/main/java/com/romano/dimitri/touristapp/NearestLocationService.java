package com.romano.dimitri.touristapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.romano.dimitri.touristapp.model.Place;

import java.util.ArrayList;


public class NearestLocationService extends IntentService {
    Location currentLocation;
    ArrayList<Place> notVisitedPlace;
    Place closestPlace = null;


    public static final String TAG = "NearestLocationService";

    public NearestLocationService() {
        super("NearestLocationService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"Receive location to check nearest");
        currentLocation = (Location)intent.getExtras().get("currentLocation");
        notVisitedPlace = (ArrayList<Place>)intent.getExtras().getSerializable("listNotVisitedPlaces");
        float dist = nearestLocation();
        if(dist != -1){
            Log.d(TAG,"closest dest from current location " + closestPlace.getTitle() + "distance de " + dist  );
            Intent
        }
        //Log.d(TAG,""+ currentLocation.getLongitude() + " " + currentLocation.getLatitude() + " " + notVisitedPlace.get(0).getTitle() );
    }

    private float nearestLocation(){
        float smallestDist = -1 ;
        for (Place place : notVisitedPlace){
            Location loc = new Location("");
            loc.setLatitude(place.getLatitude());
            loc.setLongitude(place.getLongitude());
            float distance = currentLocation.distanceTo(loc);
            if(smallestDist == -1 || distance < smallestDist){
                smallestDist = distance;
                closestPlace = place;
            }
        }
        return smallestDist;
    }


}