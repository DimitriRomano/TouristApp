package com.romano.dimitri.touristapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.romano.dimitri.touristapp.model.User;

import static com.romano.dimitri.touristapp.MainActivity.PREF;
import static com.romano.dimitri.touristapp.MainActivity.PREF_PSEUDO;

public class DashboardActivity extends AppCompatActivity {

    private SharedPreferences mPreferencesLog;

    private DBHandler mDB;
    private User mUser;

    private int currentScore;
    private int userAge;
    private String pseudoUser;
    private byte[] imageData;

    public static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mPreferencesLog = getSharedPreferences(PREF,MODE_PRIVATE);

        //initialise db
        mDB = DBHandler.getInstance(this);

        //initialise the user with rights information
        mUser = mDB.getUser(mPreferencesLog.getString(PREF_PSEUDO,null));
        pseudoUser = mUser.getPseudo();
        currentScore = mUser.getScore();
        userAge = mUser.getAge();

        Log.d(TAG, "Pseudo: " + pseudoUser + "; Score: " + currentScore + "; Age: " + userAge);

        //test to know which session we are with
        /*if(mPreferencesLog.contains(PREF_CONNEXION) && mPreferencesLog.contains(PREF_PSEUDO)){
            Toast.makeText(getApplicationContext(),"session "+ mPreferencesLog.getString(PREF_PSEUDO,null),Toast.LENGTH_LONG).show();
        }*/

        //init 2 fragments with FragmentManager
        if(savedInstanceState == null){
            //give information to the fragmentUser fragment
            ProcessLevel prolevel = new ProcessLevel(mUser);
            Bundle bundleUser = new Bundle();
            bundleUser.putString("pseudo", pseudoUser);
            bundleUser.putInt("age", userAge);
            bundleUser.putInt("score", currentScore);
            bundleUser.putString("grade", prolevel.getUserGrade(mUser.getScore()));
            bundleUser.putByteArray("image", imageData);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                     .add(R.id.fragment_user_container_view, UserFragment.class, bundleUser)
                    .add(R.id.fragment_map_container_view, MapsFragment.class, null)
                    .commit();
        }


        //UserFragment fragU = (UserFragment) getSupportFragmentManager();
        //fragU.setPseudo();


    }
}