package lk.jiat.app.happycart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.jiat.app.happycart.Adapter.AddressAdapter;
import lk.jiat.app.happycart.Adapter.CartAdapter;
import lk.jiat.app.happycart.Model.Address;
import lk.jiat.app.happycart.Model.Cart;

public class CartActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<Cart> carts;
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;
    private final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        carts=new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // User is signed in
            userId = user.getUid();

            RecyclerView addressView = findViewById(R.id.cartItem);

            CartAdapter addressAdapter = new CartAdapter(carts,CartActivity.this);

            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
            addressView.setLayoutManager(layoutManager);

            addressView.setAdapter(addressAdapter);


            firestore.collection("Users").document(userId).collection("cart")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            carts.clear();
                            for(DocumentSnapshot snapshot: value.getDocuments()){
//                    Log.i(TAG,"Manula : "+ snapshot.getId());
                                Cart item = snapshot.toObject(Cart.class);
                                item.setUserId(snapshot.getId());
                                carts.add(item);
                            }

                            addressAdapter.notifyDataSetChanged();
                        }
                    });



            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId=null;
            // User is not signed in
            // Handle the situation accordingly
        }

    }
}