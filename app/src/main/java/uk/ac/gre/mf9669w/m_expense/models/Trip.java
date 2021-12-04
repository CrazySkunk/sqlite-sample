package uk.ac.gre.mf9669w.m_expense.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Trip implements Parcelable {
    private int id;
    private String nameOfPlace,destination,dateOfTrip,description;
    private boolean riskAssessment;
    private double latitude,longitude;

    public Trip(int id, String nameOfPlace, String destination, String dateOfTrip, String description, boolean riskAssessment, double latitude, double longitude) {
        this.id = id;
        this.nameOfPlace = nameOfPlace;
        this.destination = destination;
        this.dateOfTrip = dateOfTrip;
        this.description = description;
        this.riskAssessment = riskAssessment;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Trip(Parcel in) {
        id = in.readInt();
        nameOfPlace = in.readString();
        destination = in.readString();
        dateOfTrip = in.readString();
        description = in.readString();
        riskAssessment = in.readByte() != 0;
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfPlace() {
        return nameOfPlace;
    }

    public void setNameOfPlace(String nameOfPlace) {
        this.nameOfPlace = nameOfPlace;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDateOfTrip() {
        return dateOfTrip;
    }

    public void setDateOfTrip(String dateOfTrip) {
        this.dateOfTrip = dateOfTrip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(boolean riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nameOfPlace);
        dest.writeString(destination);
        dest.writeString(dateOfTrip);
        dest.writeString(description);
        dest.writeByte((byte) (riskAssessment ? 1 : 0));
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
