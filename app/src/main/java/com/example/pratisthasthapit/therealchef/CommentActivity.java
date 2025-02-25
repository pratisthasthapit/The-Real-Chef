package com.example.pratisthasthapit.therealchef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

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


        Intent intent = getIntent();
        recipeId = intent.getStringExtra("recipeId");
        chefId = intent.getStringExtra("chefId");

        recyclerView = findViewById(R.id.commentRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, recipeId);
        recyclerView.setAdapter(commentAdapter);

        commentText = findViewById(R.id.commentText);
        userImage = findViewById(R.id.userImage);
        postComment = findViewById(R.id.postComment);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        /**
         * If the commentText is empty, a Toast is displayed.
         * If commentText is not empty, addComment() is called.
         */
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

        getUserImage();
        readComments();
    }

    /**
     * The following function was inspired from
     * https://www.youtube.com/watch?v=HEJg-hvj0nE
     */
    /**
     * Adds the comment to the database using HashMap
     */
    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(recipeId);

        String commentId = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", commentText.getText().toString());
        hashMap.put("chef", firebaseUser.getUid());
        hashMap.put("commentId", commentId);

        reference.child(commentId).setValue(hashMap);
        commentText.setText("");
    }

    /**
     * The following function is inspired by
     * https://www.youtube.com/watch?v=OH3PgaUv-nA and
     * https://www.youtube.com/watch?v=GV1qbi59rgc&t=257s
     */
    /**
     * Set the user image of the comment poster in the comments
     */
    private void getUserImage(){
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

    /**
     * The following function was inspired from
     * https://www.youtube.com/watch?v=HEJg-hvj0nE
     */
    /**
     * Get a list comments for the post and add it to commentList.
     * Notifies commentAdapter of any new added comments and displays
     * it in the recyclerview
     */
    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(recipeId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
