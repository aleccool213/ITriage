package com.example.itriage.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A patient in an ER.
 */
public class Patient {

    // This Patient's name
	private String name;

    // This Patient's date of birth
	private Calendar birthDate;

    // This Patient's health card number
	private String healthCardNumber;

    // This Patient's arrival time at the the hospital
	private Calendar arrivalTime;

    // This Patient's record of vital signs
	private PatientRecord<VitalSigns> vitalSigns;

    // This Patient's record of symptoms
	private PatientRecord<String> symptoms;

    // This Patient's record of when she has been seen by a doctor
	private TreeSet<Calendar> timesSeenByDoctor;

    // This Patient's record of prescriptions
	private PatientRecord<List<String>> prescriptions;
	
	// -1 if this Patient's urgency is decreasing; 0 if neutral; 1 if increasing
	private int status;

    /**
     * Creates a Patient with the given name, date of birth, health card
     * number and arrival time.
     *
     * @param name The name of this Patient.
     * @param birthDate The birth date of this Patient.
     * @param healthCardNumber The health card number of this Patient.
     * @param arrivalTime The arrival time of this Patient.
     */
    public Patient(String name, Calendar birthDate,
                   String healthCardNumber, Calendar arrivalTime) {
        // Clear the MILLISECOND field; we only care about SECOND precision
        birthDate.clear(Calendar.MILLISECOND);
        arrivalTime.clear(Calendar.MILLISECOND);

        this.name = name;
        this.healthCardNumber = healthCardNumber;
        this.birthDate = birthDate;
        this.arrivalTime = arrivalTime;

        this.vitalSigns = new PatientRecord<VitalSigns>();
        this.symptoms = new PatientRecord<String>();
        this.timesSeenByDoctor = new TreeSet<Calendar>();
        this.prescriptions = new PatientRecord<List<String>>();
        this.status = 0;
    }

    /**
     * Returns true if this patient is equal to the given patient,
     * otherwise return false.
     *
     * @param o the patient to compare with
     * @return true if this patient is equal to o, otherwise return false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient p = (Patient) o;

        if (arrivalTime != null ? !arrivalTime.equals(p.arrivalTime) : p.arrivalTime != null)
            return false;
        if (birthDate != null ? !birthDate.equals(p.birthDate) : p.birthDate != null)
            return false;
        if (healthCardNumber != null ? !healthCardNumber.equals(p.healthCardNumber) : p.healthCardNumber != null)
            return false;
        if (name != null ? !name.equals(p.name) : p.name != null)
            return false;

        return true;
    }

    /**
     * Returns the hash value of this patient. If p1 and p2 are patients and
     * p1.equals(p2), then p1.hashCode() == p2.hashCode().
     *
     * @return the hash value of this patient
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (healthCardNumber != null ? healthCardNumber.hashCode() : 0);
        result = 31 * result + (arrivalTime != null ? arrivalTime.hashCode() : 0);
        return result;
    }

    /**
     * Returns a human-readable string representation of this patient.
     *
     * @return a human-readable string representation of this patient
     */
    @Override
    public String toString() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String format = "Patient{name=%s, birthDate=%s, " +
                        "healthCardNumber=%s, arrivalTime=%s";

        return String.format(format,
                             name,
                             dateFormat.format(birthDate.getTime()),
                             healthCardNumber,
                             timeFormat.format(arrivalTime.getTime()));
    }

    /**
     * Returns this patient's name.
     *
     * @return The name of this patient
     */
	public String getName() {
		return name;
	}

    /**
     * Sets a new name for this patient.
     *
     * @param name The new name of the patient
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Returns this patient's birthDate.
     *
     * @return The date of birth of this patient in dd/mm/yyyy format
     */
	public Calendar getBirthDate() {
		return birthDate;
	}

    /**
     * Sets birthDate for this patient.
     *
     * @param birthDate The new birth date of this patient
     */
	public void setBirthDate(Calendar birthDate) {
		this.birthDate = birthDate;
	}

    /**
     * Returns this patient's healthCardNumber.
     *
     * @return The health card number of this patient
     */
	public String getHealthCardNumber() {
		return healthCardNumber;
	}

    /**
     * Sets a new heatlhCardNumber for this patient.
     *
     * @param healthCardNumber The new health card number of the patient
     */
	public void setHealthCardNumber(String healthCardNumber) {
		this.healthCardNumber = healthCardNumber;
	}

    /**
     * Returns this patient's arrival time.
     *
     * @return this patient's arrival time
     */
    public Calendar getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(Calendar arrivalTime) {
    	this.arrivalTime = arrivalTime;
    }

    /**
     * Returns this patient's record of when she has been seen by a doctor.
     *
     * @return The record of when this patient was seen by a doctor
     */
	public List<Calendar> getTimesSeenByDoctor() {
        return new ArrayList<Calendar>(timesSeenByDoctor);
	}

    /**
     * Returns the latest time when this patient was seen by a doctor.
     *
     * @return the latest time when this patient was seen by a doctor
     */
    public Calendar getLastSeenByDoctor() {
        return timesSeenByDoctor.last();
    }

    /**
     * Returns true if this patient has been seen by a doctor,
     * otherwise return false.
     *
     * @return true if this patient has been seen by a doctor,
     * otherwise false
     */
    public boolean hasBeenSeenByDoctor() {
        try {
            getLastSeenByDoctor();
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns this patient's urgency based on age, temperature,
     * blood pressure and heart rate.
     *
     * @return The urgency of this patient
     */
	public Integer getUrgency() {
        int urgency = 0;

        if (this.getAge() < 2) {
            urgency++;
        }
    
        VitalSigns latestVitalSigns = vitalSigns.getLatestValue();
        if (latestVitalSigns != null) {
            if (latestVitalSigns.getTemperature() >= 39.0)
                urgency++;
            if (latestVitalSigns.getSystolicBloodPressure() >= 140 ||
                latestVitalSigns.getDiastolicBloodPressure() >= 90)
                urgency++;
            if (latestVitalSigns.getHeartRate() >= 100 ||
                latestVitalSigns.getHeartRate() <= 50)
                urgency++;
        }
        return urgency;
	}

    /**
     * Records a new instance of VitalSigns for this Patient at the current
     * date and time and adds it to this Patient's Map of VitalSigns.
     *
     * @param temperature the new temperature of this patient
     * @param systolic the new systolicBloodPressure blood pressure of this patient
     * @param diastolic the new diastolicBloodPressure blood pressure of this patient
     * @param heartRate the new heart rate of this patient
     */
	public void recordVitalSigns(double temperature, int systolic,
                                 int diastolic, int heartRate) {
        int previousUrgency = getUrgency();
		VitalSigns vitalsigns = new VitalSigns(temperature, systolic,
                                               diastolic, heartRate);
        this.vitalSigns.recordValue(vitalsigns);
        this.status = getUrgency().compareTo(previousUrgency);
	}

    /**
     * Records new symptoms for this patient.
     *
     * @param symptoms The new symptoms of this patient
     */
	public void recordSymptoms(String symptoms) {
        this.symptoms.recordValue(symptoms);
	}

    /**
     * Returns this patient's age based on their birthDate and the current
     * date.
     *
     * @return The age of this patient
     */
	public int getAge() {
        long nowInMillis = Calendar.getInstance().getTimeInMillis();
        long birthDateInMillis = birthDate.getTimeInMillis();
        long difference = nowInMillis - birthDateInMillis;
        long years = difference / 1000 / 60 / 60 / 24 / 365;
        return (int) years;
	}

    /**
     * Returns this patient's record of VitalSigns.
     *
     * @return The record of vital signs of this patient
     */
	public PatientRecord<VitalSigns> getVitalSigns() {
		return vitalSigns;
	}

    /**
     * Returns this patient's record of symptoms.
     *
     * @return The record of symptoms of this patient
     */
	public PatientRecord<String> getSymptoms() {
		return symptoms;
	}

    /**
     * Returns this patient's most recent VitalSigns.
     *
     * @return The most recent vital signs of this patient
     */
    public VitalSigns getLatestVitalSigns() {
        return vitalSigns.getLatestValue();
    }

    /**
     * Returns this patient's most recent symptoms.
     *
     * @return The most recent symptoms of this patient
     */
    public String getLatestSymptoms() {
        return symptoms.getLatestValue();
    }

    /**
     * Records the current date and time in this patient's record of doctor
     * visits.
     */
	public void addSeenByDoctor() {
        timesSeenByDoctor.add(Calendar.getInstance());
	}
	
	/**
	 * Adds a new prescription to this Patient at the current date and time.
	 * @param name The name of the medication
	 * @param instructions The instructions for taking the medication
	 */
	public void addPrescription(String name, String instructions)
	{
        List<String> prescription = new ArrayList<String>(2);
        prescription.add(name);
        prescription.add(instructions);
		this.prescriptions.recordValue(prescription);
	}
	
	/**
	 * Returns this Patient's record of prescriptions.
	 * @return The record of prescriptions of this patient
	 */
	public PatientRecord<List<String>> getPrescriptions()
	{
		return prescriptions;
	}

    /**
     * Returns a two-element list with the first element as the prescription
     * name, and the second as the instructions.
     *
     * @return a two-element list with the first element as the prescription
     * name, and the second as the instructions
     */
	public List<String> getLatestPrescription()
	{
		return prescriptions.getLatestValue();
	}

    /**
     * Returns -1 if this Patient's urgency is increasing; 0 if it is the same;
     * 1 if it is increasing.
     *
     * @return -1 if this Patient's urgency is increasing; 0 if it is the same;
     * 1 if it is increasing
     */
	public int getStatus() {
        return status;
    }

}
