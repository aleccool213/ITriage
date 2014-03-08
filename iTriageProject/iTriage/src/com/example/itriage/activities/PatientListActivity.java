package com.example.itriage.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.itriage.R;
import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import com.example.itriage.models.PatientNotFoundException;
import com.example.itriage.models.UserManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientListActivity extends ActionBarActivity implements
        ActionBar.OnNavigationListener, AdapterView.OnItemClickListener {

    // UI references
    private ListView mPatientList;

    // The state of the patient filter spinner
    private enum FilterState {
        BY_URGENCY,
        BY_ARRIVAL_TIME,
        ALL
    }
    private FilterState filterState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        // Default filter state is waiting patients sorted by urgency
        filterState = FilterState.BY_URGENCY;

        // Create UI references and set event listeners
        mPatientList = (ListView) findViewById(android.R.id.list);
        mPatientList.setEmptyView(findViewById(android.R.id.empty));
        mPatientList.setOnItemClickListener(this);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                                           R.layout.patient_filter_spinner_title,
                                           android.R.id.text1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String s : getResources().getStringArray(R.array.patient_filter))
            adapter.add(s);
        actionBar.setListNavigationCallbacks(adapter, this);

        // Hide the activity title
        actionBar.setDisplayShowTitleEnabled(false);

        // Load the ER database from file
        loadERDatabaseFromFile();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Populate the list of patients
        populatePatientList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        switch (UserManager.getInstance().getCurrentUserType()) {
            case NURSE:
                getMenuInflater().inflate(R.menu.patient_list_nurse, menu);
                break;
            case PHYSICIAN:
                getMenuInflater().inflate(R.menu.patient_list_physician, menu);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        // Handle presses on the action bar buttons
        switch (item.getItemId()) {
            case R.id.patient_search:
                openSearchDialog();
                return true;
            case R.id.patient_add:
                intent = new Intent(this, PatientEditActivity.class);
                startActivity(intent);
                return true;
            case R.id.patients_save:
                return saveERDatabaseToFile();
            case R.id.log_out:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {
            case 0:
                filterState = FilterState.BY_URGENCY;
                populatePatientList();
                return true;
            case 1:
                filterState = FilterState.BY_ARRIVAL_TIME;
                populatePatientList();
                return true;
            case 2:
                filterState = FilterState.ALL;
                populatePatientList();
                return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                             long id) {

        // Using health card number as ID to pass to the activity.
        Patient patient = patientListWithCurrentFilter().get(position);
        System.out.println(position);
        String healthCardNumber = patient.getHealthCardNumber();

        Intent intent = new Intent(this, PatientDetailActivity.class);
        intent.putExtra(PatientDetailActivity.ARG_ITEM_ID, healthCardNumber);
        startActivity(intent);
    }

    /**
     * Populates the ListView of patients using the current filter.
     */
    private void populatePatientList() {
        // The items to be passed to a SimpleAdapter
        List<Map<String, String>> listItems;
        listItems = new ArrayList<Map<String, String>>();

        // Fill the list with patient data
        for (Patient patient : patientListWithCurrentFilter()) {
            Map<String, String> row = new HashMap<String, String>();
            // Set the thumbnail depending on whether the patient is improving
            int thumbnailId;
            int status = patient.getStatus();
            if (status < 0)
                thumbnailId = R.drawable.ic_down;
            else if (status > 0)
                thumbnailId = R.drawable.ic_up;
            else
                thumbnailId = R.drawable.ic_neutral;
            row.put("thumb", String.valueOf(thumbnailId));

            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma, EEE, MMM d, yyyy");

            // Add the name and health card number
            row.put("row1", patient.getName());
            row.put("row2", patient.getHealthCardNumber());
            row.put("urgency", String.valueOf(patient.getUrgency()));
            row.put("arrivalTime", dateFormat.format(patient.getArrivalTime().getTime()));

            listItems.add(row);
        }

        // Initialize a new SimpleAdapter
        String[] from = new String[] {"thumb", "row1", "row2", "urgency", "arrivalTime"};
        int[] to = new int[] { android.R.id.icon,
                               R.id.name,
                               R.id.health_card_number,
                               R.id.urgency,
                               R.id.arrival_time };
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                                                        listItems,
                                                        R.layout.patient_list_item,
                                                        from, to);

        // Set the ListView to use the new adapter
        mPatientList.setAdapter(simpleAdapter);
    }

    /**
     * Returns a list of patients with the current filter applied.
     *
     * @return a list of patients with the current filter applied
     */
    private List<Patient> patientListWithCurrentFilter() {
        ER er = ER.getInstance();
        switch (filterState) {
            case BY_URGENCY:
                return er.getWaitingPatientsByUrgency();
            case BY_ARRIVAL_TIME:
                return er.getWaitingPatientsByArrivalTime();
            case ALL:
                return er.getAllPatientsByName();
            default:
                return er.getAllPatientsByName();
        }
    }

    /**
     * Loads the ER database from file.
     * @return True if the database was loaded successfully, otherwise false.
     */
    private boolean loadERDatabaseFromFile() {
        boolean success = true;
        String erFileName = getString(R.string.er_database_filename);
        try {
            InputStream inputStream;
            inputStream = openFileInput(erFileName);
            try {
                ER.getInstance().loadFromStream(inputStream);
            } catch (IOException e) {
                String message = "Unable to load ER database from file.";
                showFileErrorToast(message);
                success = false;
            } finally {
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            String message = "ER database was not found.";
            showFileErrorToast(message);
            success = false;
        } catch (IOException e) {
            String message = "Unable to open ER database file.";
            showFileErrorToast(message);
            success = false;
        }
        return success;
    }

    /**
     * Saves the ER database to file.
     * @return true if the database was saved successfully, otherwise false
     */
    private boolean saveERDatabaseToFile() {
        boolean success = true;
        String fileName = getString(R.string.er_database_filename);
        try {
            OutputStream outputStream;
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            try {
                ER.getInstance().saveToStream(outputStream);
            } catch (IOException e) {
                String message = "Unable to save ER database to existing file.";
                showFileErrorToast(message);
                success = false;
            } finally {
                outputStream.close();
            }
        } catch (IOException e) {
            String message = "Unable to open/create ER database file.";
            showFileErrorToast(message);
            success = false;
        }
        return success;
    }

    /**
     * Shows a Toast with the specified message about a file error.
     * @param message a message about a file error
     */
    private void showFileErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void openSearchDialog() {
        final EditText searchQuery;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_patient_list_search);
        dialog.setTitle("Search");

        searchQuery = (EditText) dialog.findViewById(R.id.search_field);
        Button searchSubmit = (Button) dialog.findViewById(R.id.query_submit);
        searchSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchString = searchQuery.getText().toString();
                try {
                    ER.getInstance().getPatient(searchString);
                    Intent intent = new Intent(PatientListActivity.this, PatientDetailActivity.class);
                    intent.putExtra(PatientDetailActivity.ARG_ITEM_ID, searchString);
                    startActivity(intent);
                    dialog.dismiss();
                } catch (PatientNotFoundException e) {
                    String message = "Patient not found";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

}
