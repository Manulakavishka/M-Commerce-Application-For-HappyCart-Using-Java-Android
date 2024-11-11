package lk.jiat.app.happycart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.jiat.app.happycart.Adapter.AddressAdapter;
import lk.jiat.app.happycart.Model.Address;

public class AddressBookActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<Address> addresses;
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;
    private final String TAG = AddressBookActivity.class.getName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

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
        storage=FirebaseStorage.getInstance();

        addresses=new ArrayList<>();

        RecyclerView addressView = findViewById(R.id.recyclerViewAddresses);

        AddressAdapter addressAdapter = new AddressAdapter(addresses,AddressBookActivity.this);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        addressView.setLayoutManager(layoutManager);

        addressView.setAdapter(addressAdapter);


        firestore.collection("Addresses").document(userId).collection("user")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                addresses.clear();
                for(DocumentSnapshot snapshot: value.getDocuments()){
//                    Log.i(TAG,"Manula : "+ snapshot.getId());
                    Address item = snapshot.toObject(Address.class);
                    item.setId(snapshot.getId());
                    addresses.add(item);
                }

                addressAdapter.notifyDataSetChanged();
            }
        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressBookActivity.this, NewAddressActivity.class);
                startActivity(intent);
            }
        });

    }
}