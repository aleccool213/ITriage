package com.example.itriage.activities;
 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;
import com.example.itriage.R;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
 
public class PatientSymptomsActivity extends Activity{
       
    // The argument representing the item ID that this activity represents.
    public static final String ARG_ITEM_ID = "health_card_number";
 
    // Current patient we are looking at.
    private Patient mPatient;
 
    // UI references
    private ListView mSymptomsList;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        Intent intent = getIntent();

        if (intent.hasExtra(ARG_ITEM_ID)) {
            try {
                String healthCardNumber = intent.getStringExtra(ARG_ITEM_ID);
                ER er = ER.getInstance();
                mPatient = er.getPatient(healthCardNumber);
            } catch (PatientNotFoundException e) {
                String message = getString(R.string.patient_not_found);
                Toast.makeText(this, message, Toast.LENGTH_SHORT);
            }
        }
       
        // Create UI references.
        mSymptomsList = (ListView) findViewById(android.R.id.list);
        mSymptomsList.setEmptyView(findViewById(android.R.id.empty));
       
        // Populate the list is mPatient exists.
        if (mPatient != null) {
            List<Map<String, String>> adapterData = new ArrayList<Map<String, String>>();
            String[] colNames = new String[] { "title",
                                               getString(R.string.symptoms)};
           
                Map<Calendar, String> symptomsHistory = mPatient.getSymptoms().getHistory();
                for (Calendar timeRecorded : symptomsHistory.keySet()) {
                Map<String, String> row = new HashMap<String, String>(1);

                String currentSymptom = symptomsHistory.get(timeRecorded);
               
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma, EEE, MMM d, yyyy");
                String fDate;
               
                fDate = dateFormat.format(timeRecorded.getTime());
               
                row.put(colNames[0], fDate);
                row.put(colNames[1], currentSymptom);
               
                adapterData.add(row);
               
                }
               
                SimpleAdapter adapter = new SimpleAdapter(this,
                adapterData,
                R.layout.symptoms_list_item,
                colNames,
                new int[] { android.R.id.title,
                            R.id.symptom});
                mSymptomsList.setAdapter(adapter);
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