package com.romano.dimitri.touristapp;

import android.content.Context;

import com.romano.dimitri.touristapp.model.Place;
import com.romano.dimitri.touristapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessLevel {

    private HashMap<String, Integer> type_point;
    private HashMap<Integer, String> grades;
    private ArrayList<Place> place_al;
    private int[] levels;
    private User user;

    public ProcessLevel(User user){
        this.user = user;
        type_point();
        level_point();
    }

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
        HashMap<Integer, String> grades = new HashMap<Integer, String>();
        int[] levels = {0, 500, 1500, 3000, 6000, 7000, 8000, 9000, 10000};
        grades.put(1, "Tourist");
        grades.put(2, "Confirmed Tourist");
        grades.put(3, "Traveler");
        grades.put(4, "Confirmed Traveler");
        grades.put(5, "Explorator");
        grades.put(6, "Confirmed Explorator");
        grades.put(7, "Prospector");
        grades.put(8, "Lara Croft");
        grades.put(9, "Indiana Jones");
        this.grades = grades;
        this.levels = levels;
    }

    public String getUserGrade(int mPoints){
        int nearest = 0;
        int index = 1;
        System.out.println("Your points: " + mPoints);
        System.out.println(levels.length);
        for (int i=0; i< this.levels.length; i++){
            System.out.println(levels[i]);
            if(mPoints > levels[i]){
                nearest = levels[i];
                System.out.println("Nearest: " + nearest);
                index = i;
            }
        }
        System.out.println("Index " + index + "; Nearest " + grades.get(index));
        return grades.get(index);
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

    private void progressBarProcess(){

    }
}
