package lk.jiat.app.happycart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import lk.jiat.app.happycart.Model.Cart;
import lk.jiat.app.happycart.Model.Invoice;
import lk.jiat.app.happycart.Model.Product;

public class SingleProductViewActivity extends AppCompatActivity {
    public final static String TAG = MainActivity.class.getName();
    FirebaseAuth auth;
    FirebaseUser user;
    private FirebaseFirestore firestore;

    private FirebaseStorage storage;
    private Uri imagePath;
    private ImageView imageView;
    private String userId;
    private TextView name,price,qty,disc;
    private Button min,max, buy, cart,wish;
    private ArrayList<Product> products;
    ListenerRegistration listenerRegistration;
    private String imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);

//        Log.i(TAG,"Mnau");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String idDoc = bundle.getString("docId");
            String idCol = bundle.getString("colId");
            Log.d(TAG, "Retrieved id: " + idDoc+ " " + idCol);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            if (user != null) {
                // User is signed in
                userId = user.getUid();
                // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
                firestore = FirebaseFirestore.getInstance();
                storage = FirebaseStorage.getInstance();

                name = findViewById(R.id.productNameTextView);
                price = findViewById(R.id.productPriceTextView);
                disc = findViewById(R.id.productDescriptionTextView);
                qty = findViewById(R.id.quantityTextView);
                min = findViewById(R.id.decreaseQuantityButton);
                max = findViewById(R.id.increaseQuantityButton);
                buy = findViewById(R.id.buy);
                wish = findViewById(R.id.addToW);
                cart = findViewById(R.id.addToC);
                imageView = findViewById(R.id.productImage);


                DocumentReference docRef = firestore.collection("Categories")
                        .document(idCol)
                        .collection("products")
                        .document(idDoc);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
//                                max[0] = (long) document.get("quantity");
                                long totalQty = (long) document.get("quantity");

                                int currentQuantity = Integer.parseInt(qty.getText().toString());

                                // You can set a maximum limit if needed
                                if (totalQty > 0){

                                    max.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//                        changeQtyMax(idDoc,idCol);
                                            increaseQuantity(idDoc,idCol);
                                        }
                                    });

                                    min.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            decreaseQuantity();
                                        }
                                    });

                                    buy.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.i(TAG,pric);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("docId", idDoc);
                                            bundle.putString("colId", idCol);
                                            bundle.putString("qty", (String) qty.getText());
                                            bundle.putString("price", pric);
                                            bundle.putInt("totalQty", (int) qtyTotal);

                                            Intent intent = new Intent(SingleProductViewActivity.this, CheckOutActivity.class);
                                            intent.putExtras(bundle);

                                            startActivity(intent);
                                        }
                                    });

                                    cart.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            addCartData(idCol,idDoc,price.getText().toString(),imageId,name.getText().toString());
                                        }
                                    });
                                }else{
                                    Toast.makeText(SingleProductViewActivity.this, "Out Of Stock",Toast.LENGTH_LONG).show();

                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });



                loadData(idDoc,idCol);


            } else {
                userId=null;
                Intent intent = new Intent(SingleProductViewActivity.this, SignUpActivity.class);
                startActivity(intent);
                // User is not signed in
                // Handle the situation accordingly
            }


        } else {
            Log.e(TAG, "Bundle is null");
            Intent intent = new Intent(SingleProductViewActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }

    private void addCartData(String col, String doc,String price,String imageId,String name){
        // Initialize UI elements


        String id = UUID.randomUUID().toString();

       Cart user1 = new Cart(id,col,doc,price,name,imageId);

        firestore.collection("Users").document(userId)
                .collection("cart").document(id)
                .set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SingleProductViewActivity.this, "Success",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SingleProductViewActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }


    private void decreaseQuantity() {
        int currentQuantity = Integer.parseInt(qty.getText().toString());
        if (currentQuantity > 1) {
            currentQuantity--;
            qty.setText(String.valueOf(currentQuantity));
        }
    }
    private void increaseQuantity(String doc,String col) {
        final long[] max = new long[1];
        DocumentReference docRef = firestore.collection("Categories")
                .document(col)
                .collection("products")
                .document(doc);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        max[0] = (long) document.get("quantity");

                        int currentQuantity = Integer.parseInt(qty.getText().toString());

                        // You can set a maximum limit if needed
                        if (currentQuantity < max[0]){
                            currentQuantity++;
                            qty.setText(String.valueOf(currentQuantity));
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




    }

    private long text;

    private void changeQtyMax(){



        Source source = Source.DEFAULT;


    }

    private static String pric;
    private static long qtyTotal;
    private void loadData(String doc,String col){

        DocumentReference docRef = firestore.collection("Categories")
                .document(col)
                .collection("products")
                .document(doc);

        Source source = Source.DEFAULT;

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        name.setText((CharSequence) document.get("name"));
                        disc.setText((CharSequence) document.get("description"));
                        price.setText("Price : " +document.get("price"));
                        pric= (String) document.get("price");
                        qtyTotal = (long) document.get("quantity");


                        storage.getReference("product-images/"+document.get("image"))
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.get()
                                                .load(uri)
                                                .resize(200,200)
                                                .centerCrop()
                                                .into(imageView);
                                        imageId=document.get("image").toString();
//
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

}