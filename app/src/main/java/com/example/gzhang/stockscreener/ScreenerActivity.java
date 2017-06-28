package com.example.gzhang.stockscreener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by GZhang on 2017-06-05.
 */
public class ScreenerActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screener_layout);

    }

    public void onBackPress(View view) {
        finish();
    }

    //TODO: everything.
    /*List of ideas
    * top x% gainers/losers of the day
    * biggest high/low change, % fluctuation
    * up/down on 1/2/3..n days
    * open/close up/down
    *   open/close up/down on 1/2/3/...n days etc.
     */
}
