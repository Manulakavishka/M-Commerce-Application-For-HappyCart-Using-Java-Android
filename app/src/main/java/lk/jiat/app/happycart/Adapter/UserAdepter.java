package lk.jiat.app.happycart.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.jiat.app.happycart.Model.User;
import lk.jiat.app.happycart.R;

public class UserAdepter extends RecyclerView.Adapter<UserAdepter.ViewHolder> {

    private ArrayList<User> users;
    private FirebaseStorage storage;
    private Context context;

    public UserAdepter(ArrayList<User> users, Context context) {
        this.users = users;
        this.storage = FirebaseStorage.getInstance();
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdepter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdepter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.fTextName.setText(user.getFirstName());
        holder.lTextName.setText(user.getLastName());

        storage.getReference("user-images/"+user.getImage())
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
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView fTextName,lTextName,textGender,textMobile;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fTextName = itemView.findViewById(R.id.firstName);
            lTextName = itemView.findViewById(R.id.lastName);
            textGender = itemView.findViewById(R.id.genderSpinner);
            textMobile = itemView.findViewById(R.id.mobileNumber);
            image = itemView.findViewById(R.id.profilePic);
        }
    }
}
