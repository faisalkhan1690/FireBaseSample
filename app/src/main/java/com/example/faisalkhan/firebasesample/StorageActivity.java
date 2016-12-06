package com.example.faisalkhan.firebasesample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity class for Demonstration of Storage based on firebase.
 * <p>
 * For performing any operation you must create reference to firebase storage.
 * You can upload image on server with any auth but for download you must auth from firebase or you change setting in you firebase.
 * Firebase does not provide any list of image stored i firebase for that you have to store download link in Firebase Db
 * and for storing or fetching any data you must have auth.
 * <p>
 * FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
 * StorageReference storageRef = storage.getReferenceFromUrl(" Your firebase url");
 * <p>
 * By using these two line you can create reference.
 * And performing every operations i have created separate methods please refer those.
 * <p>
 * For more info you can follow this link :- https://firebase.google.com/docs/storage/android/start
 * and link :- https://firebase.google.com/docs/storage/android/create-reference
 * and link :- https://firebase.google.com/docs/storage/android/upload-files
 * and link :- https://firebase.google.com/docs/storage/android/download-files
 * and link :- https://firebase.google.com/docs/storage/android/delete-files
 * and link :- https://firebase.google.com/docs/storage/
 * Or link :- http://firebasesample.blogspot.in/
 *
 * @author Faisal Khan
 */
public class StorageActivity extends AppCompatActivity {

    private final String TAG = StorageActivity.class.getSimpleName();
    private TextView mTvImagePath;
    private String mImagePath = null;
    private List<GridItem> mListOfImages = new ArrayList<>();

    private AlertDialog.Builder mAlertDialog;
    private ImageGridAdapter mImageGridAdapter;
    private StorageReference mStorageReference;
    private DatabaseReference mRefDataBase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int RESULT_LOAD_IMAGE = 1;
    private final String TABLE_NAME = "images";
    private final String KEY_IMAGE_NAME = "name";

    private ProgressDialog mProgressDialog;
    private TextView mTvAuthStatus;
    private int mPosition;
    private GridView gvImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

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
                    fetchImagesFromServer();
                } else {
                    mTvAuthStatus.setText("For performing any operation you should sign in. Click here to sign in");
                    mTvAuthStatus.setTextColor(Color.RED);
                    mTvAuthStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(StorageActivity.this, Authentication.class));
                        }
                    });
                    Log.d(TAG, "User is not sign in");
                }
            }
        };

        mRefDataBase = FirebaseDatabase.getInstance().getReference(TABLE_NAME);


        //creating reference to firebase Storage
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = firebaseStorage.getReferenceFromUrl("gs://fir-sample-b203f.appspot.com/");

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

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);

        mTvAuthStatus = (TextView) findViewById(R.id.tv_authentication);
        mTvImagePath = (TextView) findViewById(R.id.tv_image_path);
        Button btnBrowser = (Button) findViewById(R.id.btn_browser);
        Button btnUpload = (Button) findViewById(R.id.btn_upload);
        gvImages = (GridView) findViewById(R.id.gv_images);
        mImageGridAdapter = new ImageGridAdapter(StorageActivity.this, mListOfImages);
        gvImages.setAdapter(mImageGridAdapter);


        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImageFromServer(mListOfImages.get(mPosition));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);


        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation())
                    uploadImageOnServer();
            }
        });

        gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAlertDialog.show();
                mPosition = position;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage == null) {
                Toast.makeText(this, "Something goes wrong please try after some time.", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mImagePath = cursor.getString(columnIndex);
            cursor.close();
            if (mImagePath == null) {
                Toast.makeText(this, "Something goes wrong please try after some time.", Toast.LENGTH_SHORT).show();
                return;
            }
            mTvImagePath.setText(mImagePath);
        }
    }

    /**
     * Method to check image path should't null or empty
     *
     * @return true if validation is ok else false
     */
    private boolean checkValidation() {
        if (mImagePath == null || mImagePath.equals("")) {
            Toast.makeText(this, "Please select an image from gallery by clicking on browser", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * Method to delete image form server
     *
     * @param mImagePath image path
     */
    public void deleteImageFromServer(final GridItem mImagePath) {
        showProgressDialog();

        // Create a reference to the file to delete
        StorageReference desertRef = mStorageReference.child("images/"+mImagePath.key+".jpg");
        desertRef.delete().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                deleteDataFromDataBase(mImagePath.key);
                Toast.makeText(StorageActivity.this, "Image delete successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                hideProgressDialog();
                Toast.makeText(StorageActivity.this, "Something goes wrong please try after some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method to delete data from firebase Data Base
     *
     * @param mImagePath imagePath
     */
    public void deleteDataFromDataBase(String mImagePath) {

        if (mImagePath == null || mImagePath.equals("")) {
            Log.e(TAG, "Invalid image path");
            hideProgressDialog();
            return;
        }
        mRefDataBase.child(mImagePath).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, databaseError.getMessage());
                    hideProgressDialog();
                } else {
                    Log.d(TAG, "Insert successfully");
                    hideProgressDialog();
                    fetchImagesFromServer();
                }
            }
        });
    }


    /**
     * Method to delete image form server
     */
    public void uploadImageOnServer() {

        showProgressDialog();

        // Create a reference to 'images/timeStamp'
        final String imageName = System.currentTimeMillis() + "";
        StorageReference storageRef = mStorageReference.child("images/" + System.currentTimeMillis() + ".jpg");
        Uri file = Uri.fromFile(new File(mImagePath));
        UploadTask uploadTask = storageRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                hideProgressDialog();
                Toast.makeText(StorageActivity.this, "Something goes wrong please try after some time.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl != null) {
                    setDataInToDataBase(imageName, downloadUrl.toString());
                    Toast.makeText(StorageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StorageActivity.this, "Something goes wrong please try after some time.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Method to fetch images form server
     */
    public void fetchImagesFromServer() {
        mListOfImages.clear();
        mRefDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }
                Map<String, String> serverData = (HashMap<String, String>) dataSnapshot.getValue();

                if (serverData == null) {
                    return;
                }
                mListOfImages = new ArrayList<>();
                for (Map.Entry image : serverData.entrySet()) {
                    if (image != null)
                        mListOfImages.add(new GridItem((String)image.getKey(),(String) image.getValue()));
                }
                Collections.reverse(mListOfImages);
                mImageGridAdapter = new ImageGridAdapter(StorageActivity.this, mListOfImages);
                gvImages.setAdapter(mImageGridAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Method to set value in firebase Data Base
     */
    public void setDataInToDataBase(String name, String imageUrl) {
        if (imageUrl == null || imageUrl.equals("")) {
            Log.e(TAG, "Can't insert in Database because path is null or empty");
            hideProgressDialog();
            return;
        }
        mRefDataBase.child(name).setValue(imageUrl, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
                if (databaseError != null)
                    Log.d(TAG, databaseError.toString());
                else
                    Log.d(TAG, "Image inserted successfully");

            }
        });
    }


    public class ImageGridAdapter extends BaseAdapter {
        private Context mContext;
        private List<GridItem> mListOfImages;

        public ImageGridAdapter(Context context, List<GridItem> listOfImages) {
            mContext = context;
            mListOfImages = listOfImages;
        }

        public int getCount() {
            return mListOfImages == null ? 0 : mListOfImages.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_image);
            Picasso.with(mContext).load(mListOfImages.get(position).value)
                    .placeholder(R.drawable.firebase_icon)
                    .error(R.drawable.firebase_icon)
                    .into(imageView);
            return imageView;

        }
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

    class GridItem{
        public String key;
        public String value;
        public GridItem(String key,String value){
            this.key=key;
            this.value=value;
        }
    }
}
