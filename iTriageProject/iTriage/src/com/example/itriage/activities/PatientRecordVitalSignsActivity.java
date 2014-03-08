package com.example.itriage.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.example.itriage.R;
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;

public class PatientRecordVitalSignsActivity extends Activity {

    // The argument representing the item ID that this activity represents.
    public static final String ARG_ITEM_ID = "health_card_number";

    //The current patients health card number.
    private String mPatientHealthCardNumber;

    //UI references.
    private EditText mTemperatureField;
    private EditText mBloodPressureSystolicField;
    private EditText mBloodPressureDiastolicField;
    private EditText mHeartRateField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     // Show the activity_record_vital_signs screen.
        setContentView(R.layout.activity_record_vital_signs);

        Intent intent = getIntent();
        if (intent.hasExtra(ARG_ITEM_ID)) {
            mPatientHealthCardNumber = intent.getStringExtra(ARG_ITEM_ID);
        }
        
        // Grab text from symptoms field in the activity
        mTemperatureField = (EditText) findViewById(R.id.patient_record_temperature);
        mBloodPressureSystolicField = (EditText) findViewById(R.id.patient_record_blood_pressure_s);
        mBloodPressureDiastolicField = (EditText) findViewById(R.id.patient_record_blood_pressure_d);
        mHeartRateField = (EditText) findViewById(R.id.patient_record_heart_rate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_record_vital_signs, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                View focusView = null;
                boolean cancel = false;

                String temperature = mTemperatureField.getText().toString();
                String bloodPressureSystolic = mBloodPressureSystolicField.getText().toString();
                String bloodPressureDiastolic = mBloodPressureDiastolicField.getText().toString();
                String heartRate = mHeartRateField.getText().toString();

                // Check for valid fields
                if (TextUtils.isEmpty(temperature)) {
                    String error = getString(R.string.error_blank_field);
                    mTemperatureField.setError(error);
                    focusView = mTemperatureField;
                    cancel = true;
                } 
                else if (TextUtils.isEmpty(bloodPressureSystolic)) {
                    String error = getString(R.string.error_blank_field);
                    mBloodPressureSystolicField.setError(error);
                    focusView = mBloodPressureSystolicField;
                    cancel = true;
                } else if (TextUtils.isEmpty(bloodPressureDiastolic)) {
                    String error = getString(R.string.error_blank_field);
                    mBloodPressureDiastolicField.setError(error);
                    focusView = mBloodPressureDiastolicField;
                    cancel = true;
                } else if (TextUtils.isEmpty(heartRate)) {
                    String error = getString(R.string.error_blank_field);
                    mHeartRateField.setError(error);
                    focusView = mHeartRateField;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    try {
                        Patient patient = ER.getInstance().getPatient
                                (mPatientHealthCardNumber);
                        patient.recordVitalSigns(Double.parseDouble(temperature),
                                                 Integer.parseInt(bloodPressureSystolic),
                                                 Integer.parseInt(bloodPressureDiastolic),
                                                 Integer.parseInt(heartRate));
                    } catch (PatientNotFoundException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
