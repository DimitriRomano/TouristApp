package com.romano.dimitri.touristapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class UserFragment extends Fragment {
    private TextView mPseudoView;
    private TextView mScoreView;
    private TextView mGradeView;
    private TextView mAgeView;

    private ProgressBar mScoreProgressBar;
    private ImageView mImageProfile;
    private Drawable mScoreProgressD;

    private String mPseudo;
    private String mGrade;
    private int mAge;
    private int mScore;
    private String mImage;
    private boolean mImageSet;

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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mGrade = requireArguments().getString("grade");
        mScore = requireArguments().getInt("score");
        mPseudo = requireArguments().getString("pseudo");
        mAge = requireArguments().getInt("age");
        mImage = requireArguments().getString("image");
        mImageSet=requireArguments().getBoolean("imageSet");


        mGradeView = view.findViewById(R.id.textViewGrade);
        mPseudoView = view.findViewById(R.id.textViewPseudo);
        mScoreView = view.findViewById(R.id.textViewScore);
        mAgeView = view.findViewById(R.id.textViewAge);
        mImageProfile = view.findViewById(R.id.imageUser);

        mPseudoView.setText(mPseudo);
        mGradeView.setText("Grade: " + mGrade);
        mScoreView.setText(mScore + "/10000 XP");
        mAgeView.setText(mAge + " ans");

        if(mImageSet==true){
            Bitmap bitmap = BitmapFactory.decodeFile(mImage);
            mImageProfile.setImageBitmap(bitmap);
        }
        calculateScore(view);
    }

    public void setScore(){ mPseudoView.setText(mScore);}

    public void calculateScore(View view){
        mScoreView = view.findViewById(R.id.textViewScore);
        int score = mScore;
        System.out.println("Score " + score);
        mScoreProgressBar = view.findViewById(R.id.progressBarScore);
        mScoreProgressBar.setProgress(score);
    }

    //mPseudoView = (TextView)getView().findViewById(R.id.textViewPseudo);
    //mScoreView = getView().findViewById(R.id.textViewScore);

}