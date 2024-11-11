package lk.jiat.app.happycart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import lk.jiat.app.happycart.Dialog.SignUpConfirmDialog;
import lk.jiat.app.happycart.dao.UserDao;
import lk.jiat.app.happycart.entity.User;
import lk.jiat.app.happycart.util.AppDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();
//                showCustomDialog();
            }
        });
        findViewById(R.id.tvSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void internalDb(String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_db").build();
                UserDao userDao = appDb.userDao();

                User user1 = new User();
                user1.setEmail(email);

                userDao.insert(user1);

            }
        }).start();


    }


    private void registerUser() {
        EditText editFirstName = findViewById(R.id.etFirstName);
        EditText editLastName = findViewById(R.id.etLastName);
        EditText editEmail = findViewById(R.id.etEmailSignUp);
        EditText editPassword = findViewById(R.id.etPasswordSignUp);
        EditText editRetypePassword = findViewById(R.id.etRePasswordSignUp);

        final String firstName = editFirstName.getText().toString();
        final String lastName = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String retypePassword = editRetypePassword.getText().toString();

        // Validate input fields
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email address");
            editEmail.requestFocus();
            return;
        }

        if (firstName.isEmpty()) {
            editFirstName.setError("Enter a First Name");
            editFirstName.requestFocus();
            return;
        }if (lastName.isEmpty()) {
            editLastName.setError("Enter a Last Name");
            editLastName.requestFocus();
            return;
        }if (password.isEmpty()) {
            editPassword.setError("Enter a password");
            editPassword.requestFocus();
            return;
        }if (retypePassword.isEmpty()) {
            editRetypePassword.setError("Enter a retype password");
            editRetypePassword.requestFocus();
            return;
        }
        if (!password.equals(retypePassword)) {
            // Passwords do not match, show an error message
            editRetypePassword.setError("Passwords do not match");
            editRetypePassword.requestFocus();
            return;
        }

        // Validate input fields (you can add your own validation logic)

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success, update user profile with first and last name
                            FirebaseUser user = auth.getCurrentUser();
                            user.sendEmailVerification();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " " + lastName)
                                        .build();
                                user.updateProfile(profileUpdates);
                                Toast.makeText(SignUpActivity.this,"Successfully Registered. Please Verify Your Email",Toast.LENGTH_LONG).show();

                                internalDb(email);

                            }

                            // You can add further actions here, such as navigating to the home screen
                        } else {
                            // Registration failed, handle the error
                            // You can show an error message or log the error
                            Toast.makeText(SignUpActivity.this,"Registration Failed",Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

}