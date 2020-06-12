package com.example.pratisthasthapit.therealchef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        /**
         * Opens login activity.
         */
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        /**
         * Allows user to register for the app.
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait..");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                /**
                 * Checks if all text fields are entered.
                 */
                if (TextUtils.isEmpty(str_username)||TextUtils.isEmpty(str_fullname)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password))
                {
                    Toast.makeText(RegisterActivity.this, "Please fill in the empty fields!", Toast.LENGTH_SHORT).show();
                }
                else if (str_password.length() < 8)
                {
                    Toast.makeText(RegisterActivity.this, "Password must have atleast 8 characters", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(str_username, str_fullname, str_email, str_password);
                }
            }
        });
    }

    /**
     * The email verification function was inspired by
     * https://www.youtube.com/watch?v=06YKlMdWyMM
     */
    /**
     * Add user details in the database
     * @param username: username of user
     * @param fullname: fullname of user
     * @param email: email of user
     * @param password: password of user
     */
    private  void register(final String username, final String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                /**
                 * Checks if the email has been previously used for another account
                 * Add the user detail in the database using HashMap
                 */
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", username.toLowerCase());
                    hashMap.put("fullname", fullname);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/the-real-chef.appspot.com/o/placeholder.png?alt=media&token=a558b6b6-bb00-420a-a704-16fd8a307687");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();

                                /**
                                 * Send verification email to the user's entered email address
                                 */
                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "Registered successfully. Please check your email for verification", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "You cannot register with this email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
