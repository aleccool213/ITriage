package com.example.itriage.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.example.itriage.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;

public class PatientAddPrescriptionActivity extends Activity {

	// The argument representing the item ID that this activity represents
    public static final String ARG_ITEM_ID = "health_card_number";

    // The patients healthcard number
    private String mPatientHealthCardNumber;
    
    //UI references
    private EditText mMedication;
    private EditText mInstructions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//show the activity_patient_add_prescription screen
		setContentView(R.layout.activity_patient_add_prescription);
	
        Intent intent = getIntent();
        if (intent.hasExtra(ARG_ITEM_ID)) {
            mPatientHealthCardNumber = intent.getStringExtra(ARG_ITEM_ID);
        }
        
        //grab the text from the require fields 
        mMedication = (EditText) findViewById(R.id.patient_medication);
        mInstructions = (EditText) findViewById(R.id.patient_instructions);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_add_prescription, menu);
		return true;
	}

	//save the data with the particular patient we are dealing with
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.patient_record_prescription:
                String medication = mMedication.getText().toString();
                String instructions = mInstructions.getText().toString();
                try {
                    Patient patient = ER.getInstance().getPatient(mPatientHealthCardNumber);
                    patient.addPrescription(medication, instructions);
                } catch (PatientNotFoundException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

}
