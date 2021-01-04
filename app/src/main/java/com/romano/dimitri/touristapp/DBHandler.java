package com.romano.dimitri.touristapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.Edits;
import android.util.Log;

import com.romano.dimitri.touristapp.model.Place;
import com.romano.dimitri.touristapp.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {


    //name of database
    public static final String DB_NAME = "TouristApp";

    //version
    public static final int DB_VERSION = 1;

    //table names
    public static final String TABLE_USER="USER";
    public static final String TABLE_PLACE="PLACE";
    public static final String TABLE_VISITED="VISITED";

    //columns names user Table
    private static final String COL_PSEUDO ="PSEUDO";
    private static final String COL_EMAIL ="EMAIL";
    private static final String COL_SCORE ="SCORE";
    private static final String COL_PASSWORD ="PASSWORD";
    private static final String COL_AGE = "AGE";
    private static final String COL_IMAGE ="IMAGE";
    //create table
    private static final String CREATE_BD = "CREATE TABLE " + TABLE_USER + "(" +
            COL_PSEUDO + " TEXT PRIMARY KEY, " + COL_EMAIL + " TEXT, " + COL_SCORE + " INTEGER, " + COL_PASSWORD + " TEXT " +
            ", " + COL_AGE + " INTEGER, " + COL_IMAGE + " TEXT NULL )" ;

    //columns names place Table
    private static final String COL_ID ="ID";
    private static final String COL_TITLE ="TITLE";
    private static final String COL_TYPE ="TYPE";
    private static final String COL_LATITUDE ="LATITUDE";
    private static final String COL_LONGITUDE ="LONGITUDE";
    private static final String COL_DESCRIPTION ="DESCRIPTION";

    //columns names visited Table
    private static final String COL_ID_VISITED="ID";
    private static final String COL_PSEUDO_VISITED="PSEUDO";

    //create user table
    private static final String CREATE_TABLE_USER = new StringBuilder().append("CREATE TABLE ").append(TABLE_USER).append("(").append(COL_PSEUDO).append(" TEXT PRIMARY KEY, ").append(COL_EMAIL).append(" TEXT, ").append(COL_SCORE).append(" INTEGER, ").append(COL_PASSWORD).append(" TEXT ").append(", ").append(COL_AGE).append(" INTEGER, ").append(COL_IMAGE).append(" BLOB NULL)").toString();

    //create place table
    private static final String CREATE_TABLE_PLACE = "CREATE TABLE " + TABLE_PLACE + "(" +
            COL_ID + " INTEGER PRIMARY KEY, " + COL_TITLE + " TEXT UNIQUE , " + COL_TYPE + " TEXT, " + COL_LATITUDE + " TEXT, " +
            COL_LONGITUDE + " TEXT, " + COL_DESCRIPTION +" TEXT " +")" ;
    //create visited table
    private static final String CREATE_TABLE_VISITED="CREATE TABLE " +TABLE_VISITED + "(" +
            COL_ID_VISITED + " INTEGER, " + COL_PSEUDO_VISITED + " TEXT, " +
            "PRIMARY KEY ("+COL_ID_VISITED +" , "+ COL_PSEUDO_VISITED +") " +
            "FOREIGN KEY (" + COL_ID_VISITED + ") REFERENCES " + TABLE_PLACE + " (" + COL_ID + "), " +
            "FOREIGN KEY (" + COL_PSEUDO_VISITED + ") REFERENCES " + TABLE_USER + " (" + COL_PSEUDO +") )" ;

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
        db.execSQL(CREATE_TABLE_VISITED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITED +";");
        onCreate(db);
    }

    /*
        getInstance is a singleton which permits us to only have one instanciation of our DBHandler class
        usable in every other class.
        @param context  Context of our activity
        @return DBHandler   Return the instanciation of our DBHandler class.
    */
    public static synchronized DBHandler getInstance(Context context){
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if(sInstance == null){
            sInstance = new DBHandler(context.getApplicationContext());
            ArrayList<Place> places=new ArrayList<>();
            places=sInstance.getPlacesFile(context);
            Iterator<Place> iter=places.iterator();
            while(iter.hasNext()){
                sInstance.addPlaceDB(iter.next());
            }
        }
        return sInstance;
    }

    /*
        addUser add the user account to the database depending on the information he gave us.
        @param user       The user object which contains every single information about our user account.
        @param psw        The password associated to the user account
    */
    public void addUser(User user, String psw){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHandler.COL_PSEUDO,user.getPseudo());
        cv.put(DBHandler.COL_EMAIL,user.getEmail());
        cv.put(DBHandler.COL_SCORE,user.getScore());
        cv.put(DBHandler.COL_PASSWORD,psw);
        cv.put(DBHandler.COL_AGE,user.getAge());
        cv.put(DBHandler.COL_IMAGE,user.getImage());

        db.insert(TABLE_USER,null, cv);
        db.close();


    }

    /*
       updateUser update the user account to the database depending on the information he gave us.
       @param userPseudo       The user pseudo which will be replaced by the actual one.
       @param userEmail        The user email which will be replaced by the actual one.
       @param userScore        The user score which will be replaced by the actual one.
       @param userPassword     The user password which will be replaced by the actual one.
       @param userAge          The user age which will be replaced by the actual one.
       @param userImage        The user image which will be replaced by the actual one.
   */
    public void updateUser(User user){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHandler.COL_PSEUDO,user.getPseudo());
        cv.put(DBHandler.COL_EMAIL,user.getEmail());
        cv.put(DBHandler.COL_SCORE,user.getScore());
        cv.put(DBHandler.COL_AGE,user.getAge());
        cv.put(DBHandler.COL_IMAGE,user.getImage());

        db.update(TABLE_USER,cv, COL_PSEUDO + "='" + user.getPseudo() + "'", null);
        db.close();


    }

    /*
        verifLog checks if the login informations are correct when the user wants to connect.
        @param pseudo     The pseudo of our user
        @param password   The password associated to the user account
        @return retVal Returns if the login infos exists and are a match in the database (table User) or not.
    */
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

    /*
     existPseudo checks if an username (pseudo) exists in our database, then return a boolean.
     @param pseudo   The pseudo of our user
     @return retVal Returns if the pseudo (username) exist in the database (table User) or not.
    */
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

    /*
        existEmail checks if an email exists in our database, then return a boolean.
        @param email   The email of our user
        @return retVal Returns if the mail exist in the database (table User) or not.
     */
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

    /*
        getUser allow us to recover the data of a user depending on his username (pseudo) from our
        database, then creating a User object with this data.
        @param pseudo   The name of our user
        @return u       The instanciation of the user filled with the data recovered
     */
    public User getUser(String pseudo){
        SQLiteDatabase db=this.getReadableDatabase();
        User u = new User();

        String selectQuery = "SELECT " + COL_PSEUDO + " FROM " + TABLE_USER + " WHERE " + COL_PSEUDO + " = '" + pseudo +"' ";
        Cursor cursor = db.query(TABLE_USER,new String[]{COL_PSEUDO,COL_EMAIL,COL_SCORE, COL_AGE, COL_IMAGE},COL_PSEUDO + " =  ? "  ,new String[]{pseudo},null,null,null);
        cursor.moveToFirst();
        u.setPseudo(cursor.getString(0));
        u.setEmail(cursor.getString(1));
        u.setScore(cursor.getInt(2));
        u.setAge(cursor.getInt(3));
        u.setImage(cursor.getString(4));
        cursor.close();
        db.close();
        return u;
    }

    /*
        addPlaceDB allow us to add places into our table "Place" in our database SQLite following the content of
        one of our Place object.
        @param place    Our place object, which contains all our data about a place.
     */
    public void addPlaceDB(Place place){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHandler.COL_TITLE,place.getTitle());
        cv.put(DBHandler.COL_TYPE,place.getType());
        cv.put(DBHandler.COL_LATITUDE,Double.toString(place.getLatitude()));
        cv.put(DBHandler.COL_LONGITUDE,Double.toString(place.getLongitude()));
        cv.put(DBHandler.COL_DESCRIPTION,place.getDescription());
        db.insert(TABLE_PLACE,null, cv);
        db.close();

    }

    /*
        getPlacesFile allow us to recover all the data from our raw file "places.txt" to create our objects
        so we can look forward to store it into the database.
        @param context  The context of our activity
        @return places  All the places recovered from the database into an ArrayList of Places.
     */
    public ArrayList<Place> getPlacesFile(Context context){
        ArrayList<Place> places=new ArrayList<>();
        String delimiter=";";


        InputStream inputStream = context.getResources().openRawResource(R.raw.places);
        try{
            if(inputStream!=null){
                BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
                String str;
                while((str=br.readLine())!=null){
                    Place place=new Place();
                    String parts[]=str.split(delimiter);
                    place.setTitle(parts[0]);
                    place.setType(parts[1]);
                    place.setLatitude(Double.parseDouble(parts[2]));
                    place.setLongitude(Double.parseDouble(parts[3]));
                    place.setDescription(parts[4]);
                    places.add(place);
                }
                br.close();
                return places;
            }
            else{
                return null;
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /*
        getAllRows allow us to recover the data of the existant places in database.
        @return place   All the rows from the table "Place" recovered from the database
                        into a List of Place.
    */
    public ArrayList<Place> getAllRows(){
        ArrayList<Place> place= new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PLACE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all the rows and addi the to the list
        if (cursor.moveToFirst()) {
            do {
                Place p=new Place();
                p.setId(cursor.getInt(0));
                p.setTitle(cursor.getString(1));
                p.setType(cursor.getString(2));
                p.setLatitude(cursor.getDouble(3));
                p.setLongitude(cursor.getDouble(4));
                p.setDescription(cursor.getString(5));

                // Add row to list
                place.add(p);
            } while (cursor.moveToNext());
        }
        else{
            System.out.println("echec :(");
        }
        cursor.close();
        db.close();

        // Return the list
        return place;
    }

    /*
        placeVisitedUser allow us to recover the data of the places the user visited and didn't visited.
        @param pseudo           The name of the user
        @param selectVisited    Boolean to let us know which data we want to recover
        Status of selectVisited :
            • false : Data of the places the user didn't visited
            • true : Data of the places the user visited
        @return place   All the places visited or non-visited into an ArrayList of Place.
     */
    public ArrayList<Place> placeVisitedUser(String pseudo, boolean selectVisited){
        ArrayList<Place> place= new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(selectVisited){
            selectQuery = "SELECT " + COL_ID + ", " + COL_TITLE + "," + COL_TYPE + ", " + COL_LATITUDE
                    + ", " + COL_LONGITUDE + "," + COL_DESCRIPTION +" FROM " + TABLE_VISITED + " NATURAL JOIN " + TABLE_PLACE + " WHERE " + TABLE_VISITED + "." + COL_PSEUDO_VISITED + " = '"
                    + pseudo + "' ";
        }
        else{
            selectQuery = "SELECT " + COL_ID +", " + COL_TITLE + ", " + COL_TYPE + ", " + COL_LATITUDE + ", " + COL_LONGITUDE + ", " + COL_DESCRIPTION +
                    " FROM " + TABLE_PLACE + " EXCEPT " + "SELECT " + COL_ID +", " + COL_TITLE + ", " + COL_TYPE + ", "
                    + COL_LATITUDE + ", " + COL_LONGITUDE + ", " + COL_DESCRIPTION +  " FROM " + TABLE_VISITED + " NATURAL JOIN " + TABLE_PLACE + " WHERE "
                    + TABLE_VISITED + "." + COL_PSEUDO_VISITED + " = '" + pseudo +"' ";
        }


        Cursor cursor = db.rawQuery(selectQuery, null);
        // Loop through all the rows and addi the to the list
        if (cursor.moveToFirst()) {
            do {
                Place p=new Place();
                p.setId(cursor.getInt(0));
                p.setTitle(cursor.getString(1));
                p.setType(cursor.getString(2));
                p.setLatitude(cursor.getDouble(3));
                p.setLongitude(cursor.getDouble(4));
                p.setDescription(cursor.getString(5));
                place.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return place;
    }

    /*
        addVisit allows us to add a row into the "Visited" table with
        the username and the place visited.
        @param pseudo           The name of the user
        @param place            The place we want to add to the database
     */
    public void addVisit(String pseudo, int idPlace){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHandler.COL_ID_VISITED,idPlace);
        cv.put(DBHandler.COL_PSEUDO_VISITED,pseudo);
        db.insert(TABLE_VISITED,null, cv);
        db.close();
    }
}
