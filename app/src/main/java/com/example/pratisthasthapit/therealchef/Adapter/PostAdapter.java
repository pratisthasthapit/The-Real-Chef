package com.example.pratisthasthapit.therealchef.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pratisthasthapit.therealchef.Post;
import com.example.pratisthasthapit.therealchef.R;
import com.example.pratisthasthapit.therealchef.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> postList;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postList){
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout, viewGroup, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder viewHolder,final int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = postList.get(i);

        Glide.with(context).load(post.getRecipeImage())
                .apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(viewHolder.recipeImage);

        if (post.getRecipeName().equals("")){
            viewHolder.recipeName.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.recipeName.setVisibility(View.VISIBLE);
            viewHolder.recipeName.setText(post.getRecipeName());
        }

        if (post.getIngredient().equals("")){
            viewHolder.ingredient.setVisibility(View.GONE);
        }
        else {
            viewHolder.ingredient.setVisibility(View.VISIBLE);
            viewHolder.ingredient.setText(post.getIngredient());
        }

        if (post.getMethod().equals("")){
            viewHolder.method.setVisibility(View.GONE);
        }
        else {
            viewHolder.method.setVisibility(View.VISIBLE);
            viewHolder.method.setText(post.getMethod());
        }

        chefInfo(viewHolder.userImage, viewHolder.username, viewHolder.chef, post.getChef());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView userImage, recipeImage, likeImage, commentImage, saveImage;
        public TextView username, numLikes, chef, recipeName, comment, ingredient, method;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            likeImage = itemView.findViewById(R.id.likeImage);
            commentImage = itemView.findViewById(R.id.commentImage);
            saveImage = itemView.findViewById(R.id.saveImage);
            username = itemView.findViewById(R.id.username);
            numLikes = itemView.findViewById(R.id.numLikes);
            chef = itemView.findViewById(R.id.chef);
            recipeName = itemView.findViewById(R.id.recipeName);
            comment = itemView.findViewById(R.id.comment);
            ingredient = itemView.findViewById(R.id.ingredient);
            method = itemView.findViewById(R.id.method);

        }
    }

    private void chefInfo(final ImageView userImage, final TextView username, final TextView chef, String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(userImage);
                username.setText(user.getUsername());
                chef.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
