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

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This activity lists a patient's history of doctor visits
 */
public class PatientDoctorActivity extends Activity {

    public static final String ARG_ITEM_ID = "health_card_number";

    private Patient mPatient;

    // UI references
    private ListView mDoctorList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen_by_doctor);

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
        mDoctorList = (ListView) findViewById(android.R.id.list);
        mDoctorList.setEmptyView(findViewById(android.R.id.empty));

        // Populate the list if mPatient exists.
        if (mPatient != null) {
            List<Map<String, String>> adapterData = new ArrayList<Map<String, String>>();
            String[] colNames = new String[] { "title" };

            List<Calendar> doctorVisits = mPatient.getTimesSeenByDoctor();
            for (Calendar doctorVisit : doctorVisits) {
                Map<String, String> row = new HashMap<String, String>(1);

                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma, EEE, MMM d, yyyy");

                row.put(colNames[0], dateFormat.format(doctorVisit.getTime()));

                adapterData.add(row);
            }

            SimpleAdapter adapter = new SimpleAdapter(this,
                                                      adapterData,
                                                      R.layout.doctor_list_item,
                                                      colNames,
                                                      new int[] { android.R.id.title });
            mDoctorList.setAdapter(adapter);
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