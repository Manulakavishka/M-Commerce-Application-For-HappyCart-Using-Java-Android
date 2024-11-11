package lk.jiat.app.happycart.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import lk.jiat.app.happycart.MainActivity;
import lk.jiat.app.happycart.Model.Cart;
import lk.jiat.app.happycart.Model.Product;
import lk.jiat.app.happycart.R;
import lk.jiat.app.happycart.SingleProductViewActivity;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ViewHolder> {

    public final static String TAG = MainActivity.class.getName();

    private ArrayList<Product> items;
    private FirebaseStorage storage;
    private Context context;
    private FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    private String userId;


    public ProductItemAdapter(ArrayList<Product> items, Context context) {
        this.items = items;
        this.storage = FirebaseStorage.getInstance();
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ProductItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemAdapter.ViewHolder holder, int position) {
        Product item = items.get(position);
        holder.textName.setText(item.getName());
        holder.textPrice.setText("RS : "+item.getPrice());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // User is signed in
            userId = user.getUid();
        }

        storage.getReference("product-images/"+item.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(200,200)
                                .centerCrop()
                                .into(holder.image);
                    }
                });

        holder.buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        Log.i(TAG,"Manu"+ item.getDocumentId()+ " "+ item.getCollectionId());

                Bundle bundle = new Bundle();
                bundle.putString("docId", item.getDocumentId());
                bundle.putString("colId", item.getCollectionId());

                Intent intent = new Intent(context, SingleProductViewActivity.class);
                intent.putExtras(bundle);

                context.startActivity(intent);

            }
        });

        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCartData(item.getCollectionId(),item.getDocumentId(),item.getPrice(),item.getImage(),item.getName());
            }
        });
//        Log.i(TAG,"Kavi"+item.getPrice().toString());

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
                        Toast.makeText(context, "Success",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName,textPrice;
        ImageView image;
        Button addToCartButton,addToWishlistButton,buyNowButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.productNameTextView);
            textPrice = itemView.findViewById(R.id.productPriceTextView);
            image = itemView.findViewById(R.id.productImageView);
            addToCartButton=itemView.findViewById(R.id.addToCart1);
            addToWishlistButton=itemView.findViewById(R.id.addToWishlist1);
            buyNowButton=itemView.findViewById(R.id.buyNow1);
        }
    }
}
