package com.romano.dimitri.touristapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private String mPseudo;
    private String mEmail;
    private int mAge;
    private int mScore;

    public User(){
        //default
    }

    public User(String pseudo, String email, int age) {
        mPseudo = pseudo;
        mEmail = email;
        mAge = age;
        mScore = 0;
    }

    public User(String pseudo, String email, int age, int score) {
        mPseudo = pseudo;
        mEmail = email;
        mScore = score;
        mAge = age;
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

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }
}
