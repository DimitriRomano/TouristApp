package com.romano.dimitri.touristapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import static com.romano.dimitri.touristapp.MainActivity.PREF;
import static com.romano.dimitri.touristapp.MainActivity.PREF_CONNEXION;
import static com.romano.dimitri.touristapp.MainActivity.PREF_PSEUDO;

public class MapActivity extends AppCompatActivity {

    private SharedPreferences mPreferencesLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPreferencesLog = getSharedPreferences(PREF,MODE_PRIVATE);
        if(mPreferencesLog.contains(PREF_CONNEXION) && mPreferencesLog.contains(PREF_PSEUDO)){
            Toast.makeText(getApplicationContext(),"session "+ mPreferencesLog.getString(PREF_PSEUDO,null),Toast.LENGTH_LONG).show();
            //mPreferencesLog.edit().putBoolean(PREF_CONNEXION,false).commit();
        }


    }
}