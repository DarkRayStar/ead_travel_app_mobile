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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ead.train_management.models.disableAccountModel;
import com.ead.train_management.models.userResponseModel;
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
    EditText firstName;
    EditText lastName;
    EditText phone;
    ImageButton updateButton;
    ImageButton disableButton;
    ImageButton logoutButton;
    EditText date;

    @SuppressLint({"Range", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

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

        firstName = findViewById(R.id.updateFirstName);
        lastName = findViewById(R.id.updateLastName);
        phone = findViewById(R.id.updatePhone);
        date = findViewById(R.id.date2);
        updateButton = findViewById(R.id.updateButton);
        disableButton = findViewById(R.id.removeButton);
        logoutButton = findViewById(R.id.logoutButton);
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

        Call<userResponseModel> data = loginService.getUserProfile(nic);

        data.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<userResponseModel> call1, Response<userResponseModel> response1) {
                if (response1.isSuccessful() && response1.body() != null) {
                    userResponseModel res = response1.body();
                    firstName.setText(res.getFirstName());
                    lastName.setText(res.getLastName());
                    phone.setText(res.getPhone());
                    date.setText(res.getDate());
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<userResponseModel> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstName.getText().toString().equals("") && lastName.getText().toString().equals("") && phone.getText().toString().equals("")) {
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
        userResponseModel u = new userResponseModel();
        u.setAcc(true);
        u.setNic(nic);
        u.setPhone(phone.getText().toString());
        u.setFirstName(firstName.getText().toString());
        u.setLastName(lastName.getText().toString());
        u.setDate(date.getText().toString());
        u.setId(uid);

        Call<userResponseModel> call = loginService.updateProfile(u);
        call.enqueue(new Callback<userResponseModel>() {
            @Override
            public void onResponse(Call<userResponseModel> call, Response<userResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Profile updated successfully, you can handle this as needed
                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<userResponseModel> call, Throwable t) {
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
        Intent intent = new Intent(this, LoginActivity.class);
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
        disableAccountModel d = new disableAccountModel();
        d.setAcc(false);
        Call<userResponseModel> data = loginService.disableAccount(nic, d);

        data.enqueue(new Callback<userResponseModel>() {
            @Override
            public void onResponse(Call<userResponseModel> call1, Response<userResponseModel> response1) {
                if (response1.isSuccessful() && response1.body() != null) {
                    // Account disabled successfully, you can handle this as needed
                    int deletedRows = db.delete("users", null, null);
                    cursor.close();
                    dbHelper.close();
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(UserProfileActivity.this, "Account Disabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<userResponseModel> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
