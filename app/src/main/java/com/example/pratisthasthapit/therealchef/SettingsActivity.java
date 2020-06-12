package com.example.pratisthasthapit.therealchef;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ImageView closeBtn, userImage;
    TextView save, change_userImage;
    MaterialEditText fullname, username, bio;
    FirebaseUser firebaseUser;

    private Uri imageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        closeBtn = findViewById(R.id.closeBtn);
        userImage = findViewById(R.id.userImage);
        save = findViewById(R.id.save);
        change_userImage = findViewById(R.id.change_userImage);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("store");

        /**
         * Get current user's data from database
         */
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Update image and return back to menu.
         */
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
                finish();
            }
        });

        /**
         * Allows user to select and crop the new user image.
         */
        change_userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL).start(SettingsActivity.this);
            }
        });

        /**
         * Display user's current user image.
         */
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL).start(SettingsActivity.this);
            }
        });

        /**
         * Save updated fullname, username and bio.
         * Close current activity
         */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile(fullname.getText().toString(), username.getText().toString(), bio.getText().toString());
                finish();
            }
        });
    }

    /**
     * Save the updated fullname, username and bio in the database.
     * @param fullname: fullname of user
     * @param username: username of user
     * @param bio: bio of user
     */
    private void saveProfile(String fullname, String username, String bio) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fullname", fullname);
        hashMap.put("username", username);
        hashMap.put("bio", bio);

        databaseReference.updateChildren(hashMap);
    }

    /*
    * The function getMimeTypeExtension()
    * has been taken from https://www.codota.com/code/java/methods/android.webkit.MimeTypeMap/getExtensionFromMimeType
    * under the "Utils.getMimeType(...)" snippet
    * */
    private String getMimeTypeExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /**
     * The following code was inspired by
     * https://www.youtube.com/watch?v=3NYIwEpYbOA&list=PLzLFqCABnRQduspfbu2empaaY9BoIGLDM&index=16
     */
    /**
     * Update the user image
     */
    private void updateImage(){

        /**
         * Checks if an image has been selected
         */
        if (imageUri !=null){
            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis()+"."+ getMimeTypeExtension(imageUri));

            uploadTask = storageReference1.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri savedUri = task.getResult();
                        String userUri = savedUri.toString();

                        /**
                         * Update database using HashMap
                         */
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", ""+userUri);

                        reference.updateChildren(hashMap);
                        Toast.makeText(SettingsActivity.this, "Update successful!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SettingsActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(SettingsActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Crops and updates the image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            imageUri = activityResult.getUri();

            updateImage();
        }
        else {
            Toast.makeText(SettingsActivity.this, "Error updating info, Please try again!", Toast.LENGTH_SHORT).show();
        }
    }
}
