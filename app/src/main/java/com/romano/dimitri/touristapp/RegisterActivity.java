package com.romano.dimitri.touristapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.romano.dimitri.touristapp.model.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mPseudoInput;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private EditText mAgeInput;
    private ImageView mImageInput;
    private String imgPath;
    //private byte[] imageData;
    private boolean imageSet = false;

    private Button upload;

    public static final String TAG = "REGISTER ACTIVITY";
    public static final int PICK_IMAGE = 1;
    private Uri imageUri;

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
        mImageInput = findViewById(R.id.uploadedImage);
        mImageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                upload(v);
            }
        });
        db=DBHandler.getInstance(this);
        Log.i("Register Activity", "Entered");
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
    public void onClick(View v) {

    }
}