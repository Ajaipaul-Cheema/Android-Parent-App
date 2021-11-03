package ca.cmpt276.as3.parentapp.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.as3.parentapp.data.Child;
import ca.cmpt276.as3.parentapp.data.FlipRecord;

public class DBOpenHelper  extends SQLiteOpenHelper{
    private SQLiteDatabase db;

    public DBOpenHelper(Context context){
        super(context,"db_test",null,1);
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS children(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "childname TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS fliprecords(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "flipno TEXT," +
                "childname TEXT," +
                "result TEXT," +
                "time TEXT," +
                "iswin TEXT)");

        initDataBase(db);
    }

    private void initDataBase (SQLiteDatabase db) {
        ContentValues values1 = new ContentValues();
        values1.put("childname","Blanche");
        db.insert("children", null, values1);

        ContentValues values2 = new ContentValues();
        values2.put("childname","Bonnie");
        db.insert("children", null, values2);

        ContentValues values3 = new ContentValues();
        values3.put("childname","Betty");
        db.insert("children", null, values3);

        ContentValues values4 = new ContentValues();
        values4.put("childname","Ariel");
        db.insert("children", null, values4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }

    @SuppressLint("Range")
    public List<Child> getAllChild(){
        List<Child> list = new ArrayList<>();
        Cursor cursor = db.query("children",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Child data = new Child();
            data.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            data.setChildname(cursor.getString(cursor.getColumnIndex("childname")));
            list.add(data);
        }
        return list;
    }

    @SuppressLint("Range")
    public List<FlipRecord> getAllRecord(){
        List<FlipRecord> list = new ArrayList<>();
        Cursor cursor = db.query("fliprecords",null,null,null,null,null,"time desc");
        while (cursor.moveToNext()){
            FlipRecord data = new FlipRecord();
            data.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            data.setChildname(cursor.getString(cursor.getColumnIndex("childname")));
            data.setFlipno(cursor.getString(cursor.getColumnIndex("flipno")));
            data.setResult(cursor.getString(cursor.getColumnIndex("result")));
            data.setTime(cursor.getString(cursor.getColumnIndex("time")));
            data.setIswin(cursor.getString(cursor.getColumnIndex("iswin")));
            list.add(data);
        }
        return list;
    }

    public boolean addRecord(FlipRecord data){
        ContentValues values = new ContentValues();
        values.put("flipno",data.getFlipno());
        values.put("childname",data.getChildname());
        values.put("result",data.getResult());
        values.put("time",data.getTime());
        values.put("iswin",data.getIswin());
        return db.insert("fliprecords", null, values) > 0;
    }
}
