package com.example.itriage.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.example.itriage.R;
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;
import com.example.itriage.models.VitalSigns;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This activity lists a patient's history of recorded vital signs
 */
public class PatientVitalSignsActivity extends Activity {

    // The argument representing the item ID that this activity represents.
    public static final String ARG_ITEM_ID = "health_card_number";

    // The current patient we are looking at.
    private Patient mPatient;

    // UI references
    private ListView mVitalSignsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_signs);

        Intent intent = getIntent();

        if (intent.hasExtra(ARG_ITEM_ID)) {
            try {
                String healthCardNumber = intent.getStringExtra(ARG_ITEM_ID);
                ER er = ER.getInstance();
                mPatient = er.getPatient(healthCardNumber);
            } catch (PatientNotFoundException e) {
                String message = getString(R.string.patient_not_found);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }

        // Create UI references
        mVitalSignsList = (ListView) findViewById(android.R.id.list);
        mVitalSignsList.setEmptyView(findViewById(android.R.id.empty));

        // Populate the list is mPatient exists.
        if (mPatient != null) {
            List<Map<String, String>> adapterData = new ArrayList<Map<String, String>>();
            String[] colNames = new String[] { "title",
                                               getString(R.string.temperature),
                                               getString(R.string.blood_pressure),
                                               getString(R.string.heart_rate) };

            Map<Calendar, VitalSigns> vitalSignsHistory = mPatient.getVitalSigns().getHistory();
            for (Calendar timeRecorded : vitalSignsHistory.keySet()) {
                Map<String, String> row = new HashMap<String, String>(3);

                VitalSigns vs = vitalSignsHistory.get(timeRecorded);

                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma, EEE, MMM d, yyyy");
                String fDate, fTemperature, fBloodPressure, fHeartRate;

                fDate = dateFormat.format(timeRecorded.getTime());
                fTemperature = String.format("%.1f", vs.getTemperature());
                fBloodPressure = String.format("%.0f / %.0f", vs.getSystolicBloodPressure(), vs.getDiastolicBloodPressure());
                fHeartRate = String.format("%.0f", vs.getHeartRate());

                row.put(colNames[0], fDate);
                row.put(colNames[1], fTemperature);
                row.put(colNames[2], fBloodPressure);
                row.put(colNames[3], fHeartRate);

                adapterData.add(row);
            }

            SimpleAdapter adapter = new SimpleAdapter(this,
                                                      adapterData,
                                                      R.layout.vital_signs_list_item,
                                                      colNames,
                                                      new int[] { android.R.id.title,
                                                                  R.id.temperature,
                                                                  R.id.blood_pressure,
                                                                  R.id.heart_rate });
            mVitalSignsList.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

}