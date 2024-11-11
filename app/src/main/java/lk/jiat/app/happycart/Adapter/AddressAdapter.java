package lk.jiat.app.happycart.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.app.happycart.AddressBookActivity;
import lk.jiat.app.happycart.Model.Address;
import lk.jiat.app.happycart.NewAddressActivity;
import lk.jiat.app.happycart.R;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private ArrayList<Address> addresses;
    private Context context;
    // Get a reference to the Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;

    private final String TAG = AddressAdapter.class.getName();

    public AddressAdapter(ArrayList<Address> addresses, Context context) {
        this.addresses = addresses;
        this.context = context;
    }

    @NonNull
    @Override
    public AddressAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.AddressViewHolder holder, int position) {
        Address address = addresses.get(position);
        holder.fullNameTextView.setText(address.getFullName());
        holder.mobileNumberTextView.setText(address.getMobileNumber());
        holder.addressTypeTextView.setText(address.getAddressType());
        holder.addressTextView.setText(address.getAddress());
        holder.landTextView.setText(address.getLandMark());
        holder.addressProDisAreaTextView.setText(address.getProvince()+" ,"+address.getDistrict()+" ,"+ address.getArea());

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

        // Specify the collection and document ID
        String collectionPath = "Addresses";
        String documentId = address.getId();

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Manula : "+ address.getId());

                Bundle bundle = new Bundle();
                bundle.putString("id", address.getId());

                Intent intent = new Intent(context, NewAddressActivity.class);
                intent.putExtras(bundle);

                context.startActivity(intent);

            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Get a reference to the document
                DocumentReference documentReference = db.collection(collectionPath).document(userId).collection("user").document(documentId);
// Delete the document
                documentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document successfully deleted
                                // You can perform any additional actions here
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder{

        TextView fullNameTextView, mobileNumberTextView, addressTypeTextView,addressTextView,addressProDisAreaTextView,landTextView;
        Button editButton, deleteButton;

        AddressViewHolder(View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.textViewFullName);
            mobileNumberTextView = itemView.findViewById(R.id.textViewMobileNumber);
            addressTypeTextView = itemView.findViewById(R.id.textViewAddType);
            addressTextView = itemView.findViewById(R.id.textViewAddress);
            addressProDisAreaTextView = itemView.findViewById(R.id.textViewProDisArea);
            landTextView = itemView.findViewById(R.id.textViewLand);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }

    }
}

