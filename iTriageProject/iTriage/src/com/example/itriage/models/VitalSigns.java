package com.example.itriage.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Vital signs of a patient
 */
public class VitalSigns {

    // This Patient's temperature
    private double temperature;

    // This Patient's Systolic blood pressure
    private int systolicBloodPressure;

    // This Patient's Diastolic blood pressure
    private int diastolicBloodPressure;

    // This Patient's heartRate in beats per minute (bpm)
    private int heartRate;

    /** Constructs a new VitalSigns object for a patient
     *
     * @param temperature the temperature of this Patient
     * @param systolicBloodPressure the systolicBloodPressure blood pressure of this Patient
     * @param diastolicBloodPressure the diastolicBloodPressure blood pressure of this Patient
     * @param heartRate the heartRate of this patient
     */
    public VitalSigns(double temperature, int systolicBloodPressure,
                      int diastolicBloodPressure, int heartRate) {
        this.temperature = temperature;
        this.systolicBloodPressure = systolicBloodPressure;
        this.diastolicBloodPressure = diastolicBloodPressure;
        this.heartRate = heartRate;

    }

    /**
     * Returns the temperature component of these vital signs.
     *
     * @return the temperature component of these vital signs
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Returns the systolic blood pressure component of these vital signs.
     *
     * @return the systolic blood pressure component of these vital signs
     */
    public double getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    /**
     * Returns the diastolic blood pressure component of these vital signs.
     *
     * @return the diastolic blood pressure component of these vital signs
     */
    public double getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    /**
     * Returns the heart rate component of these vital signs.
     *
     * @return the heart rate component of these vital signs
     */
    public double getHeartRate() {
        return heartRate;
    }

}
