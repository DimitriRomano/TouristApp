package com.romano.dimitri.touristapp.model;

import java.io.Serializable;
import java.sql.Blob;

public class User implements Serializable {
    private String mPseudo;
    private String mEmail;
    private int mAge;
    private int mScore;
    private String mImage;
    private boolean mImageSet=false;
    public User(){
        //default
    }

    public User(String pseudo, String email, int age, String image) {
        mPseudo = pseudo;
        mEmail = email;
        mAge = age;
        mScore = 0;
        mImage = image;
        mImageSet= true;
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

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public boolean getImageSet() {
        return mImageSet;
    }

    public void setImageSet(boolean mImageSet) {
        this.mImageSet = mImageSet;
    }

    @Override
    public String toString() {
        return "User{" +
                "mPseudo='" + mPseudo + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mAge=" + mAge +
                ", mScore=" + mScore +
                ", mImage='" + mImage + '\'' +
                ", mImageSet=" + mImageSet +
                '}';
    }
}
