package com.romano.dimitri.touristapp;

import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.romano.dimitri.touristapp.model.Place;

import java.util.ArrayList;


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
    private Button captionButton;

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
        // retrieve user's information
        mGrade = requireArguments().getString("grade");
        mScore = requireArguments().getInt("score");
        mPseudo = requireArguments().getString("pseudo");
        mEmail=requireArguments().getString("email");
        mAge = requireArguments().getInt("age");
        mImage = requireArguments().getString("image");
        mImageSet=requireArguments().getBoolean("imageSet");
        db = DBHandler.getInstance(this.getContext());
        alreadyVisitedarrayListPlace = db.placeVisitedUser(mPseudo, true);

        mGradeView = view.findViewById(R.id.textViewGrade);
        mPseudoView = view.findViewById(R.id.textViewPseudo);
        mEmailView = view.findViewById(R.id.textViewEmail);
        mScoreView = view.findViewById(R.id.textViewScore);
        mAgeView = view.findViewById(R.id.textViewAge);
        mImageProfile = view.findViewById(R.id.imageUser);

        alreadyVisited=view.findViewById(R.id.alreadyVisitedButton);
        alreadyVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visited(v);
            }
        });

        captionButton=view.findViewById(R.id.captionButton);
        captionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caption(v);
            }
        });

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

    /*
    visited displays the title and coordinates of the places
    visited by a user in a popup window when the button ALREADY VISITED  is clicked
     */
    public void visited(View view){
        Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.popup_places_visited);
        ListView listView = dialog.findViewById(R.id.listview);
        //retrieve a list of places that user has not visited
        alreadyVisitedarrayListPlace=db.placeVisitedUser(mPseudo,true);

        ArrayList<String> itemList=new ArrayList<>();
        for(Place p : alreadyVisitedarrayListPlace){
           String title=p.getTitle();
           Double latitute=p.getLatitude();
           Double longitude=p.getLongitude();
           String newligne=System.getProperty("line.separator");
           String item=title+newligne+"Coord : " +latitute+"|"+longitude;
           System.out.println("test list : "+item);
           itemList.add(item);
        }
        listView.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1,itemList));
        dialog.show();
    }
    /*
    caption displays the map legend in a popup window
     */
    public void caption(View view){
        Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.popup_caption);
        dialog.show();
    }
}