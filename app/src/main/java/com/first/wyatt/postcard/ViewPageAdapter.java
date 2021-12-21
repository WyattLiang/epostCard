package com.first.wyatt.postcard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<postCard> postCards;

    public ViewPageAdapter(Context context, ArrayList<postCard>postCards){
        this.context = context;
        this.postCards = postCards;
    }
    @Override
    public int getCount() {
        return postCards.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Picasso.with(context)
                .load(postCards.get(position).getPostcard())
                .into(imageView);
        imageView.setTag(String.valueOf(position));
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
