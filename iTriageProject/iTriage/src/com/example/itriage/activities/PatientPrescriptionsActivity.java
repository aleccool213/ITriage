package com.example.itriage.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.itriage.R;
import com.example.itriage.R.layout;
import com.example.itriage.R.menu;
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;
import com.example.itriage.models.VitalSigns;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PatientPrescriptionsActivity extends Activity {

    // The argument representing the item ID that this activity represents
    public static final String ARG_ITEM_ID = "health_card_number";

    private Patient mPatient;

    // UI references
    private ListView mPrescriptionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_prescription);
		
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
        
        mPrescriptionList = (ListView) findViewById(R.id.prescriptionlist);
        mPrescriptionList.setEmptyView(findViewById(R.id.emptyprescription));
        
        
        
        if (mPatient != null) {
            List<Map<String, String>> adapterData = new ArrayList<Map<String, String>>();
            String[] colNames = new String[] { "title",
                                               getString(R.string.medication),
                                               getString(R.string.instructions)};

            Map<Calendar, List<String>> prescriptionHistory = mPatient.getPrescriptions().getHistory();
            for (Calendar timeRecorded : prescriptionHistory.keySet()) {
                Map<String, String> row = new HashMap<String, String>(3);

                List<String> prescription = prescriptionHistory.get(timeRecorded);

                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma, EEE, MMM d, yyyy");
                String fDate, fMedication, fInstructions;

                fDate = dateFormat.format(timeRecorded.getTime());
                fMedication = prescription.get(0);
                fInstructions = prescription.get(1);
                
                row.put(colNames[0], fDate);
                row.put(colNames[1], fMedication);
                row.put(colNames[2], fInstructions);

                adapterData.add(row);
            }

            SimpleAdapter adapter = new SimpleAdapter(this,
                                                      adapterData,
                                                      R.layout.prescription_list_item,
                                                      colNames,
                                                      new int[] { android.R.id.title,
                                                                  R.id.medication,
                                                                  R.id.instructions,});
            mPrescriptionList.setAdapter(adapter);
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
