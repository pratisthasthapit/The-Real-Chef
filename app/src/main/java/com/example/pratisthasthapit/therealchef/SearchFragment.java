package com.example.pratisthasthapit.therealchef;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.pratisthasthapit.therealchef.UserAdapter;
import com.example.pratisthasthapit.therealchef.User;
import com.example.pratisthasthapit.therealchef.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    EditText searchBarText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.postRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBarText = view.findViewById(R.id.searchBar);

        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers);
        recyclerView.setAdapter(userAdapter);

        /**
         * Gets a list of all the users.
         */
        readUsers();

        /**
         * The following function was inspired by
         * https://www.codota.com/code/java/methods/android.widget.TextView/addTextChangedListener
         */
        /**
         * Displays the users in the recycler view as per the text in the searchbar text
         */
        searchBarText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    /**
     * Searches for a list of users from the database
     * @param key: keyword to search the user.
     */
    private void searchUsers(String key){
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(key).endAt(key+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Reads the user from the database and notifies the userAdapter of any changes occured.
     */
    private void readUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (searchBarText.getText().toString().equals("")){
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
