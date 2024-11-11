package lk.jiat.app.happycart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import lk.jiat.app.happycart.Model.Invoice;
import lk.jiat.app.happycart.Model.Product;
import lk.jiat.app.happycart.Model.User;

public class CheckOutActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    private String userId;
    public final static String TAG = MainActivity.class.getName();
    private Spinner spinnerAddress;
    private TextView totalQty,totalAmount,payment;
    Button placeOrderButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        requestPermissions(new String[]{
                Manifest.permission.POST_NOTIFICATIONS
        },100);

        notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String idDoc = bundle.getString("docId");
            String idCol = bundle.getString("colId");
            int qtyTotal = bundle.getInt("totalQty");
            double price = Double.parseDouble(bundle.getString("price"));
            int qty = Integer.parseInt(bundle.getString("qty"));

            int newQty = qtyTotal-qty;

            String totalPric= String.valueOf(price*qty);
            String tqty = String.valueOf(qty);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore= FirebaseFirestore.getInstance();

        spinnerAddress=findViewById(R.id.spinnerAddress);

        if (user != null) {
            // User is signed in
            userId = user.getUid();
            loadAddress();

            totalAmount = findViewById(R.id.totalAmount);
            totalQty = findViewById(R.id.totalQty);
            payment = findViewById(R.id.payment);
            placeOrderButton = findViewById(R.id.placeOrderButton);

            totalQty.setText("Total Quantity: "+qty);
            totalAmount.setText("Total: LKR"+(int) (qty*price));
            payment.setText(""+(int) (qty*price));


            placeOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = spinnerAddress.getSelectedItem().toString();
                    String[] addressId = s.split("  _");
                    String s1 = addressId[1];
//            Log.d(TAG, "Retrieved id: " + addressId[1]);
                    saveData(s1,idCol,idDoc,totalPric,tqty,newQty);

                }
            });




            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId=null;
            Intent intent = new Intent(CheckOutActivity.this, SignUpActivity.class);
            startActivity(intent);
            // User is not signed in
            // Handle the situation accordingly
        }


    } else {
        Log.e(TAG, "Bundle is null");
        Intent intent = new Intent(CheckOutActivity.this, MainActivity.class);
        startActivity(intent);

    }
    }



    private void saveData(String addressId,String col, String doc,String price,String qty,int qtyTotal){
        // Initialize UI elements

        LocalDate currentDate = LocalDate.now();

        // Format the date if needed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        String id =UUID.randomUUID().toString();

        Invoice user1 = new Invoice(addressId,col,doc,formattedDate,price,qty,"Panding");
        user1.setId(id);

        firestore.collection("Users").document(userId)
                .collection("invoice").document(id)
                .set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateProduct(qtyTotal,col,doc);
                Toast.makeText(CheckOutActivity.this, "Success",Toast.LENGTH_LONG).show();
                popUpNotification();
                Intent intent = new Intent(CheckOutActivity.this, SuccessActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(CheckOutActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private NotificationManager notificationManager;
    private String channelId = "info";

    private void popUpNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,"INFO",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            channel.setDescription("Your Order Was Placed.");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{0,1000,1000,1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        Bundle bundle = new Bundle();
        bundle.putString("name","Success");

        Intent intent = new Intent(CheckOutActivity.this, SuccessActivity.class);
        intent.putExtra("name", "Success");

        PendingIntent pendingIntent = PendingIntent.
                getActivity(CheckOutActivity.this, 0,intent,PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_smartwatch_icon)
                .setContentTitle("Your Order Was Placed.")
                .setContentText("Please Go To Order History To see More Details.")
                .setColor(Color.GREEN)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(1,notification);

    }

    private void updateProduct(int q,String col,String doc) {
        Product user1 = new Product();
        user1.setQuantity(q);

        firestore.collection("Categories").document(col)
                .collection("products").document(doc)
                .update("quantity",q).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CheckOutActivity.this, "Qty Updated",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(CheckOutActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    private void loadAddress() {
        ArrayList<String> Options = new ArrayList<>();
        CollectionReference provinceRef = firestore.collection("Addresses")
                .document(userId)
                .collection("user");

        provinceRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String documentId = document.getString("fullName")+ " " + document.getString("mobileNumber")+" "+document.getString("province")+" "+document.getString("district")+" "+document.getString("area")+" "+document.getString("landMark")+" "+document.getString("addressType")+" "+document.getString("address")+"  _"+document.getId();
                                Options.add(documentId);
                                Log.d(TAG, document.getId() + " => " +documentId);
                            }
//                             Create ArrayAdapter with custom style
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CheckOutActivity.this, R.layout.spinner_item, Options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Apply adapter to spinner
                            spinnerAddress.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}