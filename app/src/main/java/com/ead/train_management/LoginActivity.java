package com.ead.train_management;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ead.train_management.models.loginModel;
import com.ead.train_management.models.loginResponseModel;
import com.ead.train_management.models.userResponseModel;
import com.ead.train_management.service.LoginService;
import com.ead.train_management.util.DatabaseHelper;
import com.ead.train_management.util.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private LoginService lgService;
    EditText username;
    EditText password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        lgService = RetrofitClient.getClient().create(LoginService.class);
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Set an OnClickListener for the loginButton
        loginButton.setOnClickListener(view -> {
            if (username.getText().toString().equals("") && password.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
            } else {
                loginModel loginModelRequest = new loginModel();
                loginModelRequest.setNic(username.getText().toString());
                loginModelRequest.setPassword(password.getText().toString());

                // Make a loginModel API call
                Call<loginResponseModel> call = lgService.Login(loginModelRequest);
                call.enqueue(new Callback<loginResponseModel>() {
                    @Override
                    public void onResponse(Call<loginResponseModel> call, Response<loginResponseModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            loginResponseModel userResponse = response.body();
                            if (userResponse.getRole().equals("traveler")) {
                                // If the userModel is a traveler, fetch their profile data
                                Call<userResponseModel> data = lgService.getUserProfile(userResponse.getNic());

                                data.enqueue(new Callback<userResponseModel>() {
                                    @Override
                                    public void onResponse(Call<userResponseModel> call1, Response<userResponseModel> response1) {
                                        if (response1.isSuccessful() && response1.body() != null) {
                                            userResponseModel res = response1.body();
                                            if (res.isAcc()) {
                                                // If the account is active, insert userModel data into the local database
                                                ContentValues values = new ContentValues();
                                                values.put("nic", userResponse.getNic());
                                                values.put("uid", res.getId());
                                                long newRowId = db.insert("users", null, values);

                                                if (newRowId != -1) {
                                                    // Navigate to the CreateBookingActivity
                                                    Intent intent = new Intent(getApplicationContext(), CreateBookingActivity.class);
                                                    startActivity(intent);
                                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Account is Disabled or Invalid Credentials", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<userResponseModel> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Account Type", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Account is disabled, contact Back-Office", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<loginResponseModel> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Failed to log", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Function to navigate to the UserRegisterActivity
    public void navigateToReg(View view) {
        Intent intent = new Intent(this, UserRegisterActivity.class);
        startActivity(intent);
    }
}
