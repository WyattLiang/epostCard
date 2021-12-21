package com.first.wyatt.postcard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import static com.first.wyatt.postcard.PostcardList.postCards;

public class SelectPostCardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView postcard_list;
    private Button backToFindFriends;
    private ArrayAdapter<postCard>adapter;
    private String Id_friend;

    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_post_card);

        postcard_list = findViewById(R.id.postcards_list);
        backToFindFriends = findViewById(R.id.back_to_find_friend);

        Id_friend = getIntent().getStringExtra("userID");
        mRef = FirebaseDatabase.getInstance().getReference().child("share").child(Id_friend);
        Log.d("FROM FINDFRIEND",Id_friend);

        setUpadapter();

        backToFindFriends.setOnClickListener(button -> BackToFindFriends());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String uri = postCards.get(i).getPostcard();
        showDialog(uri);
    }

    public static class ViewHolder{
        ImageView singel_postcard;
        TextView hint;
    }

    private void setUpadapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new ArrayAdapter<postCard>(SelectPostCardActivity.this,R.layout.single_postcard,postCards){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        ViewHolder viewHolder;
                        if(convertView == null){
                            viewHolder = new ViewHolder();
                            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                            convertView = layoutInflater.inflate(R.layout.single_postcard,parent,false);
                            viewHolder.singel_postcard = convertView.findViewById(R.id.postcard_select);
                            viewHolder.hint = convertView.findViewById(R.id.hint);

                            convertView.setTag(viewHolder);
                        }
                        else{
                            viewHolder = (ViewHolder)convertView.getTag();
                        }
                        Picasso.with(SelectPostCardActivity.this).load(postCards.get(position).getPostcard()).resize(156,200)
                                .into(viewHolder.singel_postcard);
                        return convertView;
                    }
                };
                postcard_list.setAdapter(adapter);
                postcard_list.setOnItemClickListener(SelectPostCardActivity.this);
            }
        });
    }

    private void showDialog(String uri){
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPostCardActivity.this);
        builder.setTitle("Confirm");
        builder.setMessage("Would you like to send this postcard?");
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRef.child(String.valueOf(System.currentTimeMillis())).setValue(uri)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SelectPostCardActivity.this,"Your postcard has been send",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(SelectPostCardActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void BackToFindFriends(){
        onBackPressed();
    }
}
