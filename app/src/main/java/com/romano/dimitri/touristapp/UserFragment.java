package com.romano.dimitri.touristapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class UserFragment extends Fragment {
    private TextView mPseudoView;
    private TextView mScoreView;
    private TextView mGradeView;
    private TextView mAgeView;

    private String mPseudo;
    private String mGrade;
    private int mAge;
    private int mScore;

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

        mGradeView = view.findViewById(R.id.textViewGrade);
        mPseudoView = view.findViewById(R.id.textViewPseudo);
        mScoreView = view.findViewById(R.id.textViewScore);
        mAgeView = view.findViewById(R.id.textViewAge);

        mPseudoView.setText(mPseudo);
        mGradeView.setText(mGrade);
        mScoreView.setText(mScore + "/10000 XP");
        mAgeView.setText(mAge + " ans");
    }

    public void setScore(){ mPseudoView.setText(mScore);}


    //mPseudoView = (TextView)getView().findViewById(R.id.textViewPseudo);
    //mScoreView = getView().findViewById(R.id.textViewScore);



}