package com.ead.train_management.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Login model
 */

public class login {
    @SerializedName("Nic")
    @Expose
    private String nic;

    @SerializedName("Password")
    @Expose
    private String password;

    public login() {
    }

    public login(String nic, String password) {
        this.nic = nic;
        this.password = password;
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
}
