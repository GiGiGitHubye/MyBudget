package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class OnBoardingAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public OnBoardingAdapter(Context context) {
        this.context = context;
    }

    int[] titles = {
            R.string.title1,
            R.string.title2,
            R.string.title3
    };

    int[] subtitles ={
            R.string.subtitle1,
            R.string.subtitle2,
            R.string.subtitle3
    };

    int[] images ={
            R.drawable.border1,
            R.drawable.border2,
            R.drawable.border3
    };

    int[] bg = {
            R.drawable.border_1,
            R.drawable.border_2,
            R.drawable.border_3
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.slide,container,false);

        ImageView slideImg = view.findViewById(R.id.slideImg);
        TextView slideTitle = view.findViewById(R.id.slideTitle);
        TextView slideSubTitle = view.findViewById(R.id.slideSubTitle);
        ConstraintLayout layout = view.findViewById(R.id.sliderLayout);

        slideImg.setImageResource(images[position]);
        slideTitle.setText(titles[position]);
        slideSubTitle.setText(subtitles[position]);
        layout.setBackgroundResource(bg[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
