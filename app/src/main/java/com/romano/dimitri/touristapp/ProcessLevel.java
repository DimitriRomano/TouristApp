package com.romano.dimitri.touristapp;

import android.content.Context;

import com.romano.dimitri.touristapp.model.Place;
import com.romano.dimitri.touristapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessLevel {

    private HashMap<String, Integer> type_point, levels;
    private ArrayList<Place> place_al;
    private User user;

    public ProcessLevel(ArrayList<Place> place_al, User user){
        this.place_al = place_al;
        this.user = user;
        type_point();
        level_point();
    }

    /* Initializing every type of locations and how much point is going to be
     * given to the user depending on these types. */
    private void type_point(){
        HashMap<String, Integer> type_point = new HashMap<String, Integer>();
        type_point.put("Museum", 100);
        type_point.put("Monument", 80);
        type_point.put("Church", 70);
        type_point.put("Lake", 30);
        type_point.put("Beach", 60);
        this.type_point = type_point;
    }

    /* Initializing every type of level depending on how much point the user has */
    private void level_point(){
        HashMap<String, Integer> levels = new HashMap<String, Integer>();
        levels.put("Tourist", 0);
        levels.put("Confirmed Tourist", 500);
        levels.put("Traveler", 1500);
        levels.put("Confirmed Traveler", 3000);
        levels.put("Explorator", 6000);
        levels.put("Confirmed Explorator", 7000);
        levels.put("Prospector", 7000);
        levels.put("Lara Croft", 9000);
        levels.put("Indiana Jones", 10000);
        this.levels = levels;
    }

    /* This method is going to check in which location the user is going to be
     *  depending on the place's type, and if the user has either added this place
     * to his "visit" list or if he deleted it.
     * 1 : The user wants to add the location to his "visit" list
     * 0 : The user wants to delete the location from his "visit" list */
    private int givePoint(User user, Place place) {
        int userPoint = user.getScore();
        String locationType = place.getType();

        if(type_point.containsValue(locationType)){
            return userPoint + type_point.get(locationType);
        }
        else{
            return userPoint;
        }
    }


}
