package com.ead.train_management.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class train {
    @SerializedName("id")
    @Expose
    private String tidc;

    private String trainName;

    public String getTidc() {
        return tidc;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTidc(String tidc) {
        this.tidc = tidc;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
}
