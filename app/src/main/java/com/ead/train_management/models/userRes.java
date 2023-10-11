package com.ead.train_management.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * User response model
 */

public class userRes {
     @SerializedName("id")
     @Expose
     private String id;

     @SerializedName("nic")
     @Expose
     private String nic;

     @SerializedName("firstName")
     @Expose
     private String fname;

     @SerializedName("lastName")
     @Expose
     private String lname;

     @SerializedName("phoneNumber")
     @Expose
     private String phone;

     @SerializedName("accStatus")
     @Expose
     private boolean acc;

     @SerializedName("createdDate")
     @Expose
     private String date;



     public String getDate() {
          return date;
     }

     public void setDate(String date) {
          this.date = date;
     }

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
}
