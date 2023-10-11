package com.ead.train_management;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ead.train_management.models.booking;
import com.ead.train_management.models.train;
import com.ead.train_management.service.BookingService;
import com.ead.train_management.util.DatabaseHelper;
import com.ead.train_management.util.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateBookingActivity extends AppCompatActivity {

    private BookingService bookingService;
    private String nic = "";
    private String uid = "";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    EditText name;
    EditText email;
    EditText num;
    EditText date;
    EditText phone;
    Button addButton;
    ImageView infoButton;
    Spinner spinner;
    private int year, month, day, hour, minute;
    static final int DATE_DIALOG_ID = 999;

    String selectedTrainId = "";

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize the DatePicker values
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.book);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.book:
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.view:
                    startActivity(new Intent(getApplicationContext(), ViewBookingsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        name = findViewById(R.id.name3);
        spinner = findViewById(R.id.spinner);
        phone = findViewById(R.id.phone3);
        date = findViewById(R.id.date3);
        email = findViewById(R.id.email3);
        num = findViewById(R.id.num3);
        addButton = findViewById(R.id.addButton);
        infoButton = findViewById(R.id.infoButton);
        bookingService = RetrofitClient.getClient().create(BookingService.class);
        dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        String[] projection = {
                "nic",
                "uid"
        };

        Cursor cursor = db.query(
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

        Call<List<train>> data = bookingService.getTrain();

        data.enqueue(new Callback<List<train>>() {
            @Override
            public void onResponse(Call<List<train>> call1, Response<List<train>> response1) {
                if (response1.isSuccessful() && response1.body() != null) {
                    List<train> responseData = response1.body();
                    List<String> dt = new ArrayList<>();
                    List<String> trainNames = new ArrayList<>();

                    for (train d : responseData) {
                        dt.add(d.getTidc());
                        trainNames.add(d.getTrainName());
                    }
                    populateSpinner(trainNames, dt);
                } else {
                    Toast.makeText(CreateBookingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<train>> call, Throwable t) {
                Toast.makeText(CreateBookingActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionsAlert();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("") && email.getText().toString().equals("") && phone.getText().toString().equals("")) {
                    Toast.makeText(CreateBookingActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedValue = "";
                    if (spinner.getSelectedItem() != null) {
                        selectedValue = spinner.getSelectedItem().toString();
                    }

                    // Extract only the date portion from the reservation date
                    String reservationDate = date.getText().toString().split("T")[0];

                    // Get the number of tickets from the 'num' EditText
                    String numberOfTickets = num.getText().toString();

                    // Create the confirmation message with date and number of tickets
                    String confirmationMessage = "Train Name: " + selectedValue +
                            "\nDate: " + reservationDate +
                            "\nNumber of Tickets: " + numberOfTickets;

                    // Create a confirmation dialog with the message
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateBookingActivity.this);
                    builder.setTitle("Confirm Booking");
                    builder.setMessage(confirmationMessage);

                    // Add a positive button to confirm booking
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User confirmed, create the booking
                            createBooking();
                        }
                    });

                    // Add a negative button to cancel
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

        // Set an OnClickListener for the date EditText
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    // Function to display the DatePicker dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this, datePickerListener, year, month, day);
        }
        return null;
    }

    // Listener for date picker dialog
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // Display the selected date in the EditText
            String dateTime = String.format("%04d-%02d-%02dT%02d:%02d", year, month + 1, day, hour, minute);
            date.setText(dateTime);
        }
    };

    private void populateSpinner(List<String> trainNames, final List<String> dt) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When a train name is selected, store the corresponding train id (dt) in a variable
                selectedTrainId = dt.get(position);
                // You can use selectedTrainId as needed for form submission
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (if necessary)
            }
        });
    }

    private void showActionsAlert() {
        // Create a concise alert message
        String alertMessage = "a. Create new reservations (reservation date within 30 days from booking date, maximum 4 reservations per reference ID).\n\n" +
                "b. Update reservations (at least 5 days before the reservation date).\n\n" +
                "c. Cancel reservations (at least 5 days before the reservation date).";

        // Create an AlertDialog with the alert message
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateBookingActivity.this);
        builder.setTitle("Booking Policy");
        builder.setMessage(alertMessage);

        // Add an "OK" button to dismiss the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, just close the dialog
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createBooking() {
        String selectedValue = "";
        if (spinner.getSelectedItem() != null) {
            selectedValue = spinner.getSelectedItem().toString();
        }

        booking u = new booking();
        u.setRfid(nic);
        u.setTid(uid);
        u.setStatus(false);
        u.setTrain(selectedTrainId);
        u.setDate(date.getText().toString());
        u.setPhone(phone.getText().toString());
        u.setEmail(email.getText().toString());
        u.setName(name.getText().toString());
        u.setPassno(Integer.parseInt(num.getText().toString()));

        Call<String> call = bookingService.createBooking(u);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response1) {
                Intent intent = new Intent(getApplicationContext(), ViewBookingsActivity.class);
                startActivity(intent);
                Toast.makeText(CreateBookingActivity.this, "Booking Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CreateBookingActivity.this, "Booking Failed, Limit May Exceeded", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
