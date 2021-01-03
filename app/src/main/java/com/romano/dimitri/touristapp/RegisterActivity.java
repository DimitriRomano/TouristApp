package com.romano.dimitri.touristapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.romano.dimitri.touristapp.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mPseudoInput;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private EditText mAgeInput;
    private ImageView mImageInput;
    private String imgPath;
    private boolean imageSet = false;
    private Uri imageUri;
    private Button uploadButton;

    public static final String TAG = "REGISTER ACTIVITY";
    public static final int PERMISSIONS_REQUEST = 0;
    public static final int PICK_IMAGE = 1;


    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = DBHandler.getInstance(this);
        Log.i("Register Activity", "Entered");
        //check permission

        //permission already granted
        //set views
        mPseudoInput = findViewById(R.id.editTextPseudo);
        mEmailInput = findViewById(R.id.editTextEmail);
        mPasswordInput = findViewById(R.id.editTextPassword);
        mAgeInput = findViewById(R.id.editTextAge);
        mImageInput = findViewById(R.id.uploadedImage);
        uploadButton=findViewById(R.id.Upload);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mImageInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload(v);
                }
            });

        }
        else{
            //request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            String[] filePath ={MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(imageUri,filePath,null,null,null);
            cursor.moveToFirst();
            int column =cursor.getColumnIndex(filePath[0]);
            imgPath=cursor.getString(column);
            System.out.println("imgPath : "+imgPath);
            cursor.close();
            Bitmap mImage = BitmapFactory.decodeFile(imgPath);
            mImageInput.setImageBitmap(mImage);
            imageSet=true;
        }
    }

    public void upload(View view){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);

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

        if(isEmpty(mAgeInput)){
            mAgeInput.setError("Your age is required");
            mAgeInput.setHint("Please enter your age");
            return;
        }

        String uPseudo = mPseudoInput.getText().toString();
        String uEmail = mEmailInput.getText().toString();
        String uPsw = mPasswordInput.getText().toString();
        int uAge = Integer.parseInt(mAgeInput.getText().toString());
        User u;
        System.out.println("RE"+imageSet);
        if(imageSet==true){
            u=new User(uPseudo,uEmail,uAge,imgPath);
        }
        else {
            u = new User(uPseudo, uEmail, uAge);
        }
        db.addUser(u,uPsw);
        Intent backLog = new Intent();
        backLog.putExtra("result",mPseudoInput.getText().toString());
        setResult(RESULT_OK,backLog);
        finish();
    }


    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    @Override
    public void onRequestPermissionsResult(int request, String permissions[], int[] results) {
        switch (request) {
            case PERMISSIONS_REQUEST: {

                // If request is cancelled, the result arrays are empty
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay! Do something useful
                    Toast.makeText(this, "Permission granted to access device's storage", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission was denied, boo! Disable the
                    // functionality that depends on this permission
                    uploadButton.setEnabled(false);
                    Toast.makeText(this, "Permission denied to access device's storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onClick(View v) {

    }
}