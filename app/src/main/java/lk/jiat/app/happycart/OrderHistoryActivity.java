package lk.jiat.app.happycart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

import lk.jiat.app.happycart.Adapter.AddressAdapter;
import lk.jiat.app.happycart.Adapter.ViewOrderHistoryAdapter;
import lk.jiat.app.happycart.Model.Address;
import lk.jiat.app.happycart.Model.Invoice;

public class OrderHistoryActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ArrayList<Invoice> addresses;
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;
    private final String TAG = AddressBookActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // User is signed in
            userId = user.getUid();
            firestore=FirebaseFirestore.getInstance();
            addresses=new ArrayList<>();
            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user

            RecyclerView addressView = findViewById(R.id.recyclerViewOrderHistory);

            ViewOrderHistoryAdapter addressAdapter = new ViewOrderHistoryAdapter(addresses,OrderHistoryActivity.this);

            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
            addressView.setLayoutManager(layoutManager);

            addressView.setAdapter(addressAdapter);


            firestore.collection("Users").document(userId).collection("invoice")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            addresses.clear();
                            for(DocumentSnapshot snapshot: value.getDocuments()){
//                    Log.i(TAG,"Manula : "+ snapshot.getId() + " " +snapshot.getData());
                                Invoice item = snapshot.toObject(Invoice.class);
                                item.setAddressLocation("");
                                item.setId(snapshot.getId());
                                addresses.add(item);
                            }

                            addressAdapter.notifyDataSetChanged();
                        }
                    });


        } else {
            userId=null;
            // User is not signed in
            // Handle the situation accordingly
        }


    }
}