package com.example.gzhang.stockscreener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by GZhang on 2017-06-05.
 */
public class SearchActivity extends Activity {

    Button backButton,
           searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        backButton = (Button) findViewById(R.id.backButton);

    }

    public void onBackPress(View view) {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity( intent );
    }

    public void onSearchPress(View view) {


    }
}
