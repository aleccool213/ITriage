package com.example.itriage.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.example.itriage.R;

import com.example.itriage.models.UserManager;

import java.io.*;

/**
 * Activity which displays a login screen to the user.
 */
public class LoginActivity extends Activity {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create UI references.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        // Listen for the 'login' event from an input method editor, e.g. 'Sign in' on a keyboard
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                boolean handled = false;
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    handled = true;
                }
                return handled;
            }
        });

        // Listen for when the 'Sign in' button is pressed.
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Add button listener
        mRegisterButton = (Button) findViewById(R.id.new_user_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Load the credentials from file
        String fileName = getString(R.string.credentials_filename);
        UserManager um = UserManager.getInstance();
        loadCredentialsFromFile(fileName);
    	
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for valid fields.
        if (TextUtils.isEmpty(username)) {
            String error = getString(R.string.error_field_required);
            mUsernameView.setError(error);
            focusView = mUsernameView;
            cancel = true;
        } else if (!um.isUsernameValid(username)) {
            String error = getString(R.string.error_invalid_username);
            mUsernameView.setError(error);
            focusView = mUsernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            String error = getString(R.string.error_field_required);
            mPasswordView.setError(error);
            focusView = mPasswordView;
            cancel = true;
        } else if (!um.isPasswordValid(username, password)) {
            String error = getString(R.string.error_incorrect_password);
            mPasswordView.setError(error);
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Login with valid credentials
            loginAsUser(username);
        }
    }

    /**
     * Log into iTriage with a valid username and password.
     * @param username a valid username
     */
    public void loginAsUser(String username) {
        // Set the logged-in user
        UserManager.getInstance().setCurrentUser(username);

        // Open ListPatients activity
        Intent intent = new Intent(this, PatientListActivity.class);
        startActivity(intent);

        // Finish LoginActivity to prevent it from remaining in the back stack
        finish();
    }

    /**
     * Returns a map of username -> password
     *
     * @param fileName name of the file to read from
     * @return a map of username -> password
     */
    private void loadCredentialsFromFile(String fileName) {
        String[] defaultCredentials = getResources().getStringArray(R.array.credentials);
        UserManager um = UserManager.getInstance();
        try {
            InputStream fis = openFileInput(fileName);
            try {
                um.loadCredentials(fis);
            } finally {
                fis.close();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            try {
                OutputStream os = openFileOutput(fileName, Context.MODE_PRIVATE);
                try {
                    um.createNewCredentialsFile(os, defaultCredentials);
                    loadCredentialsFromFile(fileName);
                } catch (IOException e) {
                    String format = "Passwords file %s cannot be created.";
                    showFileErrorToast(String.format(format, fileName));
                } finally {
                    os.close();
                }
            } catch (IOException e) {
                String format = "Passwords file %s cannot be created.";
                showFileErrorToast(String.format(format, fileName));
            }
        } catch (IOException ioException) {
            String format = "Passwords file %s could not be read.";
            showFileErrorToast(String.format(format, fileName));
        }
    }

    /**
     * Shows a Toast with the specified message about a file error.
     *
     * @param message a message about a file error
     */
    private void showFileErrorToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Opens the "register" dialog
     */
    public void showRegisterDialog() {
        final EditText registerName;
        final EditText registerPassword;

        // Password needed to create a new user account
        final EditText adminPassword;

        // Open a dialogue that prompts the user for certain information
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_login_dialogue);
        dialog.setTitle("Register");

        // Set custom components
        registerName = (EditText) dialog.findViewById(R.id.register_username);
        registerPassword = (EditText) dialog.findViewById(R.id.register_password);
        adminPassword = (EditText) dialog.findViewById(R.id.admin_password);


        // if the "submit" button is selected
        Button dialogSubmit = (Button) dialog.findViewById(R.id.register_submit);
        dialogSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check that admin password is correct
                String passwordAttempt = adminPassword.getText().toString();
                if (passwordAttempt.equals(getString(R.string.admin_password))) {

                    // Create a new user
                    boolean isPhysician = ((CheckBox) dialog.findViewById(R.id.register_physician)).isChecked();
                    String newUsername = registerName.getText().toString();
                    String newPassword = registerPassword.getText().toString();

                    try {
                        OutputStream os = openFileOutput("passwords.txt", MODE_APPEND);
                        UserManager.getInstance().addCredentialsToFile(os, isPhysician, newUsername, newPassword);

                        mUsernameView.setText(newUsername);
                        mPasswordView.setText(newPassword);

                        attemptLogin();
                        dialog.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    // In the case that the admin password is incorrect
                    showFileErrorToast("Incorrect Admin Password");
                }
            }
        });

        // if the "cancel" button is selected
        Button dialogCancel = (Button) dialog.findViewById(R.id
                .register_cancel);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
