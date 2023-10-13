package com.ead.train_management.service;

import com.ead.train_management.models.disableAccountModel;
import com.ead.train_management.models.loginModel;
import com.ead.train_management.models.loginResponseModel;
import com.ead.train_management.models.userModel;
import com.ead.train_management.models.userResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * User and AUth services
 */

public interface LoginService {

    @POST("UserManagement/")
    Call<loginResponseModel> Login(@Body loginModel lg);

    @GET("api/TravelerManagement/{id}")
    Call<userResponseModel> getUserProfile(@Path("id") String nic);

    @POST("api/TravelerManagement/")
    Call<userResponseModel> Reg(@Body userModel u);

    @POST("api/TravelerManagement/")
    Call<userResponseModel> Update(@Body userResponseModel u);

    @PUT("api/TravelerManagement/{id}")
    Call<userResponseModel> Dis(@Path("id") String nic , @Body disableAccountModel db);

}



