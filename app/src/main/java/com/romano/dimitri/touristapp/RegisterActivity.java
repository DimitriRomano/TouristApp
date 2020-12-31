package com.romano.dimitri.touristapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.romano.dimitri.touristapp.model.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText mPseudoInput;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private EditText mAgeInput;

    public static final String TAG = "REGISTER ACTIVITY";

    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //set views
        mPseudoInput = findViewById(R.id.editTextPseudo);
        mEmailInput = findViewById(R.id.editTextEmail);
        mPasswordInput = findViewById(R.id.editTextPassword);
        mAgeInput = findViewById(R.id.editTextAge);
        db=DBHandler.getInstance(this);


    }

    public void register(View view) {
        if(isEmpty(mPseudoInput)){
            mPseudoInput.setError("Pseudo is required");
            mPseudoInput.setHint("Enter Pseudo");
            return;
        }
        else {
            if(db.existPseudo(mPseudoInput.getText().toString())== true) {
                mPseudoInput.setError("This pseudo already exist");
                return;
            }
        }

        if(isEmpty(mEmailInput)){
            mEmailInput.setError("Email is required");
            mEmailInput.setHint("Enter Email address");
            return;
        }else{
            if(db.existEmail(mEmailInput.getText().toString())== true) {
                mEmailInput.setError("This email already exist");
                return;
            }
        }

        if(isEmpty(mPasswordInput)){
            mPasswordInput.setError("Psw is required");
            mPasswordInput.setHint("Enter Password ");
            return;
        }
        String uPseudo = mPseudoInput.getText().toString();
        String uEmail = mEmailInput.getText().toString();
        String uPsw = mPasswordInput.getText().toString();
        int uAge = Integer.parseInt(mAgeInput.getText().toString());
        User u = new User(uPseudo, uEmail, uAge);
        db.addUser(u,uPsw);
        Intent backLog = new Intent();
        backLog.putExtra("result",mPseudoInput.getText().toString());
        setResult(RESULT_OK,backLog);
        finish();
    }


    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}