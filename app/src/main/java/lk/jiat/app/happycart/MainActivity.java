package lk.jiat.app.happycart;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.app.happycart.Adapter.CarouselPagerAdapter;
import lk.jiat.app.happycart.Model.CarouselItem;
import lk.jiat.app.happycart.Model.User;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    // Assuming you have the user's document ID (userId)
    private FirebaseFirestore firestore;

    private FirebaseStorage storage;
    private Uri imagePath;
    private String userId;
    public final static String TAG = MainActivity.class.getName();
    private String imageId;

    private SensorManager sensorManager;
    private Sensor acceleroter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{
                Manifest.permission.ACTIVITY_RECOGNITION
        },100);

//        loadHome();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        if (user != null) {
            // User is signed in
            userId = user.getUid();
            // Now 'userId' contains the unique identifier (UID) for the currently signed-in user
        } else {
            userId=null;
            // User is not signed in
            // Handle the situation accordingly
        }


        if(user!=null && user.isEmailVerified()){
            userLogin(user);
        }else {
            goToLogin();
        }

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdvanceSearchActivity.class);
                startActivity(intent);
//                Toast.makeText(MainActivity.this, "Not yet ready",Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.textInputSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdvanceSearchActivity.class);
                startActivity(intent);
            }
        });

    }


    // Method to reset the color of all icons to the default color
    private void resetAllIconColors(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable iconDrawable = menuItem.getIcon();

            // Reset the color of the icon to the default color
            iconDrawable.setColorFilter(null);
        }
    }
    // Method to change the icon color for a specific item
    private void changeIconColor(MenuItem item, @ColorRes int colorResource) {
        Drawable iconDrawable = item.getIcon();

        // Tint the icon drawable with the desired color
        iconDrawable.setColorFilter(ContextCompat.getColor(this, colorResource), PorterDuff.Mode.SRC_IN);

        // Set the tinted icon drawable back to the menu item
        item.setIcon(iconDrawable);
    }


    // Method to close the navigation drawer
    private void closeDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer from the start (left) side
//        loadHome();
    }

    private void showFragment(String fragmentName, Fragment fragment,NavigationView navigationView) {

        // Create a FragmentTransaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Check if the fragment is already added to avoid adding it multiple times
        if (getSupportFragmentManager().findFragmentByTag(fragmentName) == null) {
            // If not added, add the fragment
            transaction.replace(R.id.contentContainer, fragment, fragmentName);
        }


        // Commit the transaction
        transaction.commit();
        closeDrawer();
    }

    private void bottomNavi(String fragmentName, Fragment fragment){
        // Create a FragmentTransaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Check if the fragment is already added to avoid adding it multiple times
        if (getSupportFragmentManager().findFragmentByTag(fragmentName) == null) {
            // If not added, add the fragment
            transaction.replace(R.id.contentContainer, fragment, fragmentName);
        }
        // Commit the transaction
        transaction.commit();
        // Handle Home item click
//        loadHome();
    }

    private void userLogin(FirebaseUser user){
        BottomNavigationView bottomNavigationView= findViewById(R.id.bottomNavigation);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Customize item icon and text color
        bottomNavigationView.setItemIconTintList(null); // To apply individual color to each icon
        bottomNavigationView.setItemIconTintList(getResources().getColorStateList(R.color.green));
//        bottomNavigationView.setItemTextColor(getResources().getColorStateList(R.color.red));

//        bottomNavigationView.getMenu().findItem()

        // Set listener to handle item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Log.i(TAG,"Manula : "+item);

                if(item.toString().equals("Home")){
                    bottomNavi("HomeFragment",new HomeFragment());
                    return true;
                    // Add cases for other items as needed
                } else if(item.toString().equals("Profile")){
                    bottomNavi("ProfileFragment",new ProfileFragment());
                    return true;
                    // Add cases for other items as needed
                } else if(item.toString().equals("Explore")){
                    Intent intent = new Intent(MainActivity.this, AdvanceSearchActivity.class);
                    startActivity(intent);
                    return true;

                }else if(item.toString().equals("Cart")){
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    return true;

                }
                else {
                    return false;
                }

            }
        });

        // Assuming you have a reference to your NavigationView


// Create a ColorStateList with your desired color
        ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green));

// Set the itemIconTint dynamically
        navigationView.setItemIconTintList(colorStateList);

        // Find the menu item by ID
        navigationView.getMenu().findItem(R.id.sideNavLogin).setVisible(false);
        navigationView.getMenu().findItem(R.id.sideNavLogout).setVisible(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                // Get Firebase Authentication instance
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
// Log out the current user
                firebaseAuth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.sideNavProfile).setVisible(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                showFragment("ProfileFragment", new ProfileFragment(), navigationView);
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.sideNavOrders).setVisible(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                UiChange();
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.sideNavWishlist).setVisible(true);
        navigationView.getMenu().findItem(R.id.sideNavMessage).setVisible(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
                return false;
            }
        });
        navigationView.getMenu().findItem(R.id.sideNavSettings).setVisible(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                autoBrightness();
                return true;
            }
        });
        navigationView.getMenu().findItem(R.id.sideNavHome).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                showFragment("HomeFragment", new HomeFragment(),navigationView);

                return true;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                resetAllIconColors(navigationView);

                // Check if the clicked item matches the one you want to change
                if (item.getItemId() == R.id.sideNavProfile) {
                    // Change the icon color for the specific item
                    item.setIcon(R.drawable.baseline_home_24);
                }

                // Perform other actions based on item clicks

                return true;
            }
        });

        loadData(navigationView);





    }


    private void UiChange() {
        Intent intent = new Intent(MainActivity.this, OrderHistoryActivity.class);
        startActivity(intent);
    }

    private void loadData(NavigationView navigationView){

        DocumentReference docRef = firestore.collection("Users").document(userId);

        Source source = Source.DEFAULT;

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {


                        View headerView = navigationView.getHeaderView(0);

                        // Find the views within the header layout and update them
                        TextView textViewUsername = headerView.findViewById(R.id.userName);
                        TextView textViewEmail = headerView.findViewById(R.id.userEmail);
                        ImageView imageView = headerView.findViewById(R.id.profilePic);

                                textViewUsername.setText(document.get("firstName")+ " " + document.get("lastName"));
                                textViewEmail.setText(firebaseAuth.getCurrentUser().getEmail());
//                                imageView.setImageResource(R.drawable.baseline_person_24);


                        storage.getReference("user-images/"+document.get("image"))
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.get()
                                                .load(uri)
                                                .resize(200,200)
                                                .centerCrop()
                                                .into(imageView);
                                        imageId=document.get("image").toString();

                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


    private void goToLogin(){
        // Assuming you have a reference to your NavigationView
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Find the menu item by ID
        MenuItem loginMenuItem = navigationView.getMenu().findItem(R.id.sideNavLogin);

        // Now you can perform operations on the loginMenuItem
        // For example, set an onMenuItemClickListener
        loginMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the click event here
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true; // Return true to consume the click event
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){

            case Sensor.TYPE_LIGHT:
                WindowManager.LayoutParams layoutParams= getWindow().getAttributes();
                layoutParams.screenBrightness= event.values[0]/255.0f;
                getWindow().setAttributes(layoutParams);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void autoBrightness() {
        sensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null){
            acceleroter = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            Log.i(TAG,acceleroter.toString());
        }

        if(acceleroter !=null){
            sensorManager.registerListener(MainActivity.this,acceleroter,SensorManager.SENSOR_DELAY_UI);
        }
        }
}