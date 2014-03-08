package com.example.itriage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.itriage.R;
import com.example.itriage.models.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PatientDetailActivity extends ActionBarActivity implements View.OnClickListener {

    // The argument representing the item ID that this activity represents.
    public static final String ARG_ITEM_ID = "health_card_number";

    // Format used for displaying date/time.
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("h:mma, EEE, MMM d, yyyy");

    //UI references.
    private View mUrgencyView;
    private View mNameView;
    private View mBirthDateView;
    private View mHealthCardNumberView;
    private View mArrivalTimeView;
    private View mTemperatureView;
    private View mBloodPressureView;
    private View mHeartRateView;
    private View mSymptomsView;
    private View mPrescriptionsView;
    private View mDoctorView;

    // The Patient this activity is presenting.
    private Patient mPatient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        Intent intent = getIntent();

        if (intent.hasExtra(ARG_ITEM_ID)) {
            try {
                String healthCardNumber = intent.getStringExtra(ARG_ITEM_ID);
                ER er = ER.getInstance();
                mPatient = er.getPatient(healthCardNumber);
            } catch (PatientNotFoundException e) {
                String message = getString(R.string.patient_not_found);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        // Create UI references
        mUrgencyView = findViewById(R.id.patient_detail_urgency);
        mNameView = findViewById(R.id.patient_detail_name);
        mBirthDateView = findViewById(R.id.patient_detail_birth_date);
        mHealthCardNumberView = findViewById(R.id.patient_detail_health_card_number);
        mArrivalTimeView = findViewById(R.id.patient_detail_arrival_time);
        mTemperatureView = findViewById(R.id.patient_detail_temperature);
        mBloodPressureView = findViewById(R.id.patient_detail_blood_pressure);
        mHeartRateView = findViewById(R.id.patient_detail_heart_rate);
        mSymptomsView = findViewById(R.id.patient_detail_symptoms);
        mPrescriptionsView = findViewById(R.id.patient_detail_prescriptions);
        mDoctorView = findViewById(R.id.patient_detail_doctor);

        // Attach event handlers for the cards
        View vitalSignsView = findViewById(R.id.patient_detail_vital_signs);
        vitalSignsView.setOnClickListener(this);
        mSymptomsView.setOnClickListener(this);
        mPrescriptionsView.setOnClickListener(this);
        mDoctorView.setOnClickListener(this);

        // Attach event handlers for the section header buttons
        TextView recordVitalSignsButton = (TextView) findViewById(R.id.record_vital_signs);
        TextView recordSymptomsButton = (TextView) findViewById(R.id.record_symptoms);
        TextView editInfoButton = (TextView) findViewById(R.id.edit_info);
        TextView addPrescriptionButton = (TextView) findViewById(R.id.add_prescription);
        TextView addSeenByDoctorButton = (TextView) findViewById(R.id.add_seen_by_doctor);

        if (UserManager.getInstance().getCurrentUserType().equals(UserManager.UserType.NURSE)) {

            // Nurse is logged in
            recordVitalSignsButton.setOnClickListener(this);
            recordSymptomsButton.setOnClickListener(this);
            editInfoButton.setOnClickListener(this);
            addPrescriptionButton.setVisibility(View.GONE);
            addSeenByDoctorButton.setOnClickListener(this);
        } else {

            // Physician is logged in
            recordVitalSignsButton.setVisibility(View.GONE);
            recordSymptomsButton.setVisibility(View.GONE);
            editInfoButton.setVisibility(View.GONE);
            addPrescriptionButton.setOnClickListener(this);
            addSeenByDoctorButton.setVisibility(View.GONE);
        }
    }

    // This is run every time we go to the activity patient detail screen
    @Override
    protected void onResume() {
        super.onResume();

        // Populate the views with patient data.
        if (mPatient != null)
            populatePatientData();
    }

    /**
     * Populates the patient data screen with information of the patient we are currently looking at.
     */
    private void populatePatientData() {

        // Set the activity title
        setTitle(mPatient.getName());

        int urgency = mPatient.getUrgency();
        String urgencyFormat;
        if (urgency <= 1)
            urgencyFormat = "Non-urgent (%d)";
        else if (urgency == 2)
            urgencyFormat = "Less urgent (%d)";
        else
            urgencyFormat = "Urgent (%d)";
        setText(mUrgencyView, getString(R.string.urgency), String.format(urgencyFormat, urgency));

        // Populate the Vital Signs section with the patient's temperature, blood pressure, and heart rate.
        String[] patientVitalSignsLabels = getResources().getStringArray(R.array.patient_vital_signs_labels);

        VitalSigns vitalSigns = mPatient.getLatestVitalSigns();
        String temperature, bloodPressure, heartRate;
        if (vitalSigns != null) {
            temperature = String.format("%.1f", vitalSigns.getTemperature());
            bloodPressure = String.format("%.0f / %.0f", vitalSigns.getSystolicBloodPressure(), vitalSigns.getDiastolicBloodPressure());
            heartRate = String.format("%.0f", vitalSigns.getHeartRate());
        } else {
            temperature = getString(R.string.none_recorded);
            bloodPressure = getString(R.string.none_recorded);
            heartRate = getString(R.string.none_recorded);
        }

        setText(mTemperatureView, patientVitalSignsLabels[0], temperature);
        setText(mBloodPressureView, patientVitalSignsLabels[1], bloodPressure);
        setText(mHeartRateView, patientVitalSignsLabels[2], heartRate);

        // Populate the Info section with the patient's name, birth date, health card number, arrival time and symptoms.
        String[] patientInfoLabels = getResources().getStringArray(R.array.patient_info_labels);

        String formattedBirthDate = dateFormat.format(mPatient.getBirthDate().getTime());
        String formattedArrivalTime = dateTimeFormat.format(mPatient.getArrivalTime().getTime());

        setText(mNameView, patientInfoLabels[0], mPatient.getName());
        setText(mBirthDateView, patientInfoLabels[1], formattedBirthDate);
        setText(mHealthCardNumberView, patientInfoLabels[2], mPatient.getHealthCardNumber());
        setText(mArrivalTimeView, patientInfoLabels[3], formattedArrivalTime);

        // Populate the Symptoms section
        String symptoms = mPatient.getLatestSymptoms();
        TextView symptomsTextView = (TextView) mSymptomsView.findViewById(android.R.id.text1);
        if (symptoms != null)
            symptomsTextView.setText(symptoms);
        else
            symptomsTextView.setText(getString(R.string.none_recorded));

        // Populate the prescriptions section
        List<String> prescription = mPatient.getLatestPrescription();
        if (prescription != null)
            setText(mPrescriptionsView, prescription.get(0), prescription.get(1));
        else {
            String placeholder = getString(R.string.none_recorded);
            setText(mPrescriptionsView, placeholder, placeholder);
        }

        SimpleDateFormat timeDateFormat = new SimpleDateFormat("h:mma, EEE, MMM d, yyyy");
        String fLastSeenByDoctor;
        if (mPatient.hasBeenSeenByDoctor()) {
            Calendar lastSeenByDoctor = mPatient.getLastSeenByDoctor();
            fLastSeenByDoctor = timeDateFormat.format(lastSeenByDoctor.getTime());
        } else
            fLastSeenByDoctor = getString(R.string.none_recorded);
        TextView doctorTextView = (TextView) mDoctorView.findViewById(android.R.id.text1);
        doctorTextView.setText(fLastSeenByDoctor);
    }

    /**
     * Sets the text in a container view which contains TextViews with ids @android:id/text1 and @android:id/text2
     *
     * @param container the container view
     * @param label the value to set for @android:id/text1
     * @param value the value to set for @android:id/text2
     */
    private void setText(View container, String label, String value) {
        ((TextView) container.findViewById(android.R.id.text1)).setText(label);
        ((TextView) container.findViewById(android.R.id.text2)).setText(value);
    }

    @Override
    /**
     * Populates the menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     *
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete:
                ER.getInstance().removePatient(mPatient.getHealthCardNumber());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle when the user clicks on the buttons next to section headers.
     *
     * @param view The button which was pressed.
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.record_vital_signs:
                intent = new Intent(this, PatientRecordVitalSignsActivity.class);
                intent.putExtra(PatientRecordVitalSignsActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
                startActivity(intent);
                break;
            case R.id.patient_detail_vital_signs:
                intent = new Intent(this, PatientVitalSignsActivity.class);
                intent.putExtra(PatientVitalSignsActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
                startActivity(intent);
                break;
            case R.id.record_symptoms:
                intent = new Intent(this, PatientRecordSymptomsActivity.class);
                intent.putExtra(PatientRecordSymptomsActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
                startActivity(intent);
                break;
            case R.id.patient_detail_symptoms:
                intent = new Intent(this, PatientSymptomsActivity.class);
                intent.putExtra(PatientSymptomsActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
                startActivity(intent);
                break;
            case R.id.add_prescription:
                intent = new Intent(this, PatientAddPrescriptionActivity.class);
                intent.putExtra(PatientAddPrescriptionActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
                startActivity(intent);
                break;
            case R.id.patient_detail_prescriptions:
                intent = new Intent(this, PatientPrescriptionsActivity.class);
                intent.putExtra(PatientPrescriptionsActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
                startActivity(intent);
                break;
            case R.id.edit_info:
            	intent = new Intent(this, PatientEditActivity.class);
            	intent.putExtra(PatientEditActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
            	startActivity(intent);
                break;
            case R.id.patient_detail_doctor:
                intent = new Intent(this, PatientDoctorActivity.class);
                intent.putExtra(PatientDoctorActivity.ARG_ITEM_ID, mPatient.getHealthCardNumber());
                startActivity(intent);
                break;
            case R.id.add_seen_by_doctor:
                mPatient.addSeenByDoctor();
                populatePatientData();
                break;
        }
    }

}
