package com.example.pratisthasthapit.therealchef;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pratisthasthapit.therealchef.PostAdapter;
import com.example.pratisthasthapit.therealchef.Post;
import com.example.pratisthasthapit.therealchef.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private PostAdapter postAdapter;
    private List<Post> postList;
    RecyclerView postRecyclerView;
    private List<String> listFollowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postRecyclerView = (RecyclerView)view.findViewById(R.id.postRecyclerView);
        postRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        postRecyclerView.setAdapter(postAdapter);

        checkFollowing();
        return view;
    }


    /**
     * Gets a list of "following" users and reads their posts
     */
    private void checkFollowing(){
        listFollowing = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listFollowing.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    listFollowing.add(snapshot.getKey());
                }

                /**
                 * Reads posts of "following" user
                 */
                parseData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Reads all the recipes posted by the user from the listFollowing.
     */
    private void parseData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id: listFollowing){
                        if (post.getChef().equals(id)){
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
