package com.example.itriage.activities;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.itriage.R;
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;

public class PatientEditActivity extends ActionBarActivity implements View.OnClickListener {

    // The argument representing the item ID that this activity represents.
    public static final String ARG_ITEM_ID = "patient_health_card_number";
    
    // Formats used for displaying date/time.
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");

    // The patient this activity is presenting.
    private Patient mPatient;

    // The patient info this fragment is presenting.
    private Calendar mBirthDate;
    private Calendar mArrivalTime;
    private Calendar timeNow;
    private boolean wrongBirthDate;
    private boolean wrongArrivalTime;

    // UI references
    private EditText mNameField;
    private EditText mHealthCardNumberField;
    private Button mBirthDateButton;
    private Button mArrivalDateButton;
    private Button mArrivalTimeButton;
    
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    // Set the view to be activity_patient_edit
        setContentView(R.layout.activity_patient_edit);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        
    	// Set UI references
        mNameField = (EditText) findViewById(R.id.patient_name);
        mHealthCardNumberField = (EditText) findViewById(R.id.patient_health_card_number);
        mBirthDateButton = (Button) findViewById(R.id.patient_birth_date);
        mArrivalDateButton = (Button) findViewById(R.id.patient_arrival_date);
        mArrivalTimeButton = (Button) findViewById(R.id.patient_arrival_time);
        
        Calendar now = Calendar.getInstance();

        // Load default patient info variables with current date/time

        timeNow = now;
        wrongBirthDate = false;
        wrongArrivalTime = false;

        if (intent.hasExtra(ARG_ITEM_ID)) {
            try {
                mPatient = ER.getInstance().getPatient(intent.getStringExtra(ARG_ITEM_ID));
                mBirthDate = mPatient.getBirthDate();
                mArrivalTime = mPatient.getArrivalTime();
                mNameField.setText(mPatient.getName());
                mHealthCardNumberField.setText(mPatient.getHealthCardNumber());
                String birthDate = dateFormat.format(mPatient.getBirthDate().getTime());
                String arrivalDate = dateFormat.format(mPatient.getArrivalTime().getTime());
                String arrivalTime = timeFormat.format(mPatient.getArrivalTime().getTime());
                mBirthDateButton.setText(birthDate);
                mArrivalDateButton.setText(arrivalDate);
                mArrivalTimeButton.setText(arrivalTime);
                
            } catch (PatientNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            mBirthDate = now;
            mArrivalTime = now;
            String birthDate = dateFormat.format(mBirthDate.getTime());
            String arrivalDate = dateFormat.format(mArrivalTime.getTime());
            String arrivalTime = timeFormat.format(mArrivalTime.getTime());
            mBirthDateButton.setText(birthDate);
            mArrivalDateButton.setText(arrivalDate);
            mArrivalTimeButton.setText(arrivalTime);
        }

        mBirthDateButton.setOnClickListener(this);
        mArrivalDateButton.setOnClickListener(this);
        mArrivalTimeButton.setOnClickListener(this);
    }

    // Populate the menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set the buttons in the menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.patient_edit_save:
                attemptSave();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the data that we are editing for this particular patient.
     */
    private void attemptSave() {
        String name = mNameField.getText().toString();
        String healthCardNumber = mHealthCardNumberField.getText().toString();

        // Reset errors
        mNameField.setError(null);
        mHealthCardNumberField.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a name.
        if (TextUtils.isEmpty(name)) {
            String error = getString(R.string.error_blank_name);
            mNameField.setError(error);
            focusView = mNameField;
            cancel = true;
        } else if (TextUtils.isEmpty(healthCardNumber)) {
            String error = getString(R.string.error_blank_health_card_number);
            mHealthCardNumberField.setError(error);
            focusView = mHealthCardNumberField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt save and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else if (mBirthDate.getTimeInMillis() > timeNow.getTimeInMillis()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Birthdate chosen is ahead of current date." +
                    "Please set birth date to" +
                    " before current time", Toast.LENGTH_LONG);
            toast.show();
        } else if (mArrivalTime.getTimeInMillis() > timeNow.getTimeInMillis()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Wrong arrival date/time. " +
                    "Please set arrival time and date to" +
                    " before current time and date.", Toast.LENGTH_LONG);
            toast.show();
        } else {
            // Add the patient to the ER
            if (intent.hasExtra(ARG_ITEM_ID)) {
                mPatient.setName(name);
                mPatient.setBirthDate(mBirthDate);
                mPatient.setHealthCardNumber(healthCardNumber);
                mPatient.setArrivalTime(mArrivalTime);
            } else {
                ER.getInstance().addPatient(name, mBirthDate, healthCardNumber, mArrivalTime);
                Intent intent = new Intent(this, PatientDetailActivity.class);
                intent.putExtra(PatientDetailActivity.ARG_ITEM_ID, healthCardNumber);
                startActivity(intent);
            }
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.patient_birth_date:
                showDateDialog(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    	mBirthDate = new GregorianCalendar(year, month, day);
                        mBirthDateButton.setText(dateFormat.format(mBirthDate.getTime()));
                    }
                });
                break;
            case R.id.patient_arrival_date:
                showDateDialog(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mArrivalTime.set(Calendar.YEAR, year);
                        mArrivalTime.set(Calendar.MONTH, month);
                        mArrivalTime.set(Calendar.DAY_OF_MONTH, day);
                        mArrivalDateButton.setText(dateFormat.format(mArrivalTime.getTime()));
                    }
                });
                break;
            case R.id.patient_arrival_time:
                showTimeDialog(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mArrivalTime.set(Calendar.HOUR_OF_DAY, hour);
                        mArrivalTime.set(Calendar.MINUTE, minute);
                        mArrivalTimeButton.setText(timeFormat.format(mArrivalTime.getTime()));
                    }
                });
                break;
        }

    }

    /**
     * Shows a dialog screen asking for the users input for a date.
     * @param listener Callback saying the user is done picking the date.
     */
    private void showDateDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }

    /**
     * Shows a dialog screen asking for the users input for a time.
     * @param listener Callback saying the user is done picking the date.
     */
    private void showTimeDialog(TimePickerDialog.OnTimeSetListener listener) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, listener, hour, minute, false);
        dialog.show();
    }
}
