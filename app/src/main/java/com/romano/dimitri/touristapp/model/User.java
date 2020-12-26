package com.romano.dimitri.touristapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private String mPseudo;
    private String mEmail;
    private int mScore;

    public User(){
        //default
    }

    public User(String pseudo, String email) {
        mPseudo = pseudo;
        mEmail = email;
        mScore = 0;
    }

    public User(String pseudo, String email,int score) {
        mPseudo = pseudo;
        mEmail = email;
        mScore = score;
    }

    public String getPseudo() {
        return mPseudo;
    }

    public void setPseudo(String pseudo) {
        mPseudo = pseudo;
    }


    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }
}
