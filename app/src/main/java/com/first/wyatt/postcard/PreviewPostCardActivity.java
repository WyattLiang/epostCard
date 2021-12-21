package com.first.wyatt.postcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.first.wyatt.postcard.PostcardList.postCards;

public class PreviewPostCardActivity extends AppCompatActivity {


    private ImageView imageView;
    private Button save;
    private Button toMain;
    private Uri uri;

    private FirebaseAuth mAuth;
    private DatabaseReference upload;
    private StorageReference mRef;
    private String uid;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_post_card);

        imageView = findViewById(R.id.postcard_generate);
        save = findViewById(R.id.save_postcard);
        toMain = findViewById(R.id.back_to_main);
        mAuth = FirebaseAuth.getInstance();
        upload = FirebaseDatabase.getInstance().getReference().child("postcards");
        uid = mAuth.getCurrentUser().getUid();

        String uri_str = getIntent().getStringExtra("Image"); /*receive uri sent from editcard activity*/
        uri = Uri.parse(uri_str);
        imageView.setImageURI(uri);
    }

    public void backToMainPage(View view){
        Intent backTomain = new Intent(PreviewPostCardActivity.this,MainActivity.class);
        if(isSaved){
            backTomain.putExtra("isSaved",true);
            startActivity(backTomain);
        }else{
            startActivity(backTomain);
        }
    }

    public void uploadPostCard(View view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                File file = new File(uri.toString());
                try {
                    InputStream fileInputStream = new FileInputStream(file);
                    mRef = FirebaseStorage.getInstance().getReference().child(uid + System.currentTimeMillis() + ".jpg");
                    UploadTask uploadTask = mRef.putStream(fileInputStream);  /*upload postcard to storage first*/

                    /*create another task to download Uri for image just uploaded to storage*/
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }
                            return mRef.getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()){
                                                Uri downUri = task.getResult();
                                                String uri = String.valueOf(downUri);
                                                String dir = String.valueOf(System.currentTimeMillis()); //use currentMillis as child node for each postcard

                                                upload.child(uid).child(dir).setValue(uri) /*upload this uri to database*/
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(PreviewPostCardActivity.this,"Your postcard has been uploaded successfully",Toast.LENGTH_LONG).show();
                                                                    postCard new_post = new postCard(uri);
                                                                    postCards.add(new_post);  /*also update the local cache*/
                                                                    isSaved = true;
                                                                }else{
                                                                    Toast.makeText(PreviewPostCardActivity.this,"Failed to upload postcard, Please try later",Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}