package uk.ac.gre.mf9669w.m_expense.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "M-Expenses.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table trips(id integer primary key,name_of_place text,destination text,date_of_trip text,risk_assessment boolean,description text,latitude text,longitude text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users;");
        onCreate(db);
    }

    public boolean insertTrip(Trip trip) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("name_of_place", trip.getNameOfPlace());
        values.put("destination", trip.getDestination());
        values.put("date_of_trip", trip.getDateOfTrip());
        values.put("risk_assessment", trip.isRiskAssessment());
        values.put("description", trip.getDescription());
        values.put("latitude", trip.getLatitude());
        values.put("longitude", trip.getLongitude());
        db.insert("trips", null, values);
        db.close();
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("select * from trips where id=" + id + ";", null);
    }

    public int numberOfRows() {
        SQLiteDatabase database = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(database, "trips");
    }

    public boolean updateTrip(Trip trip) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name_of_place", trip.getNameOfPlace());
        values.put("destination", trip.getDestination());
        values.put("date_of_trip", trip.getDateOfTrip());
        values.put("risk_assessment", trip.isRiskAssessment());
        values.put("description", trip.getDescription());
        values.put("latitude", trip.getLatitude());
        values.put("longitude", trip.getLongitude());
        database.update("trips", values, "id=?", new String[]{Integer.toString(trip.getId())});
        database.close();
        return true;
    }

    public Integer deleteUser(Integer id) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete("users", "id=?", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public List<Trip> getTrips() {
        List<Trip> users = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        try (Cursor response = database.rawQuery("select * from trips;", null)) {
            response.moveToFirst();
            while (!response.isAfterLast()) {
                users.add((Trip) response);
                response.moveToNext();
            }
        }
        database.close();
        return users;
    }

    public boolean deleteAllData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from trips");
        database.close();
        return true;
    }
}

