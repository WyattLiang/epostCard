package com.first.wyatt.postcard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import static com.first.wyatt.postcard.PostcardList.postCards;


public class HomeActivity extends AppCompatActivity {
    private ViewPager showImages;
    private ImageView personal_image;
    private TextView userName;
    private ImageButton find_friend;
    private ImageButton setting;
    private ImageButton log_out;
    private ImageButton create_postCard;
    private ImageButton open_share;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo;
    private DatabaseReference mPostCards;
    private String profile_uri;
    private String pre_user_name;
    private String pre_profile_uri;

    private ViewPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        showImages = findViewById(R.id.show_images);
        personal_image = findViewById(R.id.personal_image);
        userName = findViewById(R.id.nickname);
        find_friend = findViewById(R.id.find_friend);
        setting = findViewById(R.id.setting);
        log_out = findViewById(R.id.log_out);
        create_postCard = findViewById(R.id.create_postCard);
        open_share = findViewById(R.id.open_shared);

        getUserInfo();
        PostcardList.setList();

        if(postCards.isEmpty()){
            loadUserPostcard();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new ViewPageAdapter(HomeActivity.this,postCards);
        showImages.setAdapter(adapter);
        if(getIntent().hasExtra("isSaved")){
            adapter.notifyDataSetChanged();
        }
    }
    public void setting(View view){
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("user_uri",pre_profile_uri);
            intent.putExtra("user_name",pre_user_name);
            mStartForResult.launch(intent);
            //Log.d("Checkout URI: ", profile_uri);

    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Log.d("ON RESULT:","On ActivityResult function");
                        Bundle extra = data.getBundleExtra("extra");
                        userName.setText(extra.getString("userName"));
                        profile_uri = extra.getString("uri");

                        if(!profile_uri.equals("noProfile"))
                            Picasso.with(HomeActivity.this).load(profile_uri).into(personal_image);
                    }
                }
            });

    public void openShared(View view) {
        Intent shared = new Intent(HomeActivity.this,ShowSharedPostCardActivity.class);
        startActivity(shared);
    }

    public void createPostCard(View view){
        Intent create = new Intent(HomeActivity.this, EditPostCardActivity.class);
        startActivity(create);
    }

    public void addFriend(View view){
        Intent addFriend = new Intent(HomeActivity.this, FindFriendsActivity.class);
        startActivity(addFriend);
    }

    public void logOut(View view){
        mAuth.signOut();
        postCards.clear();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void getUserInfo(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("Result: ", "On getUserInfo");
                String uid = mAuth.getCurrentUser().getUid();
                //Log.d("ID: ", "What is the id? " + uid );
                mUserInfo = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                mUserInfo.addValueEventListener(new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userN = snapshot.child("userName").getValue(String.class);
                        String image = snapshot.child("profile_Url").getValue(String.class);
                        pre_user_name = userN;
                        pre_profile_uri = image;
                        if(!image.equals("noProfile")){
                            //Picasso.get().load(image).into(personal_image);
                            Picasso.with(HomeActivity.this).load(image).into(personal_image);
                        }
                        userName.setText(userN);
                        mUserInfo.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeActivity.this,error.getMessage()+uid,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void loadUserPostcard(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ON LOADING POSTCARD:","LOADING....");
                mPostCards = FirebaseDatabase.getInstance().getReference().child("postcards").child(mAuth.getCurrentUser().getUid());
                mPostCards.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            postCard postCard = new postCard(ds.getValue(String.class));
                            postCards.add(postCard);
                            adapter.notifyDataSetChanged();
                            Log.d("LOAD RESULT:",ds.getValue()+"");
                        }
                        mPostCards.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(HomeActivity.this,"no Post card to be loaded",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}