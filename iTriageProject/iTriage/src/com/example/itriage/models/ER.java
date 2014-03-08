package com.example.itriage.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.*;

/**
 * An emergency room which holds and organizes patients.
 * This is a singleton class.
 */
public class ER {

    // Map of all Patients, with health card numbers as keys.
    private Map<String, Patient> allPatients;

    /**
     * Since this is a singleton, a new instance is created and a reference
     * is stored as soon as the class is loaded.
     */
    private static final ER INSTANCE = new ER();

    /**
     * Returns the instance of this ER class.
     *
     * @return the instance of this ER class.
     */
    public static ER getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs this ER.
     */
    private ER() {
        this.allPatients = new HashMap<String, Patient>();
    }

    /**
     * Adds a new Patient to this ER from their information.
     *
     * @param name The name of this Patient.
     * @param healthCardNumber The health card number of this Patient.
     * @param birthDate The month of the birthday of this Patient.
     * @param arrivalTime The arrival time of this Patient.
     */
    public void addPatient(String name, Calendar birthDate,
                           String healthCardNumber, Calendar arrivalTime) {
        addPatient(new Patient(name, birthDate, healthCardNumber, arrivalTime));
    }

    /**
     * Adds a new Patient to this ER from a pre-existing patient object.
     *
     * @param patient The patient object that is being added.
     */
    public void addPatient(Patient patient) {
        String healthCardNumber = patient.getHealthCardNumber();
        allPatients.put(healthCardNumber, patient);
    }

    /**
     * Returns the patient with the given health card number.
     *
     * @param healthCardNumber The health card number.
     * @return Returns the Patient based with the given health card number.
     */
    public Patient getPatient(String healthCardNumber)
            throws PatientNotFoundException {
        if (allPatients.containsKey(healthCardNumber))
            return allPatients.get(healthCardNumber);
        else {
            String format = "Patient with health card number %s was not found.";
            String message = String.format(format, healthCardNumber);
            throw new PatientNotFoundException(message);
        }
    }

    /**
     * Removes the patient with the given health card number.
     *
     * @param healthCardNumber the health card number of the patient to remove
     */
    public void removePatient(String healthCardNumber) {
        allPatients.remove(healthCardNumber);
    }

    /**
     * Gets all patients, sorted in alphabetical order by name.
     *
     * @return a list of all patients, sorted in alphabetical order by name
     */
    public List<Patient> getAllPatientsByName() {

        // Create a List of all patients
        List<Patient> sortedList;
        sortedList= new ArrayList<Patient>(allPatients.values());

        // Create a comparator to be used to sort the list by patient urgency
        Comparator<Patient> nameComparator = new Comparator<Patient>() {
            @Override
            public int compare(Patient patient1, Patient patient2) {
                return patient1.getName().compareTo(patient2.getName());
            }
        };

        // Sort the list using the comparator with O(n log n) performance
        Collections.sort(sortedList, nameComparator);

        return sortedList;
    }

    /**
     * Returns the list of waiting patients in descending order by urgency.
     *
     * @return the list of waiting patients in descending order by urgency.
     */
    public List<Patient> getWaitingPatientsByUrgency() {

        // Create a List of all patients
        List<Patient> sortedList = new ArrayList<Patient>();

        // Only add waiting patients to the list
        for (Patient patient : allPatients.values())
            if (!patient.hasBeenSeenByDoctor())
                sortedList.add(patient);

        // Create a comparator to be used to sort the list by patient urgency
        Comparator<Patient> urgencyComparator = new Comparator<Patient>() {
            @Override
            public int compare(Patient p1, Patient p2) {
                int comparison = p2.getUrgency().compareTo(p1.getUrgency());
                if (comparison == 0)
                    return p2.getArrivalTime().compareTo(p1.getArrivalTime());
                return comparison;
            }
        };

        // Sort the list using the comparator with O(n log n) performance
        Collections.sort(sortedList, urgencyComparator);

        return sortedList;
    }

    /**
     * Returns the list of waiting patients by arrival time
     * from earliest to latest.
     *
     * @return the list of waiting patients by arrival time
     * from earliest to latest.
     */
    public List<Patient> getWaitingPatientsByArrivalTime() {
        // Create a List of all patients
        List<Patient> sortedList = new ArrayList<Patient>();

        // Only add waiting patients to the list
        for (Patient patient : allPatients.values())
            if (!patient.hasBeenSeenByDoctor())
                sortedList.add(patient);

        // Create a comparator to be used to sort the list by arrival time
        Comparator<Patient> arrivalTimeComparator = new Comparator<Patient>() {
            @Override
            public int compare(Patient p1, Patient p2) {
                return p1.getArrivalTime().compareTo(p2.getArrivalTime());
            }
        };

        // Sort the list using the comparator with O(n log n) performance
        Collections.sort(sortedList, arrivalTimeComparator);
        Collections.reverse(sortedList);
        return sortedList;
    }

    /**
     * Saves this ER as JSON to the specified stream.
     *
     * @param outputStream the stream to write to
     * @throws IOException
     */
    public void saveToStream(OutputStream outputStream) throws IOException {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
        String allPatientsAsJson = gson.toJson(allPatients);
        outputStream.write(allPatientsAsJson.getBytes());
    }

    /**
     * Loads this ER from the specified JSON stream.
     *
     * @param inputStream the stream of JSON to read from
     * @throws IOException
     */
    public void loadFromStream(InputStream inputStream) throws IOException {
        Gson gson = new Gson();
        InputStreamReader isr = new InputStreamReader(inputStream);

        Type listType = new TypeToken<Map<String, Patient>>(){}.getType();
        allPatients = gson.fromJson(isr, listType);
    }
}
