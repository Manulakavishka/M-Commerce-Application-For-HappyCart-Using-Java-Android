package lk.jiat.app.happycart;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.app.happycart.Adapter.CarouselPagerAdapter;
import lk.jiat.app.happycart.Adapter.CategoriesItemAdapter;
import lk.jiat.app.happycart.Model.CarouselItem;
import lk.jiat.app.happycart.Model.Catagory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore firestore;

    private FirebaseStorage storage;
    public final static String TAG = MainActivity.class.getName();
    private String imageId;
    private ImageView imageView;
    private ArrayList<CarouselItem> caroImages;
    private ListView listView;
    private GridView gridView;
    private TextView categoryNameTextView;



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        caroImages=new ArrayList<>();
        items= new ArrayList<>();




        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imageView = view.findViewById(R.id.imageView);

        listView = view.findViewById(R.id.categoryListView);

//        gridView = view.findViewById(R.id.productGridView);
//        categoryNameTextView = view.findViewById(R.id.categoryNameTextView);


        loadCarousel(view);
        loadCategories(view);
        loadCategoryWithProducts(view);

        return view;
    }

    private ArrayList<String> itemsCatagory;
    private void loadCategoryWithProducts(View view) {

        itemsCatagory=new ArrayList<>();

        RecyclerView itemView = view.findViewById(R.id.productWithCata);

        CategoriesItemAdapter itemAdapter = new CategoriesItemAdapter(itemsCatagory,view.getContext());

        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);
        itemView.setLayoutManager(layoutManager);

        itemView.setAdapter(itemAdapter);
        // Set the category name in the TextView

//        categoryNameTextView.setText(categoryName);

        firestore.collection("Categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        itemsCatagory.clear();
                        for(QueryDocumentSnapshot snapshot: task.getResult()){
                            itemsCatagory.add(snapshot.getId());
                        }

                        itemAdapter.notifyDataSetChanged();
                    }
                });


//        firestore.collection("Categories")
//                .document(categoryName)
//                .collection("products")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<String> productList = new ArrayList<>();
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                // Assuming you have a field named "productName" in your product documents
//                                String productName = document.getString("name");
//                                productList.add(productName);
//                            }
//
//                            // Update the GridView with the product data
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                                    view.getContext(),
//                                    android.R.layout.simple_list_item_1,
//                                    productList
//                            );
//                            gridView.setAdapter(adapter);
//                        } else {
//                            // Handle the error
//                        }
//                    }
//                });
    }

    private void loadCategories(View view) {
        firestore.collection("Categories").limit(5L)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> categoryList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getId();
                                // Assuming have a field named "name" in your category documents
//                                String categoryName = document.getString("name");
                                String categoryName =  document.getId();;
                                categoryList.add(categoryName);
//                                Log.i(TAG,categoryName);
                            }

                            // Update the ListView with the category data
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    view.getContext(),
                                    android.R.layout.simple_list_item_1,
                                    categoryList
                            );
                            listView.setAdapter(adapter);
//                            Log.i(TAG,categoryList.size()+"");
                        } else {
                            // Handle the error
                        }
                    }
                });
    }

    List<CarouselItem> items;
    private void loadCarousel(View view) {
        ViewPager viewPager = view.findViewById(R.id.viewPager);

        firestore.collection("Carousel").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                pathId.clear();
                for(DocumentSnapshot snapshot: value.getDocuments()){
//                    pathId.add(snapshot.get("image").toString());
                    storage.getReference("carousel-images/"+snapshot.get("image").toString())
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                        Log.i(TAG,"fterter"+uri.toString());
                                    items.add(new CarouselItem(uri.toString()));
                                    CarouselPagerAdapter adapter = new CarouselPagerAdapter(view.getContext(), items);
                viewPager.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                                }
                            });

                }

            }

        });



    }




}