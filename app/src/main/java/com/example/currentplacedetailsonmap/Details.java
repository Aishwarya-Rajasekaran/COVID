package com.example.currentplacedetailsonmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Time;

public class Details implements Parcelable {
String place;
Double lat;
Double lon;
Integer no_devices;
String time;

    protected Details(Parcel in) {
        place = in.readString();
        time = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lon = null;
        } else {
            lon = in.readDouble();
        }
        if (in.readByte() == 0) {
            no_devices = null;
        } else {
            no_devices = in.readInt();
        }
    }

    public static final Creator<Details> CREATOR = new Creator<Details>() {
        @Override
        public Details createFromParcel(Parcel in) {
            return new Details(in);
        }

        @Override
        public Details[] newArray(int size) {
            return new Details[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.place);
        dest.writeString(this.time);
        dest.writeInt(this.no_devices);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
    }



        public Details()
        {}

    public Details(String place, Double lat, Double lon, Integer no_devices,String time) {
        this.place = place;
        this.lat = lat;
        this.lon = lon;
        this.no_devices = no_devices;
        this.time=time;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getNo_devices() {
        return no_devices;
    }

    public void setNo_devices(Integer no_devices) {
        this.no_devices = no_devices;
    }


}
