package lk.jiat.app.happycart.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.app.happycart.MainActivity;
import lk.jiat.app.happycart.Model.Catagory;
import lk.jiat.app.happycart.Model.Product;
import lk.jiat.app.happycart.R;

public class CategoriesItemAdapter extends RecyclerView.Adapter<CategoriesItemAdapter.ViewHolder> {

    private ArrayList<String> items;
    private Context context;

    public CategoriesItemAdapter(ArrayList<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoriesItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_with_product_with_catagory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesItemAdapter.ViewHolder holder, int position) {
        String s = items.get(position);
        holder.textName.setText(s);
        View itemView = holder.itemView;
        loadProducts(itemView,s);
//        Log.i(TAG,s);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;
        RecyclerView view;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textView2);
            view = itemView.findViewById(R.id.recylerViewItemProduct);
            this.itemView=itemView;
        }
    }
    private ArrayList<Product> itemsProduct;
    private FirebaseFirestore firestore;
    public final static String TAG = MainActivity.class.getName();
    private void loadProducts(View view,String categoryName) {

        Log.i(TAG,categoryName);

        firestore=FirebaseFirestore.getInstance();

        itemsProduct=new ArrayList<>();

        RecyclerView itemView = view.findViewById(R.id.recylerViewItemProduct);

        ProductItemAdapter itemAdapter = new ProductItemAdapter(itemsProduct,context);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
        itemView.setLayoutManager(layoutManager);

        itemView.setAdapter(itemAdapter);
        // Set the category name in the TextView

//        categoryNameTextView.setText(categoryName);

        firestore.collection("Categories")
                .document(categoryName)
                .collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                itemsProduct.clear();
                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Product item = snapshot.toObject(Product.class);
                    item.setCollectionId(categoryName);
                    item.setDocumentId(snapshot.getId());
                    itemsProduct.add(item);

//                    Log.i(TAG,"Manu : "+snapshot.getId());
                }

                itemAdapter.notifyDataSetChanged();
            }
        });

    }

}
