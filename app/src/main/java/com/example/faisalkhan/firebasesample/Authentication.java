package com.example.faisalkhan.firebasesample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity class for Demonstration of Email base user Auth from firebase.
 *
 * Firebase provide  Google+ ,Facebook ,Twitter etc based authentication. and with this it provides email based Authentication.
 *
 * For doing any operation you first need to FirebaseAuth instance and AuthStateListener instance. and after that set AuthStateListener
 * listener to firebase instance.
 *
 * Now you can do Sign Up , login and Logout.
 *
 * To know how you can configure Firebase follow link :- https://firebase.google.com/docs/android/setup
 * Or you can flow my doc as well link :- http://firebasesample.blogspot.in/
 *
 * Email based Authentication
 * Or you can also follow my link :- https://firebase.google.com/docs/auth/
 *
 * Or :- https://firebase.google.com/docs/auth/android/password-auth
 *
 *
 * @author Faisal Khan
 */
public class Authentication extends AppCompatActivity implements View.OnClickListener {


    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object

    private ProgressDialog mProgressDialog;
    private final String TAG=Authentication.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        //setting action bar
        getActionBar().setTitle("Authentication");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mDetailTextView = (TextView) findViewById(R.id.detail);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG,"User is Already sign in");
                    Toast.makeText(Authentication.this, "User is Already sign in.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG,"User is not sign in");
                }
                updateUI(user);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        //setting auth listener for Authentication object
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //removing auth listener for Authentication object
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Method to create account on Firebase
     * @param email or user id of user
     * @param password password
     */
    private void createAccount(String email, String password) {
        Toast.makeText(Authentication.this, "creating account with :" + email, Toast.LENGTH_SHORT).show();
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.d(TAG,"Create Account process complete with false ");
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Sign Up Successfully");
                            Toast.makeText(Authentication.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d(TAG,"Sign Up Failed");
                            Toast.makeText(Authentication.this, "Sign Up Failed.", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    /**
     * Method to show progress dialog
     */
    private void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    /**
     * Method to sign in with user id and pasword
     * @param email  email of user
     * @param password passworrd of user
     */
    private void signIn(String email, String password) {
        Toast.makeText(Authentication.this, "Signing in with account :" + email, Toast.LENGTH_SHORT).show();
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Sign in Successfully");
                            Toast.makeText(Authentication.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d(TAG,"Sign in Failed");
                            Toast.makeText(Authentication.this, "Sign Up Failed.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"Sign in Failed with error :"+task.getException());
                            mStatusTextView.setText("Auth failed");
                        }
                        hideProgressDialog();
                    }
                });
    }

    /**
     * Method to hide progress dialog
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    /**
     * Method to sign out
     */
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    /**
     * Method to check all validation is success
     * @return true is validation true else false
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    /**
     * Method to change UI components after an operation
     * @param user user object
     */
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }

}
