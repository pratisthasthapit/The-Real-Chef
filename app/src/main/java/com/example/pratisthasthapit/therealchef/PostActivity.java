package com.example.pratisthasthapit.therealchef;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String imageUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView closeImageButton;
    ImageView postImage;
    TextView postTextButton;
    EditText recipeName;
    EditText ingredient;
    EditText method;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            postImage.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadRecipe(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting recipe...");
        pd.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        imageUrl = downloadUri.toString();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

                        String recipeId = databaseReference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("recipeId", recipeId);
                        hashMap.put("recipeImage", imageUrl);
                        hashMap.put("recipeName", recipeName.getText().toString());
                        hashMap.put("ingredient", ingredient.getText().toString());
                        hashMap.put("method", method.getText().toString());
                        hashMap.put("chef", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        databaseReference.child(recipeId).setValue(hashMap);

                        pd.dismiss();

                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(PostActivity.this, "Posting failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        closeImageButton = findViewById(R.id.closeImageButton);
        postImage = findViewById(R.id.postImage);
        postTextButton = findViewById(R.id.postTextButton);
        recipeName = findViewById(R.id.recipeName);
        ingredient = findViewById(R.id.ingredient);
        method = findViewById(R.id.method);

        storageReference = FirebaseStorage.getInstance().getReference("recipes");

        closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        postTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadRecipe();
            }
        });
        CropImage.activity().setAspectRatio(1,1).start(PostActivity.this);
    }
}
