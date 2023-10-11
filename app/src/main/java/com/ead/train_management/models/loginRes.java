package com.ead.train_management.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Login response model
 */

public class loginRes {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("nic")
    @Expose
    private String nic;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("role")
    @Expose
    private String role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
