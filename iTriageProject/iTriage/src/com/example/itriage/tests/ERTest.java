package com.example.itriage.tests;

import com.example.itriage.models.ER;
import com.example.itriage.models.Patient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for ER.
 */
public class ERTest {

    // Reference to the ER instance
    private ER er;

    // Some test patients
    private Patient[] patients = new Patient[5];

    /**
     * Creates a reference to the ER and initializes some test patients
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        er = ER.getInstance();

        Calendar[] birthDates = new Calendar[5];
        birthDates[0] = new GregorianCalendar(1994, 6, 8);
        birthDates[1] = new GregorianCalendar(1996, 8, 12);
        birthDates[2] = new GregorianCalendar(2012, 4, 20);
        birthDates[3] = new GregorianCalendar(1960, 8, 3);
        birthDates[4] = new GregorianCalendar(1963, 1, 19);

        Calendar now = Calendar.getInstance();

        patients[0] = new Patient("Bob", birthDates[0], "QAZ", (Calendar) now.clone());
        patients[1] = new Patient("Kim", birthDates[1], "WSX", (Calendar) now.clone());
        patients[2] = new Patient("Eli", birthDates[2], "EDC", (Calendar) now.clone());
        patients[3] = new Patient("Pat", birthDates[3], "RFV", (Calendar) now.clone());
        patients[4] = new Patient("Joe", birthDates[4], "TGB", (Calendar) now.clone());
    }

    /**
     * Clears the List in ER which stores the patients. This is necessary since
     * ER is a singleton; its instance cannot be destroyed, so we must manually
     * clear its list
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        er.getAllPatientsByName().clear();
    }

    /**
     * Tests addPatient(String name, Calendar birthDate,
     *                  String healthCardNumber, Calendar arrivalTime)
     *
     * @throws Exception
     */
    @Test
    public void testAddPatientWithParameters() throws Exception {
        String name;
        Calendar birthDate;
        String healthCardNumber;
        Calendar arrivalTime;

        name = patients[0].getName();
        birthDate = patients[0].getBirthDate();
        healthCardNumber = patients[0].getHealthCardNumber();
        arrivalTime = patients[0].getArrivalTime();

        er.addPatient(name, birthDate, healthCardNumber, arrivalTime);

        name = patients[1].getName();
        birthDate = patients[1].getBirthDate();
        healthCardNumber = patients[1].getHealthCardNumber();
        arrivalTime = patients[1].getArrivalTime();

        er.addPatient(name, birthDate, healthCardNumber, arrivalTime);

        name = patients[1].getName();
        birthDate = patients[1].getBirthDate();
        healthCardNumber = patients[1].getHealthCardNumber();
        arrivalTime = patients[1].getArrivalTime();

        er.addPatient(name, birthDate, healthCardNumber, arrivalTime);

        name = patients[1].getName();
        birthDate = patients[1].getBirthDate();
        healthCardNumber = patients[1].getHealthCardNumber();
        arrivalTime = patients[1].getArrivalTime();

        er.addPatient(name, birthDate, healthCardNumber, arrivalTime);

        name = patients[1].getName();
        birthDate = patients[1].getBirthDate();
        healthCardNumber = patients[1].getHealthCardNumber();
        arrivalTime = patients[1].getArrivalTime();

        er.addPatient(name, birthDate, healthCardNumber, arrivalTime);
    }

    /**
     * Tests addPatient(Patient patient)
     *
     * @throws Exception
     */
    @Test
    public void testAddPatientWithObject() throws Exception {
        er.addPatient(patients[0]);
        er.addPatient(patients[1]);
        er.addPatient(patients[2]);
        er.addPatient(patients[3]);
        er.addPatient(patients[4]);
    }

    /**
     * Tests getAllPatientsByName()
     *
     * @throws Exception
     */
    @Test
    public void testGetAllPatientsByName() throws Exception {
        // A list of all patients, sorted in descending order by urgency
        List<Patient> allPatientsByName;

        // Add the test patients
        er.addPatient(patients[0]);
        er.addPatient(patients[1]);
        er.addPatient(patients[2]);
        er.addPatient(patients[3]);
        er.addPatient(patients[4]);

        // Call getAllPatientsByName()
        allPatientsByName = er.getAllPatientsByName();

        // The list should contain all of the test patients
        assertTrue(allPatientsByName.contains(patients[0]));
        assertTrue(allPatientsByName.contains(patients[1]));
        assertTrue(allPatientsByName.contains(patients[2]));
        assertTrue(allPatientsByName.contains(patients[3]));
        assertTrue(allPatientsByName.contains(patients[4]));

        /*
         * patients[0] "Bob" should be first in the list (index 0)
         * patients[1] "Kim" should be fourth in the list (index 3)
         * patients[2] "Eli" should be second in the list (index 1)
         * patients[3] "Pat" should be fifth in the list (index 4)
         * patients[4] "Joe" should be third in the list (index 2)
         */
        assertEquals(patients[0], allPatientsByName.get(0));
        assertEquals(patients[1], allPatientsByName.get(3));
        assertEquals(patients[2], allPatientsByName.get(1));
        assertEquals(patients[3], allPatientsByName.get(4));
        assertEquals(patients[4], allPatientsByName.get(2));
    }

    /**
     * Tests getWaitingPatientsByUrgency()
     *
     * @throws Exception
     */
    @Test
    public void testGetWaitingPatientsByUrgency() throws Exception {
        // A list of waiting patients, sorted in descending order by urgency
        List<Patient> waitingPatientsByUrgency;

        // patients[1] and patients[3] have seen a doctor; they are not waiting
        patients[1].addSeenByDoctor();
        patients[3].addSeenByDoctor();

        // Expected urgency for patients[0]: 2
        patients[0].recordVitalSigns(40, 100, 50, 70);

        // Expected urgency for patients[0]: 4
        patients[2].recordVitalSigns(40, 100, 100, 70);

        // Expected urgency for patients[4]: 3
        patients[4].recordVitalSigns(35, 150, 50, 40);

        // Add the test patients
        er.addPatient(patients[0]);
        er.addPatient(patients[1]);
        er.addPatient(patients[2]);
        er.addPatient(patients[3]);
        er.addPatient(patients[4]);

        // Call getWaitingPatientsByUrgency()
        waitingPatientsByUrgency = er.getWaitingPatientsByUrgency();

        // patients[1] and patients[3] are not expected to be waiting
        assertFalse(waitingPatientsByUrgency.contains(patients[1]));
        assertFalse(waitingPatientsByUrgency.contains(patients[3]));

        // patients[0], patients[2], patients[4] are expected to be waiting
        assertTrue(waitingPatientsByUrgency.contains(patients[0]));
        assertTrue(waitingPatientsByUrgency.contains(patients[2]));
        assertTrue(waitingPatientsByUrgency.contains(patients[4]));

        // The list should be sorted by urgency in descending order
        assertEquals(4, (int) waitingPatientsByUrgency.get(0).getUrgency());
        assertEquals(3, (int) waitingPatientsByUrgency.get(1).getUrgency());
        assertEquals(2, (int) waitingPatientsByUrgency.get(2).getUrgency());
    }

    @Test
    public void testGetPatientByHealthCardNumber() throws Exception {
        // Store references to the test patients' health card numbers
        String[] healthCardNumbers = new String[5];
        healthCardNumbers[0] = patients[0].getHealthCardNumber();
        healthCardNumbers[1] = patients[1].getHealthCardNumber();
        healthCardNumbers[2] = patients[2].getHealthCardNumber();
        healthCardNumbers[3] = patients[3].getHealthCardNumber();
        healthCardNumbers[4] = patients[4].getHealthCardNumber();

        // Add the test patients
        er.addPatient(patients[0]);
        er.addPatient(patients[1]);
        er.addPatient(patients[2]);
        er.addPatient(patients[3]);
        er.addPatient(patients[4]);

        // Each test patient should be equal to the patient retrived
        assertEquals(patients[0], er.getPatient(healthCardNumbers[0]));
        assertEquals(patients[1], er.getPatient(healthCardNumbers[1]));
        assertEquals(patients[2], er.getPatient(healthCardNumbers[2]));
        assertEquals(patients[3], er.getPatient(healthCardNumbers[3]));
        assertEquals(patients[4], er.getPatient(healthCardNumbers[4]));
    }

    @Test
    public void testSaveToStreamLoadFromStream() throws Exception {
        // Create an output stream
        OutputStream outputStream = new ByteArrayOutputStream();

        // Add the test patients
        er.addPatient(patients[0]);
        er.addPatient(patients[1]);
        er.addPatient(patients[2]);
        er.addPatient(patients[3]);
        er.addPatient(patients[4]);

        // Save the ER to the stream
        er.saveToStream(outputStream);

        // Store a reference to the string output (simulate saving to a file)
        String output = outputStream.toString();


        // Create an input stream from the saved string
        InputStream inputStream = new ByteArrayInputStream(output.getBytes());

        // Load data from the input stream
        er.loadFromStream(inputStream);

        // Get all patients from the ER
        List<Patient> patientList = er.getAllPatientsByName();

        // The list should contain all of the test patients
        assertTrue(patientList.contains(patients[0]));
        assertTrue(patientList.contains(patients[1]));
        assertTrue(patientList.contains(patients[2]));
        assertTrue(patientList.contains(patients[3]));
        assertTrue(patientList.contains(patients[4]));

        /*
         * patients[0] "Bob" should be first in the list (index 0)
         * patients[1] "Kim" should be fourth in the list (index 3)
         * patients[2] "Eli" should be second in the list (index 1)
         * patients[3] "Pat" should be fifth in the list (index 4)
         * patients[4] "Joe" should be third in the list (index 2)
         */
        assertEquals(patients[0], patientList.get(0));
        assertEquals(patients[1], patientList.get(3));
        assertEquals(patients[2], patientList.get(1));
        assertEquals(patients[3], patientList.get(4));
        assertEquals(patients[4], patientList.get(2));
    }

}
