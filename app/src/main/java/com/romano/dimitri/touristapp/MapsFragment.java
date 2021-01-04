package com.romano.dimitri.touristapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.romano.dimitri.touristapp.model.Place;
import com.romano.dimitri.touristapp.model.User;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements OnMapReadyCallback{
    private Location mCurrentLocalisation = null;
    private Location mLocation;
    private String mLocationProvider;
    private LocationManager mLocationManager;
    private FloatingActionButton current_location_btn;
    private GoogleMap mMap;
    private LocationListener locationListener;
    private SupportMapFragment mapFragment;
    private DBHandler db;
    private String mPseudo;
    private ArrayList<Place> placesAL, alreadyVisitedPlacesAL;
    private int placePositionAL;
    private User mUser;
    private ProcessLevel processLevel;

    public static final String TAG = "MapFragment";
    private static final int MULTIPLE_LOCATION_REQUEST = 42;


    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    //button activated
    boolean btnActivation = false;

    Location lastLocation; // last location known

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 15; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public static final String INTENT_FILTER= "NEAREST_LOCATION_NOTIF";
    NearestLocationReceiver mNearestLocationReceiver;

    // Declaring a Location Manager
    protected LocationManager locationManager;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        db = DBHandler.getInstance(this.getContext());
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNearestLocationReceiver = new NearestLocationReceiver();
        mPseudo = requireArguments().getString("pseudo");

        placesAL = db.placeVisitedUser(mPseudo, false);
        alreadyVisitedPlacesAL = db.placeVisitedUser(mPseudo, true);
        mUser = db.getUser(mPseudo);
        processLevel = new ProcessLevel(alreadyVisitedPlacesAL, mUser);

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //set button for self location
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
                Log.d(TAG, "Location changed" + location.getLongitude() + " " + location.getLatitude());
                mCurrentLocalisation = location;
                if (mMap != null ) {
                    updateMap();

                    if (location != null && btnActivation == true) {
                        Intent i = new Intent(getActivity(), NearestLocationService.class);
                        i.putExtra("currentLocation", location);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("listNotVisitedPlaces", placesAL);
                        i.putExtras(bundle);
                        getActivity().startService(i);
                    }
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
        //register the broadcast receiver ( local )
        IntentFilter filter = new IntentFilter(INTENT_FILTER);
        getActivity().registerReceiver(mNearestLocationReceiver,filter);

    }


    @Override
    public void onPause() {
        super.onPause();

        // Remove the listener you previously added
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(locationListener);
        }
        //remove the broadcast receiver
        getActivity().unregisterReceiver(mNearestLocationReceiver);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showAllPlaces(ArrayList<Place> arrayListPlaces, boolean alreadyVisited){
        Drawable iconDrawable;
        BitmapDescriptor icon;
        for(placePositionAL=0; placePositionAL<arrayListPlaces.size(); placePositionAL++){
            LatLng placeLatLng = new LatLng(arrayListPlaces.get(placePositionAL).getLatitude(), arrayListPlaces.get(placePositionAL).getLongitude());
            if(alreadyVisited == false) {
                switch (arrayListPlaces.get(placePositionAL).getType()) {
                    case "Stadium":
                        iconDrawable = getResources().getDrawable(R.drawable.ic_stadium);
                        icon = getMarkerIconFromDrawable(iconDrawable);
                        mMap.addMarker(new MarkerOptions().position(placeLatLng)
                                .title(arrayListPlaces.get(placePositionAL).getTitle())
                                .snippet(arrayListPlaces.get(placePositionAL).getDescription() + " ID :" + arrayListPlaces.get(placePositionAL).getId() + ":" +
                                        arrayListPlaces.get(placePositionAL).getType()).icon(icon));
                                /*.icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));*/
                        break;
                    case "Museum":
                        iconDrawable = getResources().getDrawable(R.drawable.ic_museum);
                        icon = getMarkerIconFromDrawable(iconDrawable);
                        mMap.addMarker(new MarkerOptions().position(placeLatLng)
                                .title(arrayListPlaces.get(placePositionAL).getTitle())
                                .snippet(arrayListPlaces.get(placePositionAL).getDescription() + " ID :" + arrayListPlaces.get(placePositionAL).getId() + ":" +
                                        arrayListPlaces.get(placePositionAL).getType()).icon(icon));
                                /*.icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));*/
                        break;
                    case "Castle":
                        iconDrawable = getResources().getDrawable(R.drawable.ic_castle);
                        icon = getMarkerIconFromDrawable(iconDrawable);
                        mMap.addMarker(new MarkerOptions().position(placeLatLng)
                                .title(arrayListPlaces.get(placePositionAL).getTitle())
                                .snippet(arrayListPlaces.get(placePositionAL).getDescription() + " ID :" + arrayListPlaces.get(placePositionAL).getId() + ":" +
                                        arrayListPlaces.get(placePositionAL).getType()).icon(icon));
                                /*.icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));*/
                        break;
                    case "Church":
                        iconDrawable = getResources().getDrawable(R.drawable.ic_church);
                        icon = getMarkerIconFromDrawable(iconDrawable);
                        mMap.addMarker(new MarkerOptions().position(placeLatLng)
                                .title(arrayListPlaces.get(placePositionAL).getTitle())
                                .snippet(arrayListPlaces.get(placePositionAL).getDescription() + " ID :" + arrayListPlaces.get(placePositionAL).getId() + ":" +
                                        arrayListPlaces.get(placePositionAL).getType()).icon(icon));
                                /*.icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));*/
                        break;
                    case "Monument":
                        iconDrawable = getResources().getDrawable(R.drawable.ic_monument);
                        icon = getMarkerIconFromDrawable(iconDrawable);
                        mMap.addMarker(new MarkerOptions().position(placeLatLng)
                                .title(arrayListPlaces.get(placePositionAL).getTitle())
                                .snippet(arrayListPlaces.get(placePositionAL).getDescription() + " ID :" + arrayListPlaces.get(placePositionAL).getId() + ":" +
                                        arrayListPlaces.get(placePositionAL).getType()).icon(icon));
                                //.icon(BitmapDescriptorFactory
                                //        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        break;
                    default:
                        mMap.addMarker(new MarkerOptions().position(placeLatLng)
                                .title(arrayListPlaces.get(placePositionAL).getTitle())
                                .snippet(arrayListPlaces.get(placePositionAL).getDescription() + " ID :" + arrayListPlaces.get(placePositionAL).getId() + ":" +
                                        arrayListPlaces.get(placePositionAL).getType())
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                        break;
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    public void onInfoWindowClick(Marker marker) {
                        if((mCurrentLocalisation.getLatitude() == marker.getPosition().latitude && mCurrentLocalisation.getLongitude() == marker.getPosition().longitude) == false) {
                            float[] distance = new float[1];
                        String snippetContent = marker.getSnippet();
                        String[] splittedSnippet = snippetContent.split(":");
                        int score = 0;
                        boolean alreadyValidated = false;

                            Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, mCurrentLocalisation.getLatitude(), mCurrentLocalisation.getLongitude(), distance);
                            for (int j = 0; j < alreadyVisitedPlacesAL.size(); j++) {
                                LatLng pos = new LatLng(alreadyVisitedPlacesAL.get(j).getLatitude(), alreadyVisitedPlacesAL.get(j).getLongitude());
                                if (pos.equals(marker.getPosition())) {
                                    alreadyValidated = true;
                                }
                            }
                            if (alreadyValidated == false) {
                                if (distance[0] <= 200) {
                                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    db.addVisit(mPseudo, Integer.parseInt(splittedSnippet[1]));
                                    Toast.makeText(getActivity(), "Congrats, " + marker.getTitle() + " is now validated !", Toast.LENGTH_SHORT).show();
                                    placesAL = db.placeVisitedUser(mPseudo, false);
                                    alreadyVisitedPlacesAL = db.placeVisitedUser(mPseudo, true);
                                    System.out.println("Le score de " + mPseudo + " est : " + score);
                                    score = processLevel.givePoint(mUser, alreadyVisitedPlacesAL);
                                    System.out.println("Le nvx score de " + mPseudo + " est : " + score);
                                    mUser.setScore(score);
                                    db.updateUser(mUser);
                                } else {
                                    Toast.makeText(getActivity(), "You are too far from this location, please get closer.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), marker.getTitle() + " is already validated !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(),"You are too far from this location, please get closer.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                mMap.addMarker(new MarkerOptions().position(placeLatLng)
                        .title(arrayListPlaces.get(placePositionAL).getTitle())
                        .snippet(arrayListPlaces.get(placePositionAL).getDescription() + " ID :" + arrayListPlaces.get(placePositionAL).getId() + ":" +
                                arrayListPlaces.get(placePositionAL).getType())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatLng));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    System.out.println(marker.getTitle());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
                    return false;
                }
            });
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {
                    View v = getLayoutInflater().inflate(R.layout.marker_info_layout, null);

                    LatLng latLng = arg0.getPosition();
                    String title = arg0.getTitle();
                    String description = arg0.getSnippet();

                    TextView tv1 = (TextView) v.findViewById(R.id.titleView);
                    TextView tv2 = (TextView) v.findViewById(R.id.coordinatesView);
                    TextView tv3 = (TextView) v.findViewById(R.id.descriptionView);

                    tv1.setText(title);
                    tv2.setText(latLng.latitude + " ; " + latLng.longitude);
                    tv3.setText(description);
                    return v;
                }
            });
        }
    }


    private void updateMap() {
        Log.d(TAG, "Updating map...");
        if (mMap != null) {
            mMap.clear();
            //db.getVisited(mPseudo);
            showAllPlaces(alreadyVisitedPlacesAL, true);
            showAllPlaces(placesAL, false);
            Log.d(TAG, "my currentLocalisation : " + mCurrentLocalisation);

            if (mCurrentLocalisation == null) {
                if (lastLocation != null) {
                    mCurrentLocalisation = lastLocation;
                }
            } else {
                LatLng pin = new LatLng(mCurrentLocalisation.getLatitude(), mCurrentLocalisation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pin, 13));
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

    //method to check if provider are enable on check localisation button
    private void locationEnabled(){
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try{
            gps_enabled = mLocationManager.isProviderEnabled(mLocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            network_enabled = mLocationManager.isProviderEnabled(mLocationManager.NETWORK_PROVIDER ) ;
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

    //inner declaration of the broadcast
    public class NearestLocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                Place myPlace = (Place) intentExtras.get("closest_place");
                if(mMap!=null){
                    CameraUpdate zoomNearestLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(myPlace.getLatitude(),myPlace.getLongitude()),15);
                    mMap.animateCamera(zoomNearestLocation);
                }
            }
        }
    }

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
        LatLng france_center = new LatLng(46.468133, 2.849159);
        mMap = googleMap;
        updateMap();
        btnActivation =false;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(france_center, (float) 5));
        //updateMap();
    }
}

