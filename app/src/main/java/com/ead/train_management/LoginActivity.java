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

import com.ead.train_management.models.login;
import com.ead.train_management.models.loginRes;
import com.ead.train_management.models.userRes;
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

        loginButton.setOnClickListener(view -> {
            if (username.getText().toString().equals("") && password.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
            } else {
                login loginRequest = new login();
                loginRequest.setNic(username.getText().toString());
                loginRequest.setPassword(password.getText().toString());

                Call<loginRes> call = lgService.Login(loginRequest);
                call.enqueue(new Callback<loginRes>() {
                    @Override
                    public void onResponse(Call<loginRes> call, Response<loginRes> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            loginRes userResponse = response.body();
                            if (userResponse.getRole().equals("traveler")) {
                                Call<userRes> data = lgService.getUserProfile(userResponse.getNic());

                                data.enqueue(new Callback<userRes>() {
                                    @Override
                                    public void onResponse(Call<userRes> call1, Response<userRes> response1) {

                                        if (response1.isSuccessful() && response1.body() != null) {

                                            userRes res = response1.body();

                                            if (res.isAcc()) {

                                                ContentValues values = new ContentValues();
                                                values.put("nic", userResponse.getNic());
                                                values.put("uid", res.getId());
                                                long newRowId = db.insert("users", null, values);

                                                if (newRowId != -1) {
                                                    Intent intent = new Intent(getApplicationContext(), CreateBookingActivity.class);
                                                    startActivity(intent);
                                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {
                                                Toast.makeText(LoginActivity.this, "Account is Disabled or Network Error", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<userRes> call, Throwable t) {

                                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong Account Type", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(LoginActivity.this, "Account is Disabled or Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<loginRes> call, Throwable t) {

                        Toast.makeText(LoginActivity.this, "Failed to log", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    public void navigateToReg(View view) {
        Intent intent = new Intent(this, UserRegisterActivity.class);
        startActivity(intent);
    }

}