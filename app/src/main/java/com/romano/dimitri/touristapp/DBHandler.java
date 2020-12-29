package com.romano.dimitri.touristapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.Edits;
import android.util.Log;

import com.romano.dimitri.touristapp.model.Place;
import com.romano.dimitri.touristapp.model.User;

import java.util.ArrayList;
import java.util.Iterator;

public class DBHandler extends SQLiteOpenHelper {

    //name of database
    public static final String DB_NAME = "TouristApp";

    //version
    public static final int DB_VERSION = 1;

    //table names
    public static final String TABLE_USER="USER";
    public static final String TABLE_PLACE="PLACE";

    //columns names user Table
    private static final String COL_PSEUDO ="PSEUDO";
    private static final String COL_EMAIL ="EMAIL";
    private static final String COL_SCORE ="SCORE";
    private static final String COL_PASSWORD ="PASSWORD";

    //columns names place Table
    private static final String COL_ID ="ID";
    private static final String COL_TITLE ="TITLE";
    private static final String COL_TYPE ="TYPE";
    private static final String COL_LATITUDE ="LATITUDE";
    private static final String COL_LONGITUDE ="LONGITUDE";
    private static final String COL_DESCRIPTION ="DESCRIPTION";

    //create user table
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" +
            COL_PSEUDO + " TEXT PRIMARY KEY, " + COL_EMAIL + " TEXT, " + COL_SCORE + " INTEGER, " + COL_PASSWORD + " TEXT " + ")" ;

    //create place table
    private static final String CREATE_TABLE_PLACE = "CREATE TABLE " + TABLE_PLACE + "(" +
            COL_ID + " INTEGER PRIMARY KEY, " + COL_TITLE + " TEXT, " + COL_TYPE + " TEXT, " + COL_LATITUDE + " DOUBLE, " +
            COL_LONGITUDE + " DOUBLE, " + COL_DESCRIPTION +"TEXT " +")" ;
    //singleton pattern
    private static DBHandler sInstance;

    //prevent to direct instantiation
    private DBHandler(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_PLACE);

        ArrayList<String> places=new ArrayList<String>();
        places=addPlaces();
        Iterator iter=places.iterator();
        while(iter.hasNext()){
            db.execSQL((String) iter.next());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE + ";");
        onCreate(db);
    }

    public static synchronized DBHandler getInstance(Context context){
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if(sInstance == null){
            sInstance = new DBHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public void addUser(User user, String psw){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHandler.COL_PSEUDO,user.getPseudo());
        cv.put(DBHandler.COL_EMAIL,user.getEmail());
        cv.put(DBHandler.COL_SCORE,user.getScore());
        cv.put(DBHandler.COL_PASSWORD,psw);

        db.insert(TABLE_USER,null, cv);
        db.close();

    }

    public boolean verifLog(String pseudo, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean verifReturn = false;
        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + COL_PSEUDO + " = '" + pseudo +"' ";


        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            String rMdp = cursor.getString(3);
            if(rMdp.equals(password)){
                verifReturn = true;
            }
        }
        cursor.close();
        db.close();

        return verifReturn;
    }

    public boolean existPseudo(String pseudo){
        Boolean retVal = false;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COL_PSEUDO + " FROM " + TABLE_USER + " WHERE " + COL_PSEUDO + " = '" + pseudo +"' ";


        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            retVal = true;
        }
        cursor.close();
        db.close();
        return retVal;
    }

    public boolean existEmail(String email){
        Boolean retVal = false;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COL_EMAIL + " FROM " + TABLE_USER + " WHERE " + COL_EMAIL + " = '" + email +"' ";


        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.getCount()>0){
            retVal = true;
        }
        cursor.close();
        db.close();
        return retVal;
    }

    //return User from a pseudo ( map activity)
    public User getUser(String pseudo){
        SQLiteDatabase db=this.getReadableDatabase();
        User u = new User();

        String selectQuery = "SELECT " + COL_PSEUDO + " FROM " + TABLE_USER + " WHERE " + COL_PSEUDO + " = '" + pseudo +"' ";
        Cursor cursor = db.query(TABLE_USER,new String[]{COL_PSEUDO,COL_EMAIL,COL_SCORE},COL_PSEUDO + " =  ? "  ,new String[]{pseudo},null,null,null);
        cursor.moveToFirst();
        u.setPseudo(cursor.getString(0));
        u.setEmail(cursor.getString(1));
        u.setScore(cursor.getInt(2));
        cursor.close();
        db.close();
        return u;
    }
/*inutile surement
    public void addPlace(Place place){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHandler.COL_TITLE,place.getTitle());
        cv.put(DBHandler.COL_TYPE,place.getType());
        cv.put(DBHandler.COL_LATITUDE,place.getLatitude());
        cv.put(DBHandler.COL_LONGITUDE,place.getLongitude());
        cv.put(DBHandler.COL_DESCRIPTION,place.getDescription());
        db.insert(TABLE_PLACE,null, cv);
        db.close();

    }
*/
    public Place getPlace(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Place place = new Place();

        String selectQuery = "SELECT " + COL_ID + " FROM " + TABLE_PLACE + " WHERE " + COL_ID + " = '" + id +"' ";
        Cursor cursor = db.query(TABLE_PLACE,new String[]{COL_ID,COL_TITLE,COL_TYPE,COL_LATITUDE,COL_LONGITUDE,COL_DESCRIPTION},COL_ID + " =  ? "  ,new String[]{String.valueOf(id)},null,null,null);
        cursor.moveToFirst();
        place.setId(cursor.getInt(0));
        place.setTitle(cursor.getString(1));
        place.setType(cursor.getString(2));
        place.setLatitude(cursor.getDouble(3));
        place.setLongitude(cursor.getDouble(4));
        place.setDescription(cursor.getString(5));
        cursor.close();
        db.close();
        return place;
    }

    public ArrayList<String> addPlaces(){
        ArrayList<String> places=new ArrayList<>();
        String request;
        request="INSERT INTO PLACE(" + COL_TITLE + "," + COL_TYPE +"," + COL_LATITUDE + "," + COL_LONGITUDE + "," + COL_DESCRIPTION +") VALUES" +
                "('Cathédrale Notre-Dame de Paris','Eglise', 48.852968 , 2.349902, 'Cathédrale colossale du XIIIe siècle ornée d'arcs-boutants et de gargouilles, lieu du roman de Victor Hugo.');";
        places.add(request);
        return places;
    }

}
