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

    private String mPseudo;
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
        mScore = requireArguments().getInt("score");
        mPseudo = requireArguments().getString("pseudo");

        mPseudoView = view.findViewById(R.id.textViewPseudo);
        mScoreView = view.findViewById(R.id.textViewScore);

        mPseudoView.setText(mPseudo);
        mScoreView.setText(String.valueOf(mScore));
    }

    public void setScore(){ mPseudoView.setText(mScore);}


    //mPseudoView = (TextView)getView().findViewById(R.id.textViewPseudo);
    //mScoreView = getView().findViewById(R.id.textViewScore);



}