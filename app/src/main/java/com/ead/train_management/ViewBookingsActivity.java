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

import com.ead.train_management.models.viewBooking;
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.view);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.book:
                    startActivity(new Intent(getApplicationContext(), CreateBookingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.view:
                    return true;
            }
            return false;
        });

        bgService = RetrofitClient.getClient().create(BookingService.class);
        dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        String[] projection = {
                "nic",
                "uid"
        };

        cursor = db.query(
                "users",
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            nic = cursor.getString(cursor.getColumnIndex("nic"));
            uid = cursor.getString(cursor.getColumnIndex("uid"));

        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Call<List<viewBooking>> data = bgService.getBooking(nic);

        data.enqueue(new Callback<List<viewBooking>>() {
            @Override
            public void onResponse(Call<List<viewBooking>> call1, Response<List<viewBooking>> response1) {

                if (response1.isSuccessful() && response1.body() != null) {

                    List<viewBooking> dataList = response1.body();
                    dataList.removeIf(viewBooking::isCc);

                    // Format the dates as yyyy-MM-dd
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (viewBooking booking : dataList) {
                        try {
                            Date parsedDate = dateFormat.parse(booking.getDate());
                            booking.setDate(dateFormat.format(parsedDate));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    MyAdapter adapter = new MyAdapter(dataList, ViewBookingsActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ViewBookingsActivity.this, "No Active Bookings Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<viewBooking>> call, Throwable t) {
                Toast.makeText(ViewBookingsActivity.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
