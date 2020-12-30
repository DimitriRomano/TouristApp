package com.romano.dimitri.touristapp.model;

import java.io.Serializable;

public class Place implements Serializable {
    private int mId;
    private String mTitle;
    private String mType;
    private Double mLatitude;
    private Double mLongitude;
    private String mDescription;
    private boolean mVisited;


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isVisited() {
        return mVisited;
    }

    public void setVisited(boolean visited) {
        mVisited = visited;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public String toString() {
        return "Place{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mType='" + mType + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mDescription='" + mDescription + '\'' +
                ", mVisited=" + mVisited +
                '}';
    }
}
