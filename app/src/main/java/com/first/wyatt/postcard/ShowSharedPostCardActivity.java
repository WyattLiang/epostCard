package com.first.wyatt.postcard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowSharedPostCardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView sharelist;
    private Button back_to_main;
    private ArrayList<String> postcards;
    private ArrayAdapter<String> share_postcards;

    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shared_post_card);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        sharelist = findViewById(R.id.share_list);
        back_to_main = findViewById(R.id.back_to_main_from_share);
        mRef = FirebaseDatabase.getInstance().getReference().child("share").child(mAuth.getCurrentUser().getUid());
        postcards = new ArrayList<>();

        DownLoadSharePostCards();


    }

    public void backToMain(View view) {
        onBackPressed();
        finish();
    }

    public void DownLoadSharePostCards(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            postcards.add(ds.getValue(String.class));
                            Log.d("DOWNING SHARE:",ds+"");
                        }
                        setUpAdapter();
                        mRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ShowSharedPostCardActivity.this,"Oops! Looks like there is no postcard shared to you",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public static class ViewHolder{
        ImageView share_item;
        TextView hint;
    }

   @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent zoom = new Intent(ShowSharedPostCardActivity.this,ZoomPostCard.class);
        zoom.putExtra("uri",postcards.get(i));
        startActivity(zoom);
    }

    private void setUpAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                share_postcards = new ArrayAdapter<String>(ShowSharedPostCardActivity.this,R.layout.single_share_item,postcards){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        ViewHolder viewHolder;
                        if(convertView == null){
                            viewHolder = new ViewHolder();
                            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                            convertView = layoutInflater.inflate(R.layout.single_share_item,parent,false);
                            viewHolder.share_item = convertView.findViewById(R.id.share_postcard);
                            viewHolder.hint = convertView.findViewById(R.id.hint2);
                            convertView.setTag(viewHolder);
                        }else{
                            viewHolder = (ViewHolder)convertView.getTag();
                        }
                        Picasso.with(ShowSharedPostCardActivity.this).load(postcards.get(position)).resize(156,200).into(viewHolder.share_item);
                        return convertView;
                    }
                };
                sharelist.setAdapter(share_postcards);
                sharelist.setOnItemClickListener(ShowSharedPostCardActivity.this);
            }
        });

    }

}