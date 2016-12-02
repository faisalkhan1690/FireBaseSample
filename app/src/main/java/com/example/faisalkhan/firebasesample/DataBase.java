package com.example.faisalkhan.firebasesample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Activity class for Demonstration of Database based on firebase.
 * <p>
 * <p>
 * <p>
 * To know how you can configure Firebase follow link :- https://firebase.google.com/docs/android/setup
 * Or you can flow my doc as well link :- http://firebasesample.blogspot.in/
 * For more info you can follow this link :- https://firebase.google.com/docs/database/
 * Or link :-
 * Or link :- https://firebase.google.com/docs/database/android/start/
 *
 * @author Faisal Khan
 */
public class DataBase extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = DataBase.class.getSimpleName();
    private DatabaseReference mRefDataBase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @SuppressWarnings("all")
    private final String TABLE_NAME = "test_table";

    private final String KEY_ID = "id";
    private final String KEY_NAME = "name";
    private final String KEY_CITY = "city";
    private final String KEY_COUNTRY = "country";

    private EditText mEtId;
    private EditText mEtName;
    private EditText mEtCity;
    private EditText mEtCountry;

    private TextView mTvAuthStatus;
    private ProgressDialog mProgressDialog;
    private ListView mLvData;
    private Button mBtnSave;
    private View mBtnCancel;
    private TextView mTvId;
    private TextView mTvName;
    private TextView mTvCity;
    private TextView mTvCountry;
    private View mLlButton;
    private Operations mOperations = Operations.CANCEL;
    private ArrayList<UserData> mListData;

    private enum Operations {
        INSERT, DELETE, EDIT, CANCEL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        //setting action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("DataBase");
        initUi();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mTvAuthStatus.setText("You are sign in Enjoy");
                    mTvAuthStatus.setTextColor(Color.GREEN);
                    mTvAuthStatus.setOnClickListener(null);
                    Log.d(TAG, "User is sign in");
                    getDatFromDataBase();
                } else {
                    mTvAuthStatus.setText("For performing any operation you should sign in. Click here to sign in");
                    mTvAuthStatus.setTextColor(Color.RED);
                    mTvAuthStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(DataBase.this, Authentication.class));
                        }
                    });
                    Log.d(TAG, "User is not sign in");
                }
            }
        };

        mRefDataBase = FirebaseDatabase.getInstance().getReference(TABLE_NAME);

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
     * Method to initialize UI components
     */
    private void initUi() {
        mTvAuthStatus = (TextView) findViewById(R.id.tv_authentication);
        mLvData = (ListView) findViewById(R.id.lv_data);
        mLlButton = findViewById(R.id.ll_buttons);

        mEtId = (EditText) findViewById(R.id.et_id);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtCity = (EditText) findViewById(R.id.et_city);
        mEtCountry = (EditText) findViewById(R.id.et_country);

        mTvId = (TextView) findViewById(R.id.tv_id_text);
        mTvName = (TextView) findViewById(R.id.tv_name_text);
        mTvCity = (TextView) findViewById(R.id.tv_city_text);
        mTvCountry = (TextView) findViewById(R.id.tv_country_text);


        View btnInsert = findViewById(R.id.btn_insert);
        View btnDelete = findViewById(R.id.btn_delete);
        View btnEdit = findViewById(R.id.btn_edit);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnSave = (Button) findViewById(R.id.btn_save);

        btnInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);

        mLvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserData userData = mListData.get(position);
                mEtId.setText(userData.id);
                mEtName.setText(userData.name);
                mEtCity.setText(userData.city);
                mEtCountry.setText(userData.country);

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_insert:
                mOperations = Operations.INSERT;

                mTvId.setVisibility(View.VISIBLE);
                mEtId.setVisibility(View.VISIBLE);
                mTvName.setVisibility(View.VISIBLE);
                mEtName.setVisibility(View.VISIBLE);
                mTvCity.setVisibility(View.VISIBLE);
                mEtCity.setVisibility(View.VISIBLE);
                mTvCountry.setVisibility(View.VISIBLE);
                mEtCountry.setVisibility(View.VISIBLE);
                mLlButton.setVisibility(View.GONE);

                mEtId.setText("");
                mEtName.setText("");
                mEtCity.setText("");
                mEtCountry.setText("");
                mEtId.setEnabled(true);

                mBtnCancel.setVisibility(View.VISIBLE);
                mBtnSave.setVisibility(View.VISIBLE);
                mBtnSave.setText("Insert");
                break;

            case R.id.btn_delete:
                mOperations = Operations.DELETE;

                mTvId.setVisibility(View.VISIBLE);
                mEtId.setVisibility(View.VISIBLE);
                mTvName.setVisibility(View.GONE);
                mEtName.setVisibility(View.GONE);
                mTvCity.setVisibility(View.GONE);
                mEtCity.setVisibility(View.GONE);
                mTvCountry.setVisibility(View.GONE);
                mEtCountry.setVisibility(View.GONE);
                mLlButton.setVisibility(View.GONE);

                mEtId.setText("");
                mEtName.setText("");
                mEtCity.setText("");
                mEtCountry.setText("");
                mEtId.setEnabled(true);

                mBtnCancel.setVisibility(View.VISIBLE);
                mBtnSave.setVisibility(View.VISIBLE);
                mBtnSave.setText("Delete");
                break;

            case R.id.btn_edit:
                mOperations = Operations.EDIT;

                mTvId.setVisibility(View.VISIBLE);
                mEtId.setVisibility(View.VISIBLE);
                mTvName.setVisibility(View.VISIBLE);
                mEtName.setVisibility(View.VISIBLE);
                mTvCity.setVisibility(View.VISIBLE);
                mEtCity.setVisibility(View.VISIBLE);
                mTvCountry.setVisibility(View.VISIBLE);
                mEtCountry.setVisibility(View.VISIBLE);
                mLlButton.setVisibility(View.GONE);

                mEtId.setText("");
                mEtName.setText("");
                mEtCity.setText("");
                mEtCountry.setText("");
                mEtId.setEnabled(false);

                mBtnCancel.setVisibility(View.VISIBLE);
                mBtnSave.setVisibility(View.VISIBLE);
                mBtnSave.setText("Update");
                break;

            case R.id.btn_cancel:
                mOperations = Operations.CANCEL;
                mEtId.setEnabled(false);
                setInitial();
                break;

            case R.id.btn_save:
                saveClicked();
                break;

            default:
                Log.e(TAG, "Case is not implemented");
        }

    }

    /**
     * Method to perform operation insert, delete , update.
     */
    private void saveClicked() {
        switch (mOperations) {
            case INSERT:
                setDataInToDataBase();
                break;

            case EDIT:
                setDataInToDataBase();
                break;

            case DELETE:
                deleteDataFromDataBase();
                break;

            default:
        }

        setInitial();
    }

    /**
     * set view in his initial state
     */
    private void setInitial() {

        mTvId.setVisibility(View.GONE);
        mEtId.setVisibility(View.GONE);
        mEtId.setText("");
        mTvName.setVisibility(View.GONE);
        mEtName.setVisibility(View.GONE);
        mEtName.setText("");
        mTvCity.setVisibility(View.GONE);
        mEtCity.setVisibility(View.GONE);
        mEtCity.setText("");
        mTvCountry.setVisibility(View.GONE);
        mEtCountry.setVisibility(View.GONE);
        mEtCountry.setText("");
        mEtId.setEnabled(true);

        mBtnSave.setVisibility(View.GONE);
        mBtnCancel.setVisibility(View.GONE);
        mLlButton.setVisibility(View.VISIBLE);
        mOperations = Operations.CANCEL;
    }

    /**
     * Method to show progress dialog
     */
    private void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    /**
     * Method to hide progress dialog
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


    /**
     * Method to set value in Data Base
     */
    public void setDataInToDataBase() {

        String id = mEtId.getText().toString().trim();
        String name = mEtName.getText().toString().trim();
        String city = mEtCity.getText().toString().trim();
        String country = mEtCountry.getText().toString().trim();

        if (id.equals("")) {
            Toast.makeText(DataBase.this, "Please enter a id", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.equals("")) {
            Toast.makeText(DataBase.this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (city.equals("")) {
            Toast.makeText(DataBase.this, "Please fill a City", Toast.LENGTH_SHORT).show();
            return;
        }

        if (country.equals("")) {
            Toast.makeText(DataBase.this, "Please enter an country", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();
        mRefDataBase.child(id).setValue(new UserData(id, name, city, country), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
                if (databaseError != null)
                    Toast.makeText(DataBase.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    setInitial();
                    Toast.makeText(DataBase.this, "Insert successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method to delete data from Database
     */
    public void deleteDataFromDataBase() {
        showProgressDialog();
        String id = mEtId.getText().toString().trim();

        if (id.equals("")) {
            Toast.makeText(DataBase.this, "Please enter a id", Toast.LENGTH_SHORT).show();
            return;
        }
        mRefDataBase.child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(DataBase.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                } else {
                    mEtId.setText("");
                    mEtName.setText("");
                    mEtCity.setText("");
                    mEtCountry.setText("");
                    Toast.makeText(DataBase.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    getDatFromDataBase();
                }
            }
        });
    }

    /**
     * Method to set value in Data Base
     */
    public void getDatFromDataBase() {
        mRefDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }
                ArrayList<Map<String, String>> serverData = (ArrayList<Map<String, String>>) dataSnapshot.getValue();

                if (serverData == null) {
                    return;
                }
                mListData = new ArrayList<>();
                for (Map<String, String>  map:serverData) {
                    if (map != null)
                        mListData.add(new UserData(map.get(KEY_ID), map.get(KEY_NAME), map.get(KEY_CITY), map.get(KEY_COUNTRY)));
                }
                Collections.reverse(mListData);
                mLvData.setAdapter(new ListAdapter(DataBase.this, R.layout.list_view_item, mListData));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public class UserData {
        public String id;
        public String name;
        public String city;
        public String country;

        UserData(String id, String name, String city, String country) {
            this.id = id;
            this.name = name;
            this.city = city;
            this.country = country;
        }
    }

    public class ListAdapter extends ArrayAdapter<UserData> {
        public ListAdapter(Context context, int resource, List<UserData> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserData user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
            }
            TextView tvId = (TextView) convertView.findViewById(R.id.tv_id);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tvCity = (TextView) convertView.findViewById(R.id.tv_city);
            TextView tvCountry = (TextView) convertView.findViewById(R.id.tv_country);

            tvId.setText(user != null ? user.id : "");
            tvName.setText(user != null ? user.name : "");
            tvCity.setText(user != null ? user.city : "");
            tvCountry.setText(user != null ? user.country : "");

            return convertView;
        }
    }

}
