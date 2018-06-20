package com.vaibhav.fifafixtures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

class MyDatabase {
    private static final String DB_NAME = "FIFA_FIXTURES";
    private static final int DB_VERSION=1;
    private static final String TABLE_NAME="MATCH_LIST";
    private static final String C_ID="ID";
    private static final String C_GRP="GRP";
    private static final String C_TEAM1="TEAM1";
    private static final String C_TEAM2="TEAM2";
    private static final String C_DATE="DATE";
    private static final String C_TIME="TIME";
    private static final String C_VENUE="VENUE";
    private static final String C_LOGO1="LOGO1";
    private static final String C_LOGO2="LOGO2";
    private String[] allColumns={C_ID,C_GRP,C_TEAM1,C_TEAM2,C_DATE,C_TIME,C_VENUE,C_LOGO1,C_LOGO2};

    private static final String CREATE_DB= "CREATE TABLE " + TABLE_NAME + "( " + C_ID + " INTEGER PRIMARY KEY, " + C_GRP + " TEXT, " + C_TEAM1 + " TEXT, "
            + C_TEAM2 + " TEXT, " +C_DATE + " TEXT, " + C_TIME + " TEXT, " + C_VENUE + " TEXT, " + C_LOGO1 + " Blob, " + C_LOGO2 + " Blob)";

    private SQLiteDatabase myDatabase;
    private final Context myContext;
    private dbHelper myHelper;

    public MyDatabase (Context c){
        myContext=c;
    }

    public class dbHelper extends SQLiteOpenHelper{

        public dbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    public MyDatabase open() {
        myHelper= new dbHelper(myContext);
        myDatabase=myHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        myHelper.close();
    }

    public void createEntry (matchinfo m){
        ContentValues contentValues=new ContentValues();
        contentValues.put(C_ID,m.place);
        contentValues.put(C_GRP,m.groupname);
        contentValues.put(C_TEAM1,m.team1);
        contentValues.put(C_TEAM2,m.team2);
        contentValues.put(C_DATE,m.cdate);
        contentValues.put(C_TIME,m.ctime);
        contentValues.put(C_VENUE,m.cvenue);
        contentValues.put(C_LOGO1,m.logo1);
        contentValues.put(C_LOGO2,m.logo2);
        myDatabase.insert(TABLE_NAME,null,contentValues);
        Log.e("dbCollect","entrycreated");
    }


    public ArrayList<matchinfo> getData() {
        ArrayList<matchinfo> list = new ArrayList<>();
        Cursor c = myDatabase.query(TABLE_NAME, allColumns, null, null, null, null, null);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            matchinfo mrow = convertData(c);
            list.add(mrow);
        }
        c.close();
        Log.e("dbCollect","getdata done");
        return list;
    }

    private matchinfo convertData(Cursor cursor) {
        return new matchinfo(cursor.getBlob(7),cursor.getBlob(8), cursor.getString(1),cursor.getString(2),cursor.getString(3),
                cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getInt(0));
    }

    public void removeRow(matchinfo m) {
        int args = m.place;
        myDatabase.delete(TABLE_NAME, C_ID + " = " + args, null);
    }
}
