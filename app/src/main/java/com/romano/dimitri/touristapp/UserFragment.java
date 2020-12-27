package com.romano.dimitri.touristapp;

import android.os.Bundle;

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
        mScore = requireArguments().getInt("score");
        mPseudo = requireArguments().getString("pseudo");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mPseudoView = view.findViewById(R.id.textViewPseudo);
        mScoreView = view.findViewById(R.id.textViewScore);
        return view;
    }

    public void setPseudo(){
        mPseudoView.setText(mPseudo);
    }


    //mPseudoView = (TextView)getView().findViewById(R.id.textViewPseudo);
    //mScoreView = getView().findViewById(R.id.textViewScore);



}