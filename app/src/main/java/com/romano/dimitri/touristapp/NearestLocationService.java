package com.romano.dimitri.touristapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

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
            //Log.d(TAG,"closest dest from current location " + closestPlace.getTitle() + "distance de " + dist  );
            createNotificationChannel();
            notificationSend();
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
                notificationSend();
            }
        }
        return smallestDist;
    }


    public void notificationSend(){
        String CHANNEL_ID = "chan_location_1";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Receive nearest loc")
                .setContentText("\"nearest location from you : \" + closestPlace.getTitle() ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line .."));



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //when notification is trigger
        Intent intent = new Intent(MapsFragment.INTENT_FILTER);
        if(closestPlace!=null){
            intent.putExtra("closest_place",closestPlace);
        }
        //intent.setAction("com.romano.dimitri.touristapp.NEAREST_LOCATION_NOTIF");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Nearest Location")
                .setContentText("nearest location from you : " + closestPlace.getTitle())
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "lol";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String CHANNEL_ID = "chan_location_1";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}