package lk.jiat.app.happycart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.jiat.app.happycart.Model.Invoice;
import lk.jiat.app.happycart.Model.Product;

public class InvoiceActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private ArrayList<Invoice> addresses;
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;
    private final String TAG = MainActivity.class.getName();

    private ArrayList<Product> itemsProduct;
    private Context context;
    // Get a reference to the Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        itemsProduct = new ArrayList<>();
        storage = FirebaseStorage.getInstance();

        Bundle bundle = getIntent().getExtras();
        String getStatus = bundle.getString("getStatus");
        String getProductDocId = bundle.getString("getProductDocId");
        String getCatagoryDocId = bundle.getString("getCatagoryDocId");
        String getId = bundle.getString("getId");
        String getAddressLocation = bundle.getString("getAddressLocation");
        String getDate = bundle.getString("getDate");
        String getAddressId = bundle.getString("getAddressId");
        String getPrice = bundle.getString("getPrice");
        String getQty = bundle.getString("getQty");
        String getName = bundle.getString("getName");
        String getUnitPrice = bundle.getString("getUnitPrice");
        String getImage = bundle.getString("getImage");

        if (user != null) {
            // User is signed in
            userId = user.getUid();
            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId = null;
            // User is not signed in
            // Handle the situation accordingly
        }

        TextView itemName= findViewById(R.id.itemName);
        TextView itemQuantity= findViewById(R.id.itemQuantity);
        TextView itemPrice= findViewById(R.id.itemPrice);
        TextView totalAmount= findViewById(R.id.totalAmount);
        ImageView image= findViewById(R.id.invoiceorderHistoryproductImageView);

        itemName.setText("Item Name : " + getName);
        itemQuantity.setText("Qty : " + getQty);
        itemPrice.setText("Price : " + getUnitPrice);
        totalAmount.setText("Total Amount : " + getPrice);



        db.collection("Categories")
                .document(getCatagoryDocId)
                .collection("products").document(getProductDocId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        itemsProduct.clear();
                        Product item = value.toObject(Product.class);

                        storage.getReference("product-images/" + item.getImage())
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.get()
                                                .load(uri)
                                                .resize(200, 200)
                                                .centerCrop()
                                                .into(image);
                                    }
                                });

//                    Log.i(TAG,"Manu : "+snapshot.getId());
                    }
                });

    }
}