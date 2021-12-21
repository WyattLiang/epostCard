package com.first.wyatt.postcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ZoomPostCard extends AppCompatActivity {

    private ImageView share_view;
    private Button back_btn;
    private String uri;

    private final static int WIDTH = 380;
    private final static int HEIGHT = 482;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_post_card);

        share_view = findViewById(R.id.zoom);
        back_btn = findViewById(R.id.back_to_share_list);
        uri = getIntent().getStringExtra("uri");
        Picasso.with(ZoomPostCard.this).load(uri).resize(WIDTH,HEIGHT).into(share_view);
        back_btn.setOnClickListener(button -> onBackPressed());
    }
}