package com.example.itriage.models;

/**
 * An exception which is thrown when a patient is not found in an ER.
 */
public class PatientNotFoundException extends Exception {

    /**
     * Constructs this exception with no arguments.
     */
    public PatientNotFoundException() {
        super();
    }

    /**
     * Constructs this exception with the specified detail message.
     *
     * @param message the detail message for this exception
     */
    public PatientNotFoundException(String message) {
        super(message);
    }

}
