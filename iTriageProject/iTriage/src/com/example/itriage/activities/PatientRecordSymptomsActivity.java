package com.example.itriage.activities;

import com.example.itriage.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;

public class PatientRecordSymptomsActivity extends Activity {

    // The argument representing the item ID that this activity represents.
	public static final String ARG_ITEM_ID = "health_card_number";
	
	//UI references.
	private EditText mSymptomsField;
	private String mPatientHealthCardNumber;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the activity_record_symptoms screen.
        setContentView(R.layout.activity_record_symptoms);
        
        Intent intent = getIntent();
        if (intent.hasExtra(ARG_ITEM_ID)) {
            mPatientHealthCardNumber = intent.getStringExtra(ARG_ITEM_ID);
        }
        
        // Grab text from symptoms field in the activity
        mSymptomsField = (EditText) findViewById(R.id.symptoms_text);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_record_symptoms, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Save our data to the particular patient we are working with.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
            	String symptoms = mSymptomsField.getText().toString();
                try {
                    Patient patient = ER.getInstance().getPatient(mPatientHealthCardNumber);
                    patient.recordSymptoms(symptoms);
                } catch (PatientNotFoundException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
