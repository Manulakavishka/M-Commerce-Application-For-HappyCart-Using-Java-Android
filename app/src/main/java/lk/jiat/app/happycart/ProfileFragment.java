package lk.jiat.app.happycart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lk.jiat.app.happycart.Adapter.UserAdepter;
import lk.jiat.app.happycart.Dialog.SignUpConfirmDialog;
import lk.jiat.app.happycart.Model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner genderSpinner;
    private Button saveButton;
    // Constants for intent request codes
    FirebaseAuth auth;
    FirebaseUser user;
    // Assuming you have the user's document ID (userId)
    private FirebaseFirestore firestore;

    private FirebaseStorage storage;
    private Uri imagePath;
    private ImageButton imageButton;
    private String userId;

    private EditText editTextFname,editTextLname,editTextMNum;
    private TextView status;
    public final static String TAG = ProfileFragment.class.getName();
    private ArrayList<User> users;
    ListenerRegistration listenerRegistration;
    private String imageId;

    private String mverificationId;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;



    public ProfileFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextFname = view.findViewById(R.id.firstName);
        editTextLname = view.findViewById(R.id.lastName);
        editTextMNum = view.findViewById(R.id.mobileNumber);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        status = view.findViewById(R.id.status);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imageButton = view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));


            }
        });



        loadData();



        if (user != null) {
            // User is signed in
            userId = user.getUid();
            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId=null;
            // User is not signed in
            // Handle the situation accordingly
        }

        genderLoad(view);

        saveButton = view.findViewById(R.id.saveButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData(view);
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG,"onVerificationCompleted : "+phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.i(TAG,"onVerificationFailed : "+e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.i(TAG,"onCodeSent : "+verificationId);
                Toast.makeText(getContext(),"OPT code sent your phone",Toast.LENGTH_LONG).show();

                mverificationId= verificationId;
                resendingToken = forceResendingToken;
                showCustomDialog(mverificationId);


            }
        };

        view.findViewById(R.id.addressBook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddressBookActivity.class);
                startActivity(intent);
            }
        });



        return view;


    }

    public void updatePhoneNumber( String code) {

        // Create PhoneAuthCredential using the verification ID and the code sent to the user
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mverificationId, code);

        // Update the user's phone number
        if (user != null) {
            user.updatePhoneNumber(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Phone number updated successfully
                            Toast.makeText(getContext(),"Successfully Bind The Number",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(),"Something Wrong",Toast.LENGTH_LONG).show();
                            // Phone number update failed
                            // Handle the error
                        }
                    });
        }
    }

    private void veriftyNum(String newPhoneNumber ){
        // Obtain the new phone number

// Send a verification code to the user's new phone number
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+94"+newPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)    // Timeout duration
                        .setActivity(getActivity())                    // Activity (or callback)
                        .setCallbacks(callbacks)
                        .build()
        );

    }
    private void saveData(View view){
        // Initialize UI elements

        String fname = editTextFname.getText().toString();
        String lname = editTextLname.getText().toString();
        String num = editTextMNum.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        if(imageId==null){
            imageId = UUID.randomUUID().toString();
        }

        User user1 = new User(fname,lname,gender,imageId);

        firestore.collection("Users").document(userId).set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(user.getPhoneNumber().equals("")){
                    veriftyNum(num);
                }
                if(imagePath != null){

                    StorageReference reference = storage.getReference("user-images")
                            .child(imageId);

                    reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
    private void loadData(){

        DocumentReference docRef = firestore.collection("Users").document(userId);

        Source source = Source.DEFAULT;

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                editTextFname.setText((CharSequence) document.get("firstName"));
                                editTextLname.setText((CharSequence) document.get("lastName"));

// Get the adapter from the Spinner
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) genderSpinner.getAdapter();

// Iterate through the adapter to find the index of the item
                                for (int i = 0; i < adapter.getCount(); i++) {
//                                    Log.i(TAG,"Gender : "+adapter.getItem(i));
                                    if (adapter.getItem(i).equals(document.get("gender"))) {
                                        // Set the selection
                                        genderSpinner.setSelection(i);
                                        break;
                                    }
                                }

                                if(!user.getPhoneNumber().equals("")){
                                    editTextMNum.setText((CharSequence) user.getPhoneNumber());
                                    editTextMNum.setEnabled(false);
                                }

                                if (user.isEmailVerified()){
                                    status.setText("User Verified");
                                }else {
                                    status.setText("User Not Verified");
                                }

                                storage.getReference("user-images/"+document.get("image"))
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .resize(200,200)
                                                        .centerCrop()
                                                        .into(imageButton);
                                                imageId=document.get("image").toString();

                                            }
                                        });
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

    }



    private void genderLoad(View view){
        // Initialize UI elements
        genderSpinner = view.findViewById(R.id.genderSpinner);

        // Set up gender spinner options
        String[] genderOptions = {"Male", "Female"};

        // Create ArrayAdapter with custom style
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply adapter to spinner
        genderSpinner.setAdapter(adapter);

    }

    private void showCustomDialog(String verificationId) {
        SignUpConfirmDialog customDialog = new SignUpConfirmDialog(ProfileFragment.this,getContext(),verificationId);
        customDialog.show();
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        imagePath = result.getData().getData();
                        Log.i(TAG,"Image Path : " + imagePath.getPath());

                        Picasso.get().load(imagePath).resize(200,200).
                                centerCrop().into(imageButton);



                    }
                }
            }
    );

}