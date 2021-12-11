package uk.ac.gre.mf9669w.m_expense.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.ac.gre.mf9669w.m_expense.models.Expense;
import uk.ac.gre.mf9669w.m_expense.models.Trip;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";
    public static final String DATABASE_NAME = "M-Expenses.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("" +
                "create table trips(" +
                "id integer primary key autoincrement," +
                "name_of_place text," +
                "destination text," +
                "date_of_trip text," +
                "risk_assessment boolean," +
                "description text," +
                "start_time text," +
                "end_time text);");
        db.execSQL("" +
                "create table expenses(" +
                "id integer primary key autoincrement," +
                "trip_id integer not null," +
                "expense_name text," +
                "expense_price text," +
                "expense_date text," +
                "foreign key(trip_id) references trips(id)" +
                ");");
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
        values.put("start_time", trip.getStartTime());
        values.put("end_time", trip.getEndTime());
        db.insert("trips", null, values);
        db.close();
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("select * from trips where id=" + id + ";", null);
    }

    public int numberOfRowsInTrips() {
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
        values.put("start_time", trip.getStartTime());
        values.put("end_time", trip.getEndTime());
        database.update("trips", values, "id=?", new String[]{Integer.toString(trip.getId())});
        database.close();
        return true;
    }

    public boolean updateExpense(Expense expense) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trip_id", expense.getExpenseOwner());
        values.put("expense_name", expense.getTypeOfExpense());
        values.put("expense_price", expense.getAmountOfExpense());
        values.put("expense_date", expense.getTimeOfExpense());
        database.update("expenses", values, "id=?", new String[]{Integer.toString(expense.getId())});
        return true;
    }

    public boolean addExpense(Expense expense) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trip_id", expense.getExpenseOwner());
        values.put("expense_name", expense.getTypeOfExpense());
        values.put("expense_price", expense.getAmountOfExpense());
        values.put("expense_date", expense.getTimeOfExpense());
        database.insert("expenses", null, values);
        database.close();
        return true;
    }

    @SuppressLint("Range")
    public List<Expense> getTripExpenses(int id) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        try (Cursor cursor = database.rawQuery("select * from expenses where trip_id=" + id + ";", null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                expenses.add(new Expense(
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("id"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("trip_id"))),
                        cursor.getString(cursor.getColumnIndex("expense_name")),
                        cursor.getString(cursor.getColumnIndex("expense_price")),
                        cursor.getString(cursor.getColumnIndex("expense_date")))
                );
                Log.i(TAG, "getTripExpenses: trip id ->" + cursor.getString(cursor.getColumnIndex("trip_id")));
                cursor.moveToNext();
            }
        }
        database.close();
        return expenses;
    }

    public Integer deleteUser(Integer id) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete("trips", "id=?", new String[]{Integer.toString(id)});
    }

    public Integer deleteExpense(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("expenses", "id=?", new String[]{Integer.toString(id)});
    }

    @SuppressLint("Range")
    public List<Trip> getTrips() {
        List<Trip> users = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        try (Cursor response = database.rawQuery("select * from trips;", null)) {
            response.moveToFirst();
            while (!response.isAfterLast()) {
                boolean risk;
                if (response.getString(response.getColumnIndex("risk_assessment")).equals("1")){
                    risk=true;
                }else {
                    risk=false;
                }
                users.add(new Trip(
                        Integer.parseInt(response.getString(response.getColumnIndex("id"))),
                        response.getString(response.getColumnIndex("name_of_place")),
                        response.getString(response.getColumnIndex("destination")),
                        response.getString(response.getColumnIndex("date_of_trip")),
                        response.getString(response.getColumnIndex("description")),
                        risk,
                        response.getString(response.getColumnIndex("start_time")),
                        response.getString(response.getColumnIndex("end_time"))));
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

    public boolean deleteAllExpense() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from expenses;");
        database.close();
        database.close();
        return true;
    }


}

