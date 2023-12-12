package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

public class slider_activity extends AppCompatActivity {

    CardView next_main;
    LinearLayout dotsLayout;
    ViewPager viewPager;

    TextView[] dots;
    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_feature);

        next_main = findViewById(R.id.next_viewBT);
        dotsLayout = findViewById(R.id.dotsLayout);
        viewPager = findViewById(R.id.slider);

        OnBoardingAdapter adapter = new OnBoardingAdapter(this);
        viewPager.setAdapter(adapter);

        next_main.setOnClickListener(v -> {
            Intent intent = new Intent(slider_activity.this, Home.class);
            startActivity(intent);
            finish();
        });

        dots = new TextView[3];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(HtmlCompat.fromHtml(".", HtmlCompat.FROM_HTML_MODE_LEGACY));
            dots[i].setTextColor(getColor(R.color.white));
            dots[i].setTextSize(30);

            dotsLayout.addView(dots[i]);
        }

        dotFunction(0);

        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private void dotFunction(int pos) {
        for (TextView dot : dots) {
            dot.setTextColor(getColor(R.color.white));
            dot.setTextSize(30);
        }

        if (dots.length > 0) {
            dots[pos].setTextColor(getColor(R.color.teal_700));
            dots[pos].setTextSize(40);
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            dotFunction(position);
            currentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
