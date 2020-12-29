package com.romano.dimitri.touristapp;

import android.content.Context;

import com.romano.dimitri.touristapp.model.Place;
import com.romano.dimitri.touristapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessLevel {

    private HashMap<String, Integer> type_point;
    private ArrayList<Place> place_al;
    private User user;

    public ProcessLevel(ArrayList<Place> place_al, User user){
        this.place_al = place_al;
        this.user = user;
        type_point();
    }

    /* Initializing every type of locations and how much point is going to be
    * given to the user depending on these types. */
    private void type_point(){
        HashMap<String, Integer> type_point = new HashMap<String, Integer>();
        type_point.put("Musée", 100);
        type_point.put("Monument", 80);
        type_point.put("Église", 70);
        type_point.put("Lac", 30);
        type_point.put("Plage", 60);
        this.type_point = type_point;
    }

    /* This method is going to check in which location the user is going to be
    *  depending on the place's type, and if the user has either added this place
    * to his "visit" list or if he deleted it.
    * 1 : The user wants to add the location to his "visit" list
    * 0 : The user wants to delete the location from his "visit" list */
    private int givePoint(User user, Place place, Boolean give_or_substract) {
        int userPoint = user.getScore();
        String locationType = place.getType();

        if (give_or_substract) {
            if (locationType.contains("Musée")) {
                return userPoint + type_point.get("Musée");
            } else if (locationType.contains("Monument")) {
                return userPoint + type_point.get("Monument");
            } else if (locationType.contains("Église")) {
                return userPoint + type_point.get("Église");
            } else if (locationType.contains("Lac")) {
                return userPoint + type_point.get("Lac");
            } else if (locationType.contains("Plage")) {
                return userPoint + type_point.get("Plage");
            } else {
                return userPoint;
            }
        }
        else{
            if(locationType.contains("Musée")){
                return userPoint - type_point.get("Musée");
            }
            else if(locationType.contains("Monument")){
                return userPoint - type_point.get("Monument");
            }
            else if(locationType.contains("Église")){
                return userPoint - type_point.get("Église");
            }
            else if(locationType.contains("Lac")){
                return userPoint - type_point.get("Lac");
            }
            else if(locationType.contains("Plage")){
                return userPoint - type_point.get("Plage");
            }
            else{
                return userPoint;
            }
        }
    }


}
