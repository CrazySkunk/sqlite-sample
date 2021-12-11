package uk.ac.gre.mf9669w.m_expense.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Trip implements Parcelable {
    private int id;
    private String nameOfPlace,destination,dateOfTrip,description,startTime,endTime;
    private boolean riskAssessment=true;

    public Trip(int id, String nameOfPlace, String destination, String dateOfTrip, String description, boolean riskAssessment, String startTime, String endTime) {
        this.id = id;
        this.nameOfPlace = nameOfPlace;
        this.destination = destination;
        this.dateOfTrip = dateOfTrip;
        this.description = description;
        this.riskAssessment = riskAssessment;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected Trip(Parcel in) {
        id = in.readInt();
        nameOfPlace = in.readString();
        destination = in.readString();
        dateOfTrip = in.readString();
        description = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        riskAssessment = in.readByte() != 0;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(boolean riskAssessment) {
        this.riskAssessment = riskAssessment;
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
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeByte((byte) (riskAssessment ? 1 : 0));
    }
}
