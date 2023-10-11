package com.ead.train_management.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class viewBooking {


    @SerializedName("id")
    @Expose
    private String  id;
    @SerializedName("travallerName")
    @Expose
    private String  name;

    @SerializedName("noOfPassenger")
    @Expose
    private int  num;

    @SerializedName("emailAddress")
    @Expose
    private String  email;

    @SerializedName("reservationDate")
    @Expose
    private String  date;

    public boolean isCc() {
        return cc;
    }

    public void setCc(boolean cc) {
        this.cc = cc;
    }

    @SerializedName("isCancelled")
    @Expose
    private boolean  cc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
