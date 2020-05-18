package com.example.pratisthasthapit.therealchef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {

    EditText commentText;
    ImageView userImage;
    TextView postComment;

    String recipeId;
    String chefId;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        commentText = findViewById(R.id.commentText);
        userImage = findViewById(R.id.userImage);
        postComment = findViewById(R.id.postComment);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        recipeId = intent.getStringExtra("recipeId");
        chefId = intent.getStringExtra("chefId");

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentText.getText().toString().equals("")){
                    Toast.makeText(CommentActivity.this, "You cannot send empty comment", Toast.LENGTH_SHORT).show();
                }
                else {
                    addComment();
                }
            }
        });

        getRecipeImage();



    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(recipeId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", commentText.getText().toString());
        hashMap.put("chef", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        commentText.setText("");
    }

    private  void getRecipeImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
