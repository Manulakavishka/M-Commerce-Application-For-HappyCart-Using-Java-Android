package lk.jiat.app.happycart.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import lk.jiat.app.happycart.Model.CarouselItem;
import lk.jiat.app.happycart.R;
// Step 4: Implement a custom PagerAdapter
public class CarouselPagerAdapter extends PagerAdapter {
    private List<CarouselItem> items;
    private Context context;

    public CarouselPagerAdapter(Context context, List<CarouselItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.carousel_item, container, false);

        // Customize the layout for each item (e.g., set an image)
        ImageView imageView = view.findViewById(R.id.imageView);
//        imageView.setImageResource(items.get(position).getImage());
        Picasso.get()
                .load(items.get(position).getImage())
                .resize(1200,440)
                .centerCrop()
                .into(imageView);

//        imageView.setImageIcon(view.findViewById(););

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
