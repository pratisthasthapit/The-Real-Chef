package com.example.pratisthasthapit.therealchef;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context context;
    private List<Comment> commentList;
    private String recipeId;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context context, List<Comment> commentList, String recipeId) {
        this.context = context;
        this.commentList = commentList;
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = commentList.get(i);

        viewHolder.comment.setText(comment.getComment());
        getChefInfo(viewHolder.userImage, viewHolder.username, comment.getChef());

        /**
         * Opens user profile
         */
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("chefId", comment.getChef());
                context.startActivity(intent);
           }
        });

        /**
         * Opens user profile
         */
        viewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("chefId", comment.getChef());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView userImage;
        public TextView comment, username;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    /** Gets the user information from firebase database
     * @param userImage: user image of the user who posted the comment
     * @param username: username of the user who posted the comment
     * @param chefId: user id of the user who posted the comment
     */
    private  void getChefInfo(final ImageView userImage, final TextView username, String chefId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(chefId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(userImage);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
