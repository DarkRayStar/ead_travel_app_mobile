package com.ead.train_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.ead.train_management.models.viewBookingModel;
import com.ead.train_management.service.BookingService;
import com.ead.train_management.util.DatabaseHelper;
import com.ead.train_management.util.MyAdapter;
import com.ead.train_management.util.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class ViewBookingsActivity extends AppCompatActivity {
    private BookingService bgService;
    private String nic = "";
    private String uid = "";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    @SuppressLint({"NonConstantResourceId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Initialize the bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.view);

        // Set up item selection listener for the bottom navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.book:
                    // Start the CreateBookingActivity
                    startActivity(new Intent(getApplicationContext(), CreateBookingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.home:
                    // Start the UserProfileActivity
                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.view:
                    // Stay on the current activity
                    return true;
            }
            return false;
        });

        // Initialize the BookingService for API calls
        bgService = RetrofitClient.getClient().create(BookingService.class);

        // Initialize the DatabaseHelper and SQLiteDatabase for local database operations
        dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        // Define the projection for retrieving "nic" and "uid" from the local database
        String[] projection = {
                "nic",
                "uid"
        };

        // Perform a database query to get the userModel's "nic" and "uid"
        cursor = db.query(
                "users",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        // Check if the cursor contains data and retrieve "nic" and "uid" if available
        if (cursor.moveToFirst()) {
            nic = cursor.getString(cursor.getColumnIndex("nic"));
            uid = cursor.getString(cursor.getColumnIndex("uid"));
        }

        // Initialize the RecyclerView to display the list of bookings
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Make an API call to retrieve a list of bookings associated with the userModel's "nic"
        Call<List<viewBookingModel>> data = bgService.getBooking(nic);

        data.enqueue(new Callback<List<viewBookingModel>>() {
            @Override
            public void onResponse(Call<List<viewBookingModel>> call1, Response<List<viewBookingModel>> response1) {
                if (response1.isSuccessful() && response1.body() != null) {
                    // Retrieve the list of bookings from the API response
                    List<viewBookingModel> dataList = response1.body();

                    // Remove bookings where "isCc" field is true (if needed)
                    dataList.removeIf(viewBookingModel::isCc);

                    // Format the dates in "yyyy-MM-dd" format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (viewBookingModel booking : dataList) {
                        try {
                            Date parsedDate = dateFormat.parse(booking.getDate());
                            booking.setDate(dateFormat.format(parsedDate));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Create an adapter to populate the RecyclerView with the processed data
                    MyAdapter adapter = new MyAdapter(dataList, ViewBookingsActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    // Display a toast message if no active bookings are available
                    Toast.makeText(ViewBookingsActivity.this, "No Active Bookings Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<viewBookingModel>> call, Throwable t) {
                // Handle API call failure by displaying an error message
                Toast.makeText(ViewBookingsActivity.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
