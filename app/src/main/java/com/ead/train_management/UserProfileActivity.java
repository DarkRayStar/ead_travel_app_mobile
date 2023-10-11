package com.ead.train_management;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ead.train_management.models.disable;
import com.ead.train_management.models.userRes;
import com.ead.train_management.service.LoginService;
import com.ead.train_management.util.DatabaseHelper;
import com.ead.train_management.util.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class UserProfileActivity extends AppCompatActivity {

    private LoginService loginService;
    private String nic = "";
    private String uid = "";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    EditText fname;
    EditText lname;
    EditText phone;
    Button upButton;
    Button rmButton;
    Button lgButton;
    EditText date;

    @SuppressLint({"Range", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.book:
                    startActivity(new Intent(getApplicationContext(), CreateBookingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.home:
                    return true;
                case R.id.view:
                    startActivity(new Intent(getApplicationContext(), ViewBookingsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });


        fname = findViewById(R.id.fname2);
        lname = findViewById(R.id.lname2);
        phone = findViewById(R.id.phone2);
        date = findViewById(R.id.date2);
        upButton = findViewById(R.id.upButton);
        rmButton = findViewById(R.id.rmButton);
        lgButton = findViewById(R.id.lgButton);
        loginService = RetrofitClient.getClient().create(LoginService.class);
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


        Call<userRes> data = loginService.getUserProfile(nic);

        data.enqueue(new Callback<userRes>() {
            @Override
            public void onResponse(Call<userRes> call1, Response<userRes> response1) {

                if (response1.isSuccessful() && response1.body() != null) {

                    userRes res = response1.body();

                    fname.setText(res.getFname());
                    lname.setText(res.getLname());
                    phone.setText(res.getPhone());
                    date.setText(res.getDate());
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<userRes> call, Throwable t) {

                Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fname.getText().toString().equals("") && lname.getText().toString().equals("") && phone.getText().toString().equals("")) {
                    Toast.makeText(UserProfileActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                    builder.setTitle("Confirm Update");
                    builder.setMessage("Are you sure you want to update your profile?");

                    // Add a positive button to confirm updating
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User confirmed, update the profile
                            updateUserProfile();
                        }
                    });

                    // Add a negative button to cancel
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User canceled, do nothing
                        }
                    });

                    // Create and show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


    }

    private void updateUserProfile() {
        userRes u = new userRes();
        u.setAcc(true);
        u.setNic(nic);
        u.setPhone(phone.getText().toString());
        u.setFname(fname.getText().toString());
        u.setLname(lname.getText().toString());
        u.setDate(date.getText().toString());
        u.setId(uid);

        Call<userRes> call = loginService.Update(u);
        call.enqueue(new Callback<userRes>() {
            @Override
            public void onResponse(Call<userRes> call, Response<userRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Profile updated successfully, you can handle this as needed
                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<userRes> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Failed to Update Profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void LogOut(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want to log out?");

        // Add a positive button to confirm logout
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, log out
                logoutUser();
            }
        });

        // Add a negative button to cancel
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled, do nothing
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logoutUser() {
        int deletedRows = db.delete("users", null, null);
        cursor.close();
        dbHelper.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void Disable(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Disable");
        builder.setMessage("Are you sure you want to disable your account?");

        // Add a positive button to confirm disabling
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, disable the account
                disableAccount(view);
            }
        });

        // Add a negative button to cancel
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled, do nothing
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void disableAccount(View view) {
        disable d = new disable();
        d.setAcc(false);
        Call<userRes> data = loginService.Dis(nic, d);

        data.enqueue(new Callback<userRes>() {
            @Override
            public void onResponse(Call<userRes> call1, Response<userRes> response1) {
                if (response1.isSuccessful() && response1.body() != null) {
                    // Account disabled successfully, you can handle this as needed
                    LogOut(view);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<userRes> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}