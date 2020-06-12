package com.example.pratisthasthapit.therealchef;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pratisthasthapit.therealchef.UserRecipeAdapter;
import com.example.pratisthasthapit.therealchef.MenuActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView userImage, menuOption;
    TextView numRecipes, numFollowers, numFollowing, fullname, userBio, username;
    Button followBtn;
    ImageButton my_recipes, my_saved_recipe;

    FirebaseUser firebaseUser;
    String profileid;

    private RecyclerView myRecipe_recyclerView;
    private UserRecipeAdapter UserRecipeAdapter;
    private List<Post> postList;

    private List<String> mySaved;
    private RecyclerView mySaved_recyclerView;
    private UserRecipeAdapter SavedRecipeAdapter;
    private List<Post> saved_postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        userImage = view.findViewById(R.id.userImage);
        menuOption = view.findViewById(R.id.menuOption);
        numRecipes = view.findViewById(R.id.numRecipes);
        numFollowers = view.findViewById(R.id.numFollowers);
        numFollowing = view.findViewById(R.id.numFollowing);
        fullname = view.findViewById(R.id.fullname);
        userBio = view.findViewById(R.id.userBio);
        username = view.findViewById(R.id.username);
        followBtn = view.findViewById(R.id.followBtn);
        my_recipes = view.findViewById(R.id.my_recipes);
        my_saved_recipe = view.findViewById(R.id.my_saved_recipe);

        /**
         * Recycler view containing the user's posts
         */
        myRecipe_recyclerView = view.findViewById(R.id.myRecipe_recyclerView);
        myRecipe_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        myRecipe_recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        UserRecipeAdapter = new UserRecipeAdapter(getContext(), postList);
        myRecipe_recyclerView.setAdapter(UserRecipeAdapter);

        /**
         * Recycler view containing saved recipes
         */
        mySaved_recyclerView = view.findViewById(R.id.mySaved_recyclerView);
        mySaved_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 2);
        mySaved_recyclerView.setLayoutManager(linearLayoutManager1);
        saved_postList = new ArrayList<>();
        SavedRecipeAdapter = new UserRecipeAdapter(getContext(), saved_postList);
        mySaved_recyclerView.setAdapter(SavedRecipeAdapter);

        /**
         * Setting the recycler view with the user's posts to display initially.
         */
        myRecipe_recyclerView.setVisibility(View.VISIBLE);
        mySaved_recyclerView.setVisibility(View.GONE);

        userInfo();
        displayFollowers();
        getNumRecipe();
        myRecipe();
        mySavedRecipes();

        /**
         * If the profile fragment displays the user's own profile, the follow/following button is disabled.
         * Else the option to display saved list of recipes is disabled.
         */
        if(profileid.equals(firebaseUser.getUid())){
            followBtn.setVisibility(View.GONE);
        }
        else
        {
            /**
             * Check if the user is followed or not by the current user
             */
            checkFollowing();
            my_saved_recipe.setVisibility(View.GONE);
        }

        /**
         * The following code was inspired by
         * https://www.youtube.com/watch?v=59ibixMg4ck
         * and has been applied else where in app.
         */
        /**
         * On clicking the followBtn, the database is updated such that
         * the user is added to/removed from followers/following list.
         */
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnText = followBtn.getText().toString();

                if (btnText.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                }
                else if (btnText.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });



        /**
         * On clicking my_recipes, the recycler view containing
         * list of the user's recipes are displayed
         */
        my_recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRecipe_recyclerView.setVisibility(View.VISIBLE);
                mySaved_recyclerView.setVisibility(View.GONE);
            }
        });

        /**
         * On clicking my_saved_recipe, the recycler view containing
         * list of saved recipes are displayed
         */
        my_saved_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRecipe_recyclerView.setVisibility(View.GONE);
                mySaved_recyclerView.setVisibility(View.VISIBLE);
            }
        });

        /**
         * Opens menu activity
         */
        menuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Displays user's information in profile fragment.
     */
    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext() == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(userImage);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                userBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Checks the status of the user and changes the
     * followBtn text from following to follow button or vice versa.
     */
    private void checkFollowing(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    followBtn.setText("following");
                }
                else {
                    followBtn.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Gets the total number of followers and following from the database
     * and displays it in the profile fragment.
     */
    private void displayFollowers(){

        /**
         * Getting number of followers
         */
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numFollowers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /**
         * Getting number of following
         */
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numFollowing.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Gets the total number of recipes posted by the user from the database
     * and displays it in the profile fragment.
     */
    private void getNumRecipe(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getChef().equals(profileid)){
                        i++;
                    }
                }
                numRecipes.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Gets the list of recipes posted by the user and adds it to postList in reverse order
     * Notifies the UserRecipeAdapter of any changes and updates the list
     */
    private void myRecipe(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getChef().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                UserRecipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * The following function was inspired by
     * https://www.youtube.com/watch?v=OH3PgaUv-nA
     * and has been applied else where in app.
     */
    /**
     * Gets the list of recipes saved and adds to mySaved list
     */
    private void mySavedRecipes(){
        mySaved = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaved.add(snapshot.getKey());
                }
                getSavedRecipe();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * The following function was inspired by
     * https://www.youtube.com/watch?v=OH3PgaUv-nA
     * and has been applied else where in app.
     */
    /**
     * Gets the list of recipes saved by the user and adds it to saved_postList
     * Notifies the SavedRecipeAdapter of any changes and updates the list
     */
    private void getSavedRecipe() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saved_postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : mySaved){
                        if (post.getRecipeId().equals(id)){
                            saved_postList.add(post);
                        }
                    }
                }
                SavedRecipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
