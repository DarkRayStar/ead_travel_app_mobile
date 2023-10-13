package com.ead.train_management;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ead.train_management.service.LoginService;
import com.ead.train_management.util.RetrofitClient;

public class UserRegisterActivity extends AppCompatActivity {

    private LoginService lgService;
    EditText nic;
    EditText password;
    EditText firstName;
    EditText lastName;
    EditText phone;
    Button regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        nic = findViewById(R.id.clientNIC);
        password = findViewById(R.id.password);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phone = findViewById(R.id.phoneNumber);
        regButton = findViewById(R.id.registerButton);
        lgService = RetrofitClient.getClient().create(LoginService.class);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nic.getText().toString().equals("") && password.getText().toString().equals("")
                        && firstName.getText().toString().equals("") && lastName.getText().toString().equals("")
                        && phone.getText().toString().equals("")) {
                    showAlertDialog("Fill all details", "Please complete all fields.");
                } else if (!isPasswordValid(password.getText().toString())) {
                    showPasswordAlertDialog("Password Validation Error", "Password must be longer " +
                            "than 8 digits and combined with at least one UPPERCASE, LOWERCASE, " +
                            "SPECIAL character, and a number");
                } else {
                    // Your registration logic here
                    // ...
                }
            }
        });
    }

    private boolean isPasswordValid(String password) {
        // Password should be at least 8 characters long with a combination of lowercase, uppercase, number and at least one special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }

    private void showPasswordAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
