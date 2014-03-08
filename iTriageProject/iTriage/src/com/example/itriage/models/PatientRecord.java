package com.example.itriage.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;

/**
 * A generic container which holds the current and past values of
 * a patient record.
 */
public class PatientRecord<T> {

    /**
     * A map of current and past values of this patient record, with
     * Calendar (Date/time recorded) -> T (value)
     *
     * Since this is a TreeMap,
     * the map is sorted by its keys, making it easy to retrieve the
     * first (earliest) and last (latest) values.
     */
    private TreeMap<Calendar, T> values;

    /**
     * Constructs a new PatientRecord
     */
    public PatientRecord() {
        values = new TreeMap<Calendar, T>();
    }

    /**
     * Constructs a new PatientRecord with a given value
     *
     * @param value the value to add
     */
    public PatientRecord(T value) {
        recordValue(value);
    }

    /**
     * Records a new data point to the PatientRecord
     *
     * @param value the value of type T that will be recorded to the current Record
     */
    public void recordValue(T value) {
        values.put(Calendar.getInstance(), value);
    }

    /**
     * Returns the entire PatientHistory
     *
     * @return returns the entire PatientHistory of this patient
     */
    public TreeMap<Calendar, T> getHistory() {
        return values;
    }

    /**
     * Returns an individual point from the PatientHistory
     *
     * @param date the date that is being used to search for a certain value.
     * @return returns the desired value searched by the Calendar object
     */
    public T getValue(Calendar date) {
        return values.get(date);
    }

    /**
     * Returns the latest recorded value from this PatientRecord
     *
     * @return returns the latest value recorded in the PatientRecord.
     */
    public T getLatestValue() {
        return !values.isEmpty() ? values.lastEntry().getValue() : null;
    }

}
