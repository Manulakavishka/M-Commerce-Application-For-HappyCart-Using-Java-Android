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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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

import lk.jiat.app.happycart.CheckOutActivity;
import lk.jiat.app.happycart.InvoiceActivity;
import lk.jiat.app.happycart.MainActivity;
import lk.jiat.app.happycart.Model.Invoice;
import lk.jiat.app.happycart.Model.Product;
import lk.jiat.app.happycart.MyLocationActivity;
import lk.jiat.app.happycart.OrderHistoryActivity;
import lk.jiat.app.happycart.R;
import lk.jiat.app.happycart.SuccessActivity;

public class ViewOrderHistoryAdapter extends RecyclerView.Adapter<ViewOrderHistoryAdapter.AddressViewHolder> {

    private ArrayList<Invoice> addresses;
    private ArrayList<Product> itemsProduct;
    private Context context;
    // Get a reference to the Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    FirebaseAuth auth;
    FirebaseUser user;
    private FirebaseStorage storage;

    private final String TAG = MainActivity.class.getName();

    public ViewOrderHistoryAdapter(ArrayList<Invoice> addresses, Context context) {
        this.addresses = addresses;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewOrderHistoryAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_invocie, parent, false);
        return new ViewOrderHistoryAdapter.AddressViewHolder(view);
    }

    private Product product;

    @Override
    public void onBindViewHolder(@NonNull ViewOrderHistoryAdapter.AddressViewHolder holder, int position) {
        if (user != null) {
            // User is signed in
            userId = user.getUid();
            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId=null;
            // User is not signed in
            // Handle the situation accordingly
        }
        Invoice address = addresses.get(position);

        holder.priceTextView.setText("Price : "+address.getPrice());
        holder.qtyTextView.setText("Qty : "+address.getQty()+"");
        holder.statusTextView.setText("Status : "+address.getStatus()+"");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        itemsProduct=new ArrayList<>();


        db.collection("Categories")
                .document(address.getCatagoryDocId())
                .collection("products").document(address.getProductDocId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        itemsProduct.clear();
                        Product item = value.toObject(Product.class);
                        item.setCollectionId(address.getCatagoryDocId());
                        item.setDocumentId(address.getProductDocId());
                        itemsProduct.add(item);

                        product = null;
                        product =value.toObject(Product.class);
                        product.setCollectionId(address.getCatagoryDocId());
                        product.setDocumentId(address.getProductDocId());

                        holder.nameTextView.setText("Name : "+item.getName());
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

//                    Log.i(TAG,"Manu : "+snapshot.getId());
                    }
                });


        // Specify the collection and document ID
        String collectionPath = "Addresses";
        String documentId = address.getId();

        holder.invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "Manula : "+ address.getId());
                Intent intent = new Intent(context, InvoiceActivity.class);
//                intent.putExtra("invoice", address);
//                intent.putExtra("product", product);
//
//                context.startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString("getAddressId",address.getAddressId());
                bundle.putString("getDate",address.getDate());
//                bundle.putString("getAddressLocation",address.getAddressLocation());
                bundle.putString("getId",address.getId());
                bundle.putString("getPrice",address.getPrice());
                bundle.putString("getCatagoryDocId",address.getCatagoryDocId());
                bundle.putString("getProductDocId",address.getProductDocId());
                bundle.putString("getStatus",address.getStatus());
                bundle.putString("getQty",address.getQty());
                bundle.putLong("ProGetId",product.getId());
                bundle.putString("getName",product.getName());
                bundle.putString("getUnitPrice",product.getPrice());
                bundle.putString("getImage",product.getImage());

//                Intent intent = new Intent(context, NewAddressActivity.class);
                intent.putExtras(bundle);
//
                context.startActivity(intent);

            }
        });

        if(address.getStatus().equalsIgnoreCase("Delivering")){

            holder.addLocationButton.setVisibility(View.VISIBLE);
            holder.addLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MyLocationActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("getId",address.getId());

                    intent.putExtras(bundle);
//
                    context.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView, priceTextView, qtyTextView,statusTextView;
        Button invoiceButton,addLocationButton;
        View view;
        ImageView image;

        AddressViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewProductNmae);
            priceTextView = itemView.findViewById(R.id.textViewPrice);
            qtyTextView = itemView.findViewById(R.id.textViewQty);
            statusTextView = itemView.findViewById(R.id.textViewProductStatus);
            invoiceButton = itemView.findViewById(R.id.buttonInvoice);
            addLocationButton = itemView.findViewById(R.id.buttonAddLocation);
            view=itemView;
            image = itemView.findViewById(R.id.orderHistoryproductImageView);
        }

    }


}

