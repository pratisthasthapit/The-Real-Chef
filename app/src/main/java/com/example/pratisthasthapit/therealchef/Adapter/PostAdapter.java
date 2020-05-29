package com.example.pratisthasthapit.therealchef.Adapter;

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
import com.bumptech.glide.request.RequestOptions;
import com.example.pratisthasthapit.therealchef.CommentActivity;
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
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout, viewGroup, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder viewHolder, final int i) {
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
        isLiked(post.getRecipeId(), viewHolder.likeImage);
        isSaved(post.getRecipeId(), viewHolder.saveImage);
        numLikes(viewHolder.numLikes, post.getRecipeId());
        getComments(post.getRecipeId(), viewHolder.comment);

        viewHolder.saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.saveImage.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getRecipeId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getRecipeId()).removeValue();
                }
            }
        });

        viewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.likeImage.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getRecipeId())
                            .child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getRecipeId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        viewHolder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("recipeId", post.getRecipeId());
                intent.putExtra("chefId", post.getChef());
                context.startActivity(intent);

            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("recipeId", post.getRecipeId());
                intent.putExtra("chefId", post.getChef());
                context.startActivity(intent);

            }
        });
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

    private void getComments(String recipeId, final TextView comment){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(recipeId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0)
                {
                    comment.setText("");
                }
                else if (dataSnapshot.getChildrenCount() == 1)
                {
                    comment.setText("View " + dataSnapshot.getChildrenCount() + " Comment");
                }
                else
                {
                    comment.setText("View all " + dataSnapshot.getChildrenCount() + " Comments");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void isLiked(String recipeId, final ImageView likedImage){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(recipeId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    likedImage.setImageResource(R.drawable.ic_like_color);
                    likedImage.setTag("liked");
                }
                else {
                    likedImage.setImageResource(R.drawable.ic_like);
                    likedImage.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String recipeId, final ImageView savedImage){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(recipeId).exists()){
                    savedImage.setImageResource(R.drawable.ic_saved);
                    savedImage.setTag("saved");
                }
                else {
                    savedImage.setImageResource(R.drawable.ic_save);
                    savedImage.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void numLikes(final TextView likeText, String recipeId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(recipeId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0)
                {
                    likeText.setText("");
                }
                else if(dataSnapshot.getChildrenCount() == 1){
                    likeText.setText(dataSnapshot.getChildrenCount()+ " like");
                }
                else{
                    likeText.setText(dataSnapshot.getChildrenCount()+ " likes");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
