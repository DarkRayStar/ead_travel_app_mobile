package com.ead.train_management.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * User model
 */

public class user {

    @SerializedName("Nic")
    @Expose
    private String nic;

    @SerializedName("FirstName")
    @Expose
    private String fname;

    @SerializedName("LastName")
    @Expose
    private String lname;

    @SerializedName("PhoneNumber")
    @Expose
    private String phone;

    @SerializedName("AccStatus")
    @Expose
    private boolean acc;
    
    @SerializedName("UserInfo")
    public UserInfo data = null;

    public class UserInfo {
        @SerializedName("Password")
        @Expose
        private String password;

        @SerializedName("Role")
        @Expose
        private String role;

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

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAcc() {
        return acc;
    }

    public void setAcc(boolean acc) {
        this.acc = acc;
    }

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
