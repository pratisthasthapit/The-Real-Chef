package com.example.pratisthasthapit.therealchef.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pratisthasthapit.therealchef.Adapter.PostAdapter;
import com.example.pratisthasthapit.therealchef.Post;
import com.example.pratisthasthapit.therealchef.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class RecipeDetailFragment extends Fragment {

    String recipeId;
    private RecyclerView recipeDetail_recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        recipeId = prefs.getString("recipeid", "none");

        recipeDetail_recyclerView = view.findViewById(R.id.recipeDetail_recyclerView);
        recipeDetail_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recipeDetail_recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recipeDetail_recyclerView.setAdapter(postAdapter);

        getPost();
        return view;
    }

    /**
     * Get the details of post from the database whose details are to be displayed.
     */
    private void getPost(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes").child(recipeId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
