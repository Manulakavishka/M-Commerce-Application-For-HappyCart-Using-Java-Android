package lk.jiat.app.happycart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lk.jiat.app.happycart.Model.Address;
import lk.jiat.app.happycart.Model.User;

public class NewAddressActivity extends AppCompatActivity {

    private Spinner spinnerProvince,spinnerDistrict,spinnerArea,spinnerAddressType;
    private EditText fullNameEditText,mobileEditText,addressEditText,landMarkEditText;
    private FirebaseFirestore firestore;
    public static final String TAG = NewAddressActivity.class.getName();
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;
    ArrayAdapter<String> adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        Bundle bundle = getIntent().getExtras();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // User is signed in
            userId = user.getUid();
            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId=null;
            // User is not signed in
            // Handle the situation accordingly
        }

        firestore=FirebaseFirestore.getInstance();
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerArea = findViewById(R.id.spinnerArea);
        spinnerAddressType = findViewById(R.id.spinnerAddressType);
        fullNameEditText = findViewById(R.id.editTextFullName);
        mobileEditText = findViewById(R.id.editTextMobileNumber);
        addressEditText = findViewById(R.id.editTextAddress);
        landMarkEditText = findViewById(R.id.editTextLandmark);

        loadProvince();

        loadDistrict();
        loadArea();
        loadAddressType();

        if (bundle != null) {
            String id = bundle.getString("id");
            loadData(id);
            findViewById(R.id.buttonSaveAddress).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData(id);
                }
            });

            // Now you have the "id" value, you can use it as needed
            // For example, you can log it
            Log.d("BundleExample", "Retrieved id: " + id);
        } else {
            Log.e("BundleExample", "Bundle is null");
            findViewById(R.id.buttonSaveAddress).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData(null);
                }
            });

        }


    }
    private void loadData(String id){
        // Specify the collection and document ID
        String collectionPath = "Addresses";

        DocumentReference docRef = firestore.collection(collectionPath).document(userId).collection("user").document(id);

        Source source = Source.DEFAULT;

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        fullNameEditText.setText((CharSequence) document.get("fullName"));
                        mobileEditText.setText((CharSequence) document.get("mobileNumber"));
                        addressEditText.setText((CharSequence) document.get("address"));
                        landMarkEditText.setText((CharSequence) document.get("landMark"));


                        loadSpinners("province",document);
                        loadSpinners("addressType",document);
                        loadSpinners("area",document);
                        loadSpinners("district",document);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void loadSpinners(String name,DocumentSnapshot document){


        if(name.equals("province")){
            // Apply the adapter to the spinner
            adapter = (ArrayAdapter<String>) spinnerProvince.getAdapter();

            if (adapter != null) {
                // Iterate through the adapter to find the index of the item
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).equals(document.get(name))) {
                        // Set the selection
                        spinnerProvince.setSelection(i);
                        break;
                    }
                }
            }

        }else if(name.equals("addressType")){
            // Apply the adapter to the spinner
            adapter = (ArrayAdapter<String>) spinnerAddressType.getAdapter();

            if (adapter != null) {
                // Iterate through the adapter to find the index of the item
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).equals(document.get(name))) {
                        // Set the selection
                        spinnerAddressType.setSelection(i);
                        break;
                    }
                }
            }

        }else if(name.equals("area")){
            // Apply the adapter to the spinner
            adapter = (ArrayAdapter<String>) spinnerArea.getAdapter();

            if (adapter != null) {
                // Iterate through the adapter to find the index of the item
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).equals(document.get(name))) {
                        // Set the selection
                        spinnerArea.setSelection(i);
                        break;
                    }
                }
            }

        }else if(name.equals("district")){
            // Apply the adapter to the spinner
            adapter = (ArrayAdapter<String>) spinnerDistrict.getAdapter();

            if (adapter != null) {
                // Iterate through the adapter to find the index of the item
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).equals(document.get(name))) {
                        // Set the selection
                        spinnerDistrict.setSelection(i);
                        break;
                    }
                }
            }

        }



    }

    private void saveData(String id){
        // Initialize UI elements

        String fullName = fullNameEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String landmark = landMarkEditText.getText().toString();
        String provice = spinnerProvince.getSelectedItem().toString();
        String district = spinnerDistrict.getSelectedItem().toString();
        String area = spinnerArea.getSelectedItem().toString();
        String addtressType = spinnerAddressType.getSelectedItem().toString();


        Address user1 = new Address(fullName,mobile,provice,district,area,address,landmark,addtressType);

        if(id.equals(null)){
            firestore.collection("Addresses").document(userId).collection("user").document().set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(NewAddressActivity.this, "Success",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(NewAddressActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else {
            firestore.collection("Addresses").document(userId).collection("user").document(id).set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(NewAddressActivity.this, "Update",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(NewAddressActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }



    }

    private void loadAddressType()  {
        ArrayList<String> Options = new ArrayList<>();
        CollectionReference provinceRef = firestore.collection("AddressType");
        provinceRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData()+" : "+ document.getString("name"));
                                String documentId = document.getString("name");
                                Options.add(documentId);
                            }
//                             Create ArrayAdapter with custom style
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(NewAddressActivity.this, R.layout.spinner_item, Options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply adapter to spinner
                            spinnerAddressType.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void loadArea()  {
        ArrayList<String> Options = new ArrayList<>();
        CollectionReference provinceRef = firestore.collection("Area");
        provinceRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData()+" : "+ document.getString("name"));
                                String documentId = document.getString("name");
                                Options.add(documentId);
                            }
//                             Create ArrayAdapter with custom style
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(NewAddressActivity.this, R.layout.spinner_item, Options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply adapter to spinner
                            spinnerArea.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void loadDistrict() {
        ArrayList<String> Options = new ArrayList<>();
        CollectionReference provinceRef = firestore.collection("Distract");
        provinceRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData()+" : "+ document.getString("name"));
                                String documentId = document.getString("name");
                                Options.add(documentId);
                            }
//                             Create ArrayAdapter with custom style
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(NewAddressActivity.this, R.layout.spinner_item, Options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply adapter to spinner
                            spinnerDistrict.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void loadProvince() {
        ArrayList<String> provinceOptions = new ArrayList<>();
        CollectionReference provinceRef = firestore.collection("Province");
        provinceRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData()+" : "+ document.getString("name"));
                                String documentId = document.getString("name");
                                provinceOptions.add(documentId);
                            }
//                             Create ArrayAdapter with custom style
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(NewAddressActivity.this, R.layout.spinner_item, provinceOptions);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply adapter to spinner
                            spinnerProvince.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

}