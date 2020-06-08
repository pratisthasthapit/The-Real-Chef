package com.example.pratisthasthapit.therealchef.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pratisthasthapit.therealchef.Fragment.RecipeDetailFragment;
import com.example.pratisthasthapit.therealchef.Post;
import com.example.pratisthasthapit.therealchef.R;

import java.util.List;

public class UserRecipeAdapter extends RecyclerView.Adapter<UserRecipeAdapter.ViewHolder>{

    private Context context;
    private List<Post> postList;

    public UserRecipeAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public UserRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_post_layout, viewGroup, false);
        return new UserRecipeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Post post = postList.get(i);
        Glide.with(context).load(post.getRecipeImage()).into(viewHolder.recipeImage);

        if (post.getRecipeName().equals("")){
            viewHolder.recipeName.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.recipeName.setVisibility(View.VISIBLE);
            viewHolder.recipeName.setText(post.getRecipeName());
        }

        viewHolder.recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("recipeid", post.getRecipeId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecipeDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView recipeImage;
        public TextView recipeName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
        }
    }
}
