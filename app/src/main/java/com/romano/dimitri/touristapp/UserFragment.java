package com.romano.dimitri.touristapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.romano.dimitri.touristapp.model.Place;

import java.util.ArrayList;
import java.util.Iterator;


public class UserFragment extends Fragment {
    private DBHandler db;

    private TextView mPseudoView;
    private TextView mScoreView;
    private TextView mGradeView;
    private TextView mAgeView;
    private TextView mEmailView;

    private ProgressBar mScoreProgressBar;
    private ImageView mImageProfile;
    private Drawable mScoreProgressD;

    private String mPseudo;
    private String mEmail;
    private String mGrade;
    private int mAge;
    private int mScore;
    private String mImage;
    private boolean mImageSet;

    private Button alreadyVisited;
    private ArrayList<Place> alreadyVisitedarrayListPlace;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        mGradeView = v.findViewById(R.id.textViewGrade);
        mPseudoView = v.findViewById(R.id.textViewPseudo);
        mEmailView = v.findViewById(R.id.textViewEmail);
        mScoreView = v.findViewById(R.id.textViewScore);
        mAgeView = v.findViewById(R.id.textViewAge);
        mImageProfile = v.findViewById(R.id.imageUser);
        alreadyVisited= v.findViewById(R.id.alreadyVisitedButton);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                calculateScore(view);
                refreshHandler.postDelayed(this, 1 * 1000);
            }
        };
        refreshHandler.postDelayed(runnable, 1 * 1000);

        mGrade = requireArguments().getString("grade");
        mScore = requireArguments().getInt("score");
        mPseudo = requireArguments().getString("pseudo");
        mEmail=requireArguments().getString("email");
        mAge = requireArguments().getInt("age");
        mImage = requireArguments().getString("image");
        mImageSet=requireArguments().getBoolean("imageSet");
        db = DBHandler.getInstance(this.getContext());
        alreadyVisitedarrayListPlace = db.placeVisitedUser(mPseudo, true);

        mPseudoView.setText(mPseudo);
        mEmailView.setText(mEmail);
        mGradeView.setText("Grade: " + mGrade);
        mScoreView.setText(mScore + "/10000 XP");
        mAgeView.setText(mAge + " ans");

        if(mImageSet==true){
            Bitmap bitmap = BitmapFactory.decodeFile(mImage);
            mImageProfile.setImageBitmap(bitmap);
        }
        calculateScore(view);
    }

    public void setScore(){
        int newScore = db.getUser(mPseudo).getScore();
        mScore = newScore;
        mScoreView.setText(mScore + "/10000 XP");
    }

    public void calculateScore(View view){
        mScore = db.getUser(mPseudo).getScore();
        mScoreView = view.findViewById(R.id.textViewScore);
        mScoreProgressBar = view.findViewById(R.id.progressBarScore);
        mScoreView.setText(mScore + "/10000 XP");
        mScoreProgressBar.setProgress(mScore);
    }
}