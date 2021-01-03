package com.romano.dimitri.touristapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment {
    private Location mCurrentLocalisation = null;
    private Location mLocation;
    private LocationManager mLocationManager;
    private FloatingActionButton current_location_btn;
    private GoogleMap mMap;
    private LocationListener locationListener;


    private static final String TAG = "MapFragment";
    private static final int MULTIPLE_LOCATION_REQUEST = 42;


    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    //button activated
    boolean btnActivation = false;

    Location lastLocation; // last location known

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000  * 5;
    // Declaring a Location Manager
    protected LocationManager locationManager;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng paris = new LatLng(48.858093, 2.294694);
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 6));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        //set button for self location
        current_location_btn = getActivity().findViewById(R.id.current_location_btn);
        current_location_btn.setOnClickListener(this::getSelfLocation);

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);


        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the location provider
                Log.d(TAG, "Location changed" + location.getLongitude() + " " + location.getLatitude() );
                //Toast.makeText(getActivity(), "Location changed, updating map...",
                        //Toast.LENGTH_SHORT).show();
                mCurrentLocalisation = location;
                if (mMap != null && btnActivation==true) {
                    updateMap();
                }
                if(location != null){
                    Intent i = new Intent(getActivity(),LocationReceiver.class);
                    i.putExtra("currentLocation",location);
                    getActivity().startService(i);
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdates();
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==true && btnActivation==true){
            updateMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove the listener you previously added
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(locationListener);
        }
    }

    private void updateMap() {
        Log.d(TAG, "Updating map...");
        if (mMap != null) {
            mMap.clear();
            if(mCurrentLocalisation == null) {
                if(lastLocation != null){
                    mCurrentLocalisation = lastLocation;
                }
            }
            if(mCurrentLocalisation != null){
                LatLng pin = new LatLng(mCurrentLocalisation.getLatitude(), mCurrentLocalisation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pin));
                mMap.addMarker(new MarkerOptions().position(pin).title("Current location"));
            }
        }
    }

    //when button selfLocalisation is clicked
    public void getSelfLocation(View view) {
        locationEnabled();
        requestLocationUpdates();
        updateMap();
    }

    private void locationEnabled(){
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try{
            gps_enabled = mLocationManager.isProviderEnabled(mLocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            network_enabled = mLocationManager.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }

        if (!gps_enabled && !network_enabled ) {
            new AlertDialog.Builder(getActivity() )
                    .setMessage( "GPS not Enable please turn on" )
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton( "Cancel" , null )
                    .show() ;
        }
        btnActivation = true;
    }

    private void requestLocationUpdates() {
        // Check permission
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

            // getting GPS status
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // First get location from Network Provider
            if (isNetworkEnabled)
            {
                mLocationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                if (locationManager != null)
                {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled)
            {
                mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                if (locationManager != null)
                {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

            }




        } else {
            // Request permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},
                    MULTIPLE_LOCATION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_LOCATION_REQUEST:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    requestLocationUpdates();
                }
                else{
                    Toast.makeText(getActivity(),"Permission denied to access device's location",Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
    }
}

