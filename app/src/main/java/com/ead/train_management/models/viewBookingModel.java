package com.ead.train_management.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class viewBookingModel {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("travallerName")
    @Expose
    private String name;

    @SerializedName("noOfPassenger")
    @Expose
    private int num;

    @SerializedName("emailAddress")
    @Expose
    private String email;

    @SerializedName("bookedTrain")
    @Expose
    private BookedTrain bookedTrain;

    @SerializedName("reservationDate")
    @Expose
    private String date;

    @SerializedName("isCancelled")
    @Expose
    private boolean isCancelled;

    @SerializedName("isCc")
    @Expose
    private boolean isVisible;

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

    public BookedTrain getBookedTrain() {
        return bookedTrain;
    }

    public void setBookedTrain(BookedTrain bookedTrain) {
        this.bookedTrain = bookedTrain;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean visible) {
        this.isVisible = visible;
    }

    // Inner class to represent the booked train details
    public static class BookedTrain {
        @SerializedName("trainName")
        @Expose
        private String trainName;

        public String getTrainName() {
            return trainName;
        }

        public void setTrainName(String trainName) {
            this.trainName = trainName;
        }
    }
}
