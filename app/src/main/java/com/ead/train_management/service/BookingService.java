package com.ead.train_management.service;

import com.ead.train_management.models.bookingModel;
import com.ead.train_management.models.trainModel;
import com.ead.train_management.models.viewBookingModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Booking service
 */
public interface BookingService {

    @GET("api/ReservationManagement/{id}")
    Call<List<viewBookingModel>> getBooking(@Path("id") String nic);

    @POST("api/ReservationManagement")
    Call<String> createBooking(@Body bookingModel u);

    @DELETE("api/ReservationManagement/{id}")
    Call<String> removeBooking(@Path("id") String id );

    @GET("api/TrainManagement")
    Call<List<trainModel>> getTrain();

}


