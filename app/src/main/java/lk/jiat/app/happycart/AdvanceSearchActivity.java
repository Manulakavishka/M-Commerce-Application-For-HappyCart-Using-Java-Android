package lk.jiat.app.happycart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.app.happycart.Adapter.AddressAdapter;
import lk.jiat.app.happycart.Adapter.ProductItemAdapter;
import lk.jiat.app.happycart.Model.Address;
import lk.jiat.app.happycart.Model.CarouselItem;
import lk.jiat.app.happycart.Model.Product;

public class AdvanceSearchActivity extends AppCompatActivity {

    private SeekBar seekBarPrice;
    private TextView textViewPrice;

    private FirebaseFirestore firestore;
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;

    private FirebaseStorage storage;
    public final static String TAG = MainActivity.class.getName();
    private String imageId;
    private ImageView imageView;
    private ArrayList<Product> products;
    private ListView listView;
    private GridView gridView;
    private TextView categoryNameTextView;
//    private EditText editTextKeyword;
    private Spinner spinnerCateg;
    private int pro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_search);

        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        seekBarPrice = findViewById(R.id.seekBarPrice);
        textViewPrice = findViewById(R.id.textViewPrice);
//        editTextKeyword = findViewById(R.id.editTextKeyword);
        spinnerCateg = findViewById(R.id.spinnerCateg);

        products=new ArrayList<>();

        if (user != null) {
            // User is signed in
            userId = user.getUid();

            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId=null;
            // User is not signed in
            // Handle the situation accordingly
        }



        // Set the initial price value
        int initialPrice = seekBarPrice.getProgress();
        textViewPrice.setText("Price: LKR " + initialPrice);

        // Set up a listener for the SeekBar changes
        seekBarPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the current progress
                textViewPrice.setText("Price: LKR " + progress);
                pro= progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing when tracking starts
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing when tracking stops
            }
        });
        loadCategory();

        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProcess();
            }
        });

    }

    private List<String> allWords;
    private List<String> filteredWords;
    private ArrayAdapter<String> adapter;
    private void searchProcess() {
        String category = spinnerCateg.getSelectedItem().toString();

        RecyclerView prodView = findViewById(R.id.advanceID);

        ProductItemAdapter productItemAdapter = new ProductItemAdapter(products, AdvanceSearchActivity.this);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        prodView.setLayoutManager(layoutManager);

        prodView.setAdapter(productItemAdapter);


        firestore.collection("Categories").document(category).collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        products.clear();
//                        allWords.clear();
//                        allWords = new ArrayList<>();
                        for(DocumentSnapshot snapshot: value.getDocuments()){
                            String price =snapshot.getString("price");
                            int price1 = Integer.parseInt(price);
//                    Log.i(TAG,"Manula : "+ snapshot.getId());
                            if(price1<= pro){
//                                Log.i(TAG,pro+ " "+ price1);

                                                Product item = snapshot.toObject(Product.class);
                                                item.setDocumentId(snapshot.getId());
                                                item.setCollectionId(category);
                                                products.add(item);


                            }

                        }

                        productItemAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadCategory() {
        ArrayList<String> Options = new ArrayList<>();
        CollectionReference provinceRef = firestore.collection("Categories");
        provinceRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                Options.add(documentId);
                            }
//                             Create ArrayAdapter with custom style
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AdvanceSearchActivity.this, R.layout.spinner_item, Options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply adapter to spinner
                            spinnerCateg.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}