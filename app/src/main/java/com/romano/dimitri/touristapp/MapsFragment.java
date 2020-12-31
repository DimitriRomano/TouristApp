package com.romano.dimitri.touristapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
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
import android.widget.Button;
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
    private String mLocationProvider;
    private LocationManager mLocationManager;
    private FloatingActionButton current_location_btn;
    private GoogleMap mMap;
    private LocationListener locationListener;


    private static final String TAG = "MapFragment";
    private static final int LOCALISATION_REQUEST = 30;
    private static final int MULTIPLE_LOCATION_REQUEST = 42;


    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

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
            //updateMap();
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

        current_location_btn = getActivity().findViewById(R.id.current_location_btn);
        current_location_btn.setOnClickListener(this::getSelfLocation);


        //set location provider
        mLocationProvider = LocationManager.GPS_PROVIDER;

        //acquire reference to the system Location Manager
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);


        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the location provider
                Log.d(TAG, "Location changed");
                Toast.makeText(getActivity(), "Location changed, updating map...",
                        Toast.LENGTH_SHORT).show();
                mCurrentLocalisation = location;
                if (mMap != null) {
                    updateMap();
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
            Log.d(TAG, "my currentLocalisation : " + mCurrentLocalisation);
            /*if (mCurrentLocalisation == null) {
                mCurrentLocalisation = new Location(mLocationProvider);
                mCurrentLocalisation.setLatitude(48.858093);
                mCurrentLocalisation.setLongitude(2.294694);
            }
            LatLng pin = new LatLng(mCurrentLocalisation.getLatitude(), mCurrentLocalisation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pin));
            mMap.addMarker(new MarkerOptions().position(pin).title("Current location"));*/
            if(mCurrentLocalisation==null){
            try {
                locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no GPS Provider and no network provider is enabled
                } else {   // Either GPS provider or network provider is enabled

                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null)
                            {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                this.canGetLocation = true;
                            }
                        }
                    }// End of IF network enabled

                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled)
                    {
                        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null)
                            {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                this.canGetLocation = true;
                            }
                        }

                    }// End of if GPS Enabled
                }// End of Either GPS provider or network provider is enabled

            } catch (Exception e)
            {
                e.printStackTrace();
            }

            // If GPS is enabled, the GPS coordinates will be returned in location.
            // If GPS is not enabled, then the network coordinates will be returned.
            // If both are enabled, then GPS co orindate will be returned because GPS coordinates
            mCurrentLocalisation = location;
            }
            if(mCurrentLocalisation != null) {
                LatLng pin = new LatLng(mCurrentLocalisation.getLatitude(), mCurrentLocalisation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pin));
                mMap.addMarker(new MarkerOptions().position(pin).title("Current location"));
            }
        }
    }

    public void getSelfLocation(View view) {
        locationEnabled();
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
                    .setMessage( "GPS not Enable" )
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton( "Cancel" , null )
                    .show() ;
        }else{
            updateMap();
        }
    }

    private void requestLocationUpdates() {
        // Check permission
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

            // Permission already granted
            mLocationManager.requestLocationUpdates(
                    mLocationProvider, 5000, 1, locationListener);
            //mMap.setMyLocationEnabled(true);
                    updateMap();

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
            case LOCALISATION_REQUEST:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    requestLocationUpdates();
                }else{
                    Toast.makeText(getActivity(),"Permission denied to access device's location",Toast.LENGTH_SHORT).show();
                }
            }
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

