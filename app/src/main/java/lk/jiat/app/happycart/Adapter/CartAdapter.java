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

import androidx.annotation.NonNull;
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

import lk.jiat.app.happycart.MainActivity;
import lk.jiat.app.happycart.Model.Address;
import lk.jiat.app.happycart.Model.Cart;
import lk.jiat.app.happycart.NewAddressActivity;
import lk.jiat.app.happycart.R;
import lk.jiat.app.happycart.SingleProductViewActivity;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.AddressViewHolder> {

    private ArrayList<Cart> addresses;
    private Context context;
    // Get a reference to the Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;
    private FirebaseStorage storage;


    private final String TAG = MainActivity.class.getName();

    public CartAdapter(ArrayList<Cart> addresses, Context context) {
        this.addresses = addresses;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();

    }

    @NonNull
    @Override
    public CartAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cart, parent, false);
        return new CartAdapter.AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.AddressViewHolder holder, int position) {
        Cart address = addresses.get(position);
        holder.cartNameTextView.setText("Name : "+ address.getName());
        holder.cartPriceTextView.setText("LKR : "+address.getPrice());

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
        String collectionPath = "cart";
        String documentId = address.getUserId();

        storage.getReference("product-images/"+address.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(200,200)
                                .centerCrop()
                                .into(holder.productImageView);
                    }
                });


        holder.cartbuyNow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("docId", address.getProductDocId());
                bundle.putString("colId", address.getCatagoryDocId());

                Intent intent = new Intent(context, SingleProductViewActivity.class);
                intent.putExtras(bundle);

                context.startActivity(intent);

            }
        });
        holder.cartaddToWishlist1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Get a reference to the document

            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder{

        TextView cartNameTextView, cartPriceTextView;
        Button cartaddToWishlist1, cartbuyNow1;
        ImageView productImageView;

        AddressViewHolder(View itemView) {
            super(itemView);
            cartNameTextView = itemView.findViewById(R.id.cartNameTextView);
            cartPriceTextView = itemView.findViewById(R.id.cartPriceTextView);
            productImageView = itemView.findViewById(R.id.cartImageView);
            cartaddToWishlist1 = itemView.findViewById(R.id.cartaddToWishlist1);
            cartbuyNow1 = itemView.findViewById(R.id.cartbuyNow1);
        }

    }
}

