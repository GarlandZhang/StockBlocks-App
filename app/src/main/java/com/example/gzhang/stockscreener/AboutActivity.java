package com.example.gzhang.stockscreener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by GZhang on 2017-06-24.
 */
public class AboutActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

    }

    public void onBackPress(View view) {
        finish();
    }
}
