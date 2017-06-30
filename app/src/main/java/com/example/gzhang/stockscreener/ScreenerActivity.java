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

    public void onTopGainsPresrs(View view) {

    }

    public void onOpenChangePress(View view) {

        //TODO: temporary settings
        String positiveOrNegative = "POSITIVE";
        int percentChange = 10;

        //loop through all items in database
        while( true/* not finished looping through database */ )
        {
            Stock theStock = null; //stock from database

            double openPrice = theStock.getOpenPrice();
            double closePrice = theStock.getClosePrice();

            double percentReturn = (closePrice - openPrice) / openPrice * 100.0;

            if( positiveOrNegative.equals( "POSITIVE" ) && percentReturn >= percentChange || positiveOrNegative.equals( "NEGATIVE" ) && percentReturn <= percentChange )
            {
                //put stock in listview
                    //-> display all info
                //show change in % in the same row too
            }

            //move on to next stock
        }
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
