package lk.jiat.app.happycart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.jiat.app.happycart.Model.Catagory;
import lk.jiat.app.happycart.R;

//public class CatagoryAdapter extends RecyclerView.Adapter<CatagoryAdapter.ViewHolder> {
//    private ArrayList<Catagory> items;
//    private Context context;
//
//
//    public CatagoryAdapter (ArrayList<Catagory> items, Context context) {
//        this.items = items;
//        this.context = context;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CatagoryAdapter.ViewHolder holder, int position) {
//        Catagory item = items.get(position);
//        holder.textCataName.setText(item.getName());
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CatagoryAdapter holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textCataName;
//        GridView gride;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textCataName= itemView.findViewById(R.id.categoryNameTextView);
//            gride= itemView.findViewById(R.id.productGridView);
//        }
//    }
//}
