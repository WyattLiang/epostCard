package com.first.wyatt.postcard;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private ImageButton add_image;
    private EditText user_name;
    private EditText first_name;
    private EditText last_name;
    private EditText phone;
    private Button update;
    private Button cancel;

    private Uri profileImageUri;
    private StorageReference myStorage;
    private String uri;
    private String pre_user_name;
    private String pre_user_profile;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image = findViewById(R.id.profile_image);
        add_image = findViewById(R.id.add_profile);
        user_name = findViewById(R.id.user_name_update);
        first_name = findViewById(R.id.firstN_update);
        last_name = findViewById(R.id.lastN_update);
        phone = findViewById(R.id.phone_update);
        update = findViewById(R.id.update_btn);
        cancel = findViewById(R.id.cancel_btn);

        pre_user_name = getIntent().getStringExtra("user_name");
        pre_user_profile = getIntent().getStringExtra("user_uri");

        Log.d("Pre_user_name", pre_user_name);
        Log.d("pre_user_profile", pre_user_profile);
        if(!pre_user_profile.equals("noProfile")){
            Picasso.with(this).load(pre_user_profile).into(profile_image);
        }
        user_name.setText(pre_user_name);
    }

    public void updateInfo(View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String userName = user_name.getText().toString();
                String firstN = first_name.getText().toString();
                String lastN = last_name.getText().toString();
                String phoneNo = phone.getText().toString();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                /*get database reference*/
                mDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(firstN) || TextUtils.isEmpty(lastN) || TextUtils.isEmpty(phoneNo)) {
                    Toast.makeText(EditProfileActivity.this, "Please fill all the information above", Toast.LENGTH_LONG).show();
                }
                else{
                    {
                        User user = new User(email, userName, firstN, lastN, phoneNo);

                        /*check if user upload profile image or not*/
                        if (!TextUtils.isEmpty(uri)) {
                            user.setprofile_Url(uri);

                        }else if(!TextUtils.isEmpty(pre_user_profile) && TextUtils.isEmpty(uri)){
                            user.setprofile_Url(pre_user_profile);
                        }
                        mDatabase.setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful())
                                            Toast.makeText(EditProfileActivity.this, "Updated failed, Please try later",Toast.LENGTH_LONG).show();
                                        else{
                                            Bundle extra = new Bundle();
                                            extra.putString("userName",userName);
                                            extra.putString("uri",uri);
                                            Intent intent = new Intent();
                                            intent.putExtra("extra",extra);
                                            setResult(Activity.RESULT_OK,intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                }

            }
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    profileImageUri = result;
                    profile_image.setImageURI(result);
                    myStorage = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
                    UploadTask uploadTask = myStorage.putFile(profileImageUri);

                    Task<Uri>urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return myStorage.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                profileImageUri = task.getResult();
                                uri = String.valueOf(profileImageUri);
                            }
                        }
                    });
                }
            });

    public void chooseProfileImage(View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGetContent.launch("image/*");
                //Log.d("Image: ", profile_image.toString());
            }
        });
    }

    public void cancelUpdate(View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(EditProfileActivity.this,MainActivity.class);
                startActivity(main);
            }
        });
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==RESULT_OK && data != null){
            profileImageUri = data.getData();
            profile_image.setImageURI(profileImageUri);

            myStorage = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
            UploadTask uploadTask = myStorage.putFile(profileImageUri);

            Task<Uri>urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return myStorage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>(){

                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        profileImageUri = task.getResult();
                        uri = String.valueOf(profileImageUri);
                    }
                }
            });

        }
    }*/

}