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

import com.ead.train_management.models.bookingModel;
import com.ead.train_management.models.trainModel;
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
    private SQLiteDatabase dbObj;
    EditText clientName;
    EditText email;
    EditText noOfTickets;
    EditText date;
    EditText phone;
    Button createBookingButton;
    ImageView infoButton;
    Spinner trainDropdown;
    private int year, month, day, hour, minute;
    static final int DATE_DIALOG_ID = 999;

    String selectedTrainId = "";
    String selectedTrain = "Select the train";

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

        // Initialize and set up the bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.book);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Handle bottom navigation item selection
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

// Initialize UI elements
        clientName = findViewById(R.id.clientName);
        trainDropdown = findViewById(R.id.spinner);
        phone = findViewById(R.id.phoneNumber);
        date = findViewById(R.id.bookingDate);
        email = findViewById(R.id.clientEmail);
        noOfTickets = findViewById(R.id.numberOfTickets);
        createBookingButton = findViewById(R.id.createBooking);
        infoButton = findViewById(R.id.infoButton);
        bookingService = RetrofitClient.getClient().create(BookingService.class);
        dbHelper = new DatabaseHelper(getApplicationContext());
        dbObj = dbHelper.getWritableDatabase();

// Define the projection for database query
        String[] projection = {
                "nic",
                "uid"
        };

// Query the database to fetch userModel data
        Cursor cursor = dbObj.query(
                "users",
                projection,
                null,
                null,
                null,
                null,
                null
        );

// Check if cursor contains data and extract userModel information
        if (cursor.moveToFirst()) {
            nic = cursor.getString(cursor.getColumnIndex("nic"));
            uid = cursor.getString(cursor.getColumnIndex("uid"));
        }

// Create a list of train names, including "Select the train" as the first entry
        List<String> trainNames = new ArrayList<>();
        trainNames.add("Select the train");

// Make an API call to fetch the list of available trains
        Call<List<trainModel>> data = bookingService.getTrain();

        data.enqueue(new Callback<List<trainModel>>() {
            @Override
            public void onResponse(Call<List<trainModel>> call1, Response<List<trainModel>> response1) {
                if (response1.isSuccessful() && response1.body() != null) {
                    List<trainModel> responseData = response1.body();
                    List<String> dt = new ArrayList<>();

                    for (trainModel d : responseData) {
                        dt.add(d.getTidc());
                        trainNames.add(d.getTrainName());
                    }
                    populateSpinner(trainNames, dt);
                } else {
                    Toast.makeText(CreateBookingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<trainModel>> call, Throwable t) {
                Toast.makeText(CreateBookingActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the OnClickListener for the "info" button
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionsAlert();
            }
        });

        // Set up the OnClickListener for the create booking button
        createBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clientName.getText().toString().equals("") && email.getText().toString().equals("") && phone.getText().toString().equals("")) {
                    Toast.makeText(CreateBookingActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                } else if (selectedTrain.equals("Select the train")) {
                    Toast.makeText(CreateBookingActivity.this, "Please select a train", Toast.LENGTH_SHORT).show();
                } else {
                    // Extract the selected trainModel name from the spinner
                    String selectedValue = "";
                    if (trainDropdown.getSelectedItem() != null) {
                        selectedValue = trainDropdown.getSelectedItem().toString();
                    }

                    // Extract only the date portion from the reservation date
                    String reservationDate = date.getText().toString().split("T")[0];

                    // Get the number of tickets from the 'num' EditText
                    String numberOfTickets = noOfTickets.getText().toString();

                    // Create the confirmation message with date and number of tickets
                    String confirmationMessage = "Train Name: " + selectedValue +
                            "\nDate: " + reservationDate +
                            "\nNumber of Tickets: " + numberOfTickets;

                    // Create a confirmation dialog with the message
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateBookingActivity.this);
                    builder.setTitle("Confirm Booking");
                    builder.setMessage(confirmationMessage);

                    // Add a positive button to confirm bookingModel
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
            String dateStr = String.format("%04d-%02d-%02d", year, month + 1, day);
            date.setText(dateStr);
        }
    };

    // Function to populate the trainModel spinner
    private void populateSpinner(List<String> trainNames, final List<String> dt) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainDropdown.setAdapter(adapter);

        trainDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedTrain = trainDropdown.getSelectedItem().toString();
                if (position == 0) {
                    selectedTrainId = dt.get(position);
                } else {
                    selectedTrainId = dt.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });
    }

    // Function to display the bookingModel policy information
    private void showActionsAlert() {
        String alertMessage = "a. Create new reservations (reservation date within 30 days from booking date, maximum 4 reservations per one person).\n\n" +
                "b. Update reservations (at least 5 days before the reservation date).\n\n" +
                "c. Cancel reservations (at least 5 days before the reservation date).";

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateBookingActivity.this);
        builder.setTitle("Booking Policy");
        builder.setMessage(alertMessage);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User acknowledged the bookingModel policy
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Function to create a bookingModel
    private void createBooking() {
        bookingModel u = new bookingModel();
        u.setRfid(nic);
        u.setTid(uid);
        u.setStatus(false);
        u.setTrain(selectedTrainId);
        u.setDate(date.getText().toString());
        u.setPhone(phone.getText().toString());
        u.setEmail(email.getText().toString());
        u.setName(clientName.getText().toString());
        u.setPassno(Integer.parseInt(noOfTickets.getText().toString()));

        Call<String> call = bookingService.createBooking(u);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response1) {
                Toast.makeText(CreateBookingActivity.this, "Booking Failed, Refer the Booking Policy", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CreateBookingActivity.this, "Booking Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ViewBookingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
