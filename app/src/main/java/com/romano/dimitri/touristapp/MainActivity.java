package com.romano.dimitri.touristapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    private EditText mLoginInput;
    private EditText mPassWorldInput;

    public static final String TAG = "MAIN ACTIVITY";
    public static final int REGISTER_ACTIVITY_REQUEST_CODE = 30;

    private SharedPreferences mPreferencesLog;
    public static final String PREF = "PREFS_LOG";
    public static final String PREF_CONNEXION = "PREFS_CONNECTION";
    public static final String PREF_PSEUDO = "PREFS_PSEUDO";

    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginInput =findViewById(R.id.inputPseudo);
        mPassWorldInput = findViewById(R.id.inputPassword);

        mPreferencesLog = getApplicationContext().getSharedPreferences(PREF,MODE_PRIVATE);
        if(mPreferencesLog.contains(PREF_CONNEXION) && mPreferencesLog.contains(PREF_PSEUDO)){
            boolean isActive = mPreferencesLog.getBoolean(PREF_CONNEXION,false);
            String isPseudo = mPreferencesLog.getString(PREF_PSEUDO,null);
            if(isActive == true){
                //if PREF_CONNEXION = true = session is already active then we log on pseudo associate
                Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }else{
                //else just put the last session pseudo on inputText
                mLoginInput.setText(isPseudo);
            }
        }

        db = DBHandler.getInstance(this);

        Button registerBtn = findViewById(R.id.activity_main_register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(MainActivity.this,RegisterActivity.class);
                startActivityForResult(registerActivity,REGISTER_ACTIVITY_REQUEST_CODE);
            }
        });

        Button loginBtn = findViewById(R.id.activity_main_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegisterActivity.isEmpty(mLoginInput)){
                    mLoginInput.setError("No login entered");
                    mLoginInput.setHint("Enter pseudo pls");
                    return;
                }else{
                    if(db.existPseudo(mLoginInput.getText().toString())== false) {
                        mLoginInput.setError("This pseudo doesn't exist");
                        return;
                    }
                }
                if(RegisterActivity.isEmpty(mPassWorldInput)){
                    mPassWorldInput.setError("No Password entered");
                    mPassWorldInput.setHint("Enter a password pls");
                    return;
                }
                boolean LogUser=db.verifLog(mLoginInput.getText().toString(),mPassWorldInput.getText().toString());
                if(LogUser==false){
                    Toast.makeText(getApplicationContext(),"Login and password don't match",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"You sucessfully connected! Welcome " + mLoginInput.getText().toString(),Toast.LENGTH_LONG).show();
                    mPreferencesLog.edit().putBoolean(PREF_CONNEXION,true).putString(PREF_PSEUDO,mLoginInput.getText().toString()).commit();
                    Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REGISTER_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                //get Pseudo for intent;
                String resultPseudo = data.getStringExtra("result");
                Toast.makeText(getApplicationContext(),resultPseudo + " Successful register",Toast.LENGTH_LONG).show();
                mPreferencesLog.edit().putBoolean(PREF_CONNEXION,true).putString(PREF_PSEUDO,resultPseudo).commit();
                //log on the session
                Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        }
    }
}