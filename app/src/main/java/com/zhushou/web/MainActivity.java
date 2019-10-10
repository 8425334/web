package com.zhushou.web;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bannerlib.BannerView;
import com.youth.banner.Banner;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new BannerView(this, (Banner)findViewById(R.id.banner));
    }
}
