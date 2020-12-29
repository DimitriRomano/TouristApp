package com.romano.dimitri.touristapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.romano.dimitri.touristapp.model.User;

import static com.romano.dimitri.touristapp.MainActivity.PREF;
import static com.romano.dimitri.touristapp.MainActivity.PREF_CONNEXION;
import static com.romano.dimitri.touristapp.MainActivity.PREF_PSEUDO;

public class DashboardActivity extends AppCompatActivity {

    private SharedPreferences mPreferencesLog;

    private DBHandler mDB;
    private User mUser;

    private int CurrentScore;
    private String PseudoUser;

    public static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPreferencesLog = getSharedPreferences(PREF,MODE_PRIVATE);

        //initialise db
        mDB = DBHandler.getInstance(this);

        //initialise the user with rights information
        mUser = mDB.getUser(mPreferencesLog.getString(PREF_PSEUDO,null));
        PseudoUser = mUser.getPseudo();
        CurrentScore = mUser.getScore();
        Log.d(TAG,PseudoUser + " " + CurrentScore);

        //test to know which session we are with
        /*if(mPreferencesLog.contains(PREF_CONNEXION) && mPreferencesLog.contains(PREF_PSEUDO)){
            Toast.makeText(getApplicationContext(),"session "+ mPreferencesLog.getString(PREF_PSEUDO,null),Toast.LENGTH_LONG).show();
        }*/

        //init 2 fragments with FragmentManager
        if(savedInstanceState == null){
            //give information to the fragmentUser fragment
            Bundle bundleUser = new Bundle();
            bundleUser.putString("pseudo",PseudoUser);
            bundleUser.putInt("score",CurrentScore);


             getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                     .add(R.id.fragment_user_container_view,UserFragment.class,bundleUser)
                    .add(R.id.fragment_map_container_view, MapsFragment.class, null)
                    .commit();
        }


        //UserFragment fragU = (UserFragment) getSupportFragmentManager();
        //fragU.setPseudo();


    }
}