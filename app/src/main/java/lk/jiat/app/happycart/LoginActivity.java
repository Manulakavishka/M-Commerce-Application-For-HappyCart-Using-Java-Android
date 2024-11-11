package lk.jiat.app.happycart;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import lk.jiat.app.happycart.dao.UserDao;
import lk.jiat.app.happycart.entity.User;
import lk.jiat.app.happycart.util.AppDatabase;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getName();
    private FirebaseAuth firebaseAuth;
    private SignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loading();

        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(getApplicationContext());

        EditText emailEditText = findViewById(R.id.etEmail);
        EditText passwordEditText = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(!user.isEmailVerified()){
                    Toast.makeText(LoginActivity.this,"Please Verify your email",Toast.LENGTH_LONG).show();
                }else {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                    // Validate input fields
                    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailEditText.setError("Enter a valid email address");
                        emailEditText.requestFocus();
                        return;
                    }

                    if (password.isEmpty()) {
                        passwordEditText.setError("Enter a password");
                        passwordEditText.requestFocus();
                        return;
                    }


                    firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    updateUi(firebaseAuth.getCurrentUser());
                                }else {
                                        passwordEditText.setError("Email Or Password Wrong. Please Check again");
                                        passwordEditText.requestFocus();
                                        return;
                                }
                            }
                        });
                }
            }
        });


        findViewById(R.id.btnOnetapLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeginSignInRequest oneTapRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(
                                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                        .setSupported(true)
                                        .setServerClientId(getString(R.string.web_client_id))
                                        .setFilterByAuthorizedAccounts(true)
                                        .build()
                        ).build();
                Task<BeginSignInResult> beginSignInResultTask = signInClient.beginSignIn(oneTapRequest);
                beginSignInResultTask.addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest
                                .Builder(beginSignInResult.getPendingIntent()).build();
                        signInLauncher.launch(intentSenderRequest);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });


        findViewById(R.id.btnGoogleLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSignInIntentRequest signInIntentRequest = GetSignInIntentRequest.builder()
                        .setServerClientId(getString(R.string.web_client_id)).build();

                Task<PendingIntent> signInIntent = signInClient.getSignInIntent(signInIntentRequest);
                signInIntent.addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest
                                .Builder(pendingIntent).build();
                        signInLauncher.launch(intentSenderRequest);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        findViewById(R.id.tvAppName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //go to sign up
        findViewById(R.id.tvSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loading() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDb = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_db").build();
                UserDao userDao = appDb.userDao();

                User user = userDao.getById(1);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.etEmail);
                        textView.setText(user.getEmail());
                    }
                },5000);

            }
        }).start();

    }

    private void updateUi(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        Task<AuthResult> authResultTask = firebaseAuth.signInWithCredential(authCredential);
        authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUi(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void handleSignInResult(Intent intent){
        try {
            SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(intent);
            String idToken = signInCredential.getGoogleIdToken();
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            Log.i(TAG,e.getMessage());
        }
    }
    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            handleSignInResult(o.getData());
                        }
                    });
}