package com.first.wyatt.postcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText search_box;
    private ImageButton search_btn;
    private RecyclerView friend_list;
    private DatabaseReference allUserDatabase;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private HashMap<String,User> record;
    private Boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        search_box = findViewById(R.id.search_box);
        search_btn =findViewById(R.id.search_btn);
        friend_list = findViewById(R.id.friend_list);
        //toolbar = findViewById(R.id.toolbar);

        friend_list.setHasFixedSize(true);
        friend_list.setLayoutManager(new LinearLayoutManager(this));
        allUserDatabase = FirebaseDatabase.getInstance().getReference("users");

        record = new HashMap<>();

    }

    public void search(View view){
        String email = search_box.getText().toString();
        setRecyclerView(email);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isListening){
            firebaseRecyclerAdapter.stopListening();
        }
    }
    private void setRecyclerView(String email){

        Query SearchPeopleQuery = allUserDatabase.orderByChild("email").startAt(email).endAt(email);
        FirebaseRecyclerOptions<User>options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(SearchPeopleQuery,User.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, FindFriendsViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull User model) {
                if(!model.getprofile_Url().equals("noProfile"))
                    holder.setProfileImage(model.getprofile_Url());
                holder.setEmail(model.getemail());
                record.put(String.valueOf(position),model);
            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_friend_info,parent,false);

                return new FindFriendsViewHolder(view);
            }
        };
        friend_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        isListening = true;
    }

    public class FindFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView userEmail;
        private CircleImageView profileImage;
        private ImageButton share_btn;
        String email;
        String Id_friend;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_friend);
            userEmail = itemView.findViewById(R.id.email_friend);
            share_btn = itemView.findViewById(R.id.share_btn);

            share_btn.setOnClickListener(this);
        }


        public void setProfileImage(String profile_image){
            Picasso.with(FindFriendsActivity.this).load(profile_image).into(profileImage);
        }
        public void setEmail(String email){
            userEmail.setText(email);
        }

        @Override
        public void onClick(View view) {
            User user = record.get(String.valueOf(getAdapterPosition()));
            email = user.getemail();

            allUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("RESULT",dataSnapshot+"");
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        DataSnapshot dataS = ds;
                        if(dataS.child("email").getValue(String.class).equals(email)){
                            Log.d("USER ID:", dataS.child("email").getValue(String.class)+"");
                            Id_friend = ds.getKey();
                            break;
                        }
                    }

                    allUserDatabase.removeEventListener(this);
                    /*open another activity and send Id_friend to it */
                    Intent selectImage = new Intent(FindFriendsActivity.this,SelectPostCardActivity.class);
                    selectImage.putExtra("userID",Id_friend);
                    startActivity(selectImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }
    }


}