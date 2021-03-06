package com.romano.dimitri.touristapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.romano.dimitri.touristapp.model.User;

import static com.romano.dimitri.touristapp.MainActivity.PREF;
import static com.romano.dimitri.touristapp.MainActivity.PREF_CONNEXION;
import static com.romano.dimitri.touristapp.MainActivity.PREF_PSEUDO;

public class DashboardActivity extends AppCompatActivity {

    private SharedPreferences mPreferencesLog;

    private DBHandler mDB;
    private User mUser;

    private int currentScore;
    private int userAge;
    private String pseudoUser;
    private String emailUser;
    private String imageData;
    private boolean imageSet;
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
        emailUser=mUser.getEmail();
        currentScore = mUser.getScore();
        userAge = mUser.getAge();
        imageData = mUser.getImage();
        if(imageData!=null){
            imageSet=true;
        }
        Log.d(TAG, "Pseudo: " + pseudoUser + "; Score: " + currentScore + "; Age: " + userAge +"; Email: " + emailUser);

        //init 2 fragments with FragmentManager
        if(savedInstanceState == null){
            //give information to the fragmentUser fragment
            ProcessLevel prolevel = new ProcessLevel(mUser);
            Bundle bundleUser = new Bundle();
            bundleUser.putString("pseudo", pseudoUser);
            bundleUser.putString("email",emailUser);
            bundleUser.putInt("age", userAge);
            bundleUser.putInt("score", currentScore);
            bundleUser.putString("grade", prolevel.getUserGrade(mUser.getScore()));
            bundleUser.putString("image", imageData);
            bundleUser.putBoolean("imageSet", imageSet);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                     .add(R.id.fragment_user_container_view, UserFragment.class, bundleUser)
                    .add(R.id.fragment_map_container_view, MapsFragment.class, bundleUser)
                    .commit();
        }
    }

    public void logOut(View view){
        //set off auto connexion
        mPreferencesLog.edit().putBoolean(PREF_CONNEXION,false).apply();
        Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}