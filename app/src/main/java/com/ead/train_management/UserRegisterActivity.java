package com.ead.train_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ead.train_management.models.user;
import com.ead.train_management.models.userRes;
import com.ead.train_management.service.LoginService;
import com.ead.train_management.util.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterActivity extends AppCompatActivity {

    private LoginService lgService;
    EditText nic;
    EditText password;
    EditText fname;
    EditText lname;
    EditText phone;
    Button regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nic = findViewById(R.id.nic1);
        password = findViewById(R.id.password1);
        fname = findViewById(R.id.fname1);
        lname = findViewById(R.id.lname1);
        phone = findViewById(R.id.phone1);
        regButton = findViewById(R.id.regButton);
        lgService = RetrofitClient.getClient().create(LoginService.class);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nic.getText().toString().equals("") && password.getText().toString().equals("") && fname.getText().toString().equals("") && lname.getText().toString().equals("") && phone.getText().toString().equals("")) {
                    Toast.makeText(UserRegisterActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                } else {

                    user u = new user();
                    user.UserInfo ui = u.new UserInfo();
                    u.setAcc(true);
                    u.setNic(nic.getText().toString());
                    u.setPhone(phone.getText().toString());
                    u.setFname(fname.getText().toString());
                    u.setLname(lname.getText().toString());
                    ui.setPassword(password.getText().toString());
                    ui.setRole("traveler");
                    u.setData(ui);
                    Call<userRes> call = lgService.Reg(u);
                    call.enqueue(new Callback<userRes>() {
                        @Override
                        public void onResponse(Call<userRes> call, Response<userRes> response) {

                            if (response.isSuccessful() && response.body() != null) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(UserRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(UserRegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<userRes> call, Throwable t) {

                            Toast.makeText(UserRegisterActivity.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}