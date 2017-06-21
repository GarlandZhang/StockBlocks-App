package com.example.gzhang.stockscreener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by GZhang on 2017-06-05.
 */
public class SearchActivity extends Activity {

    Button backButton,
           searchButton;

    TextView openPriceTV, highPriceTV, lowPriceTV, closePriceTV, volumeTV;

    EditText stockTickerET;

    HashMap<String,Stock> stockHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        backButton = (Button) findViewById(R.id.backButton);

        searchButton = (Button) findViewById(R.id.searchButton);

        openPriceTV = (TextView) findViewById(R.id.openPriceTV);
        highPriceTV = (TextView) findViewById(R.id.highPriceTV);
        lowPriceTV = (TextView) findViewById(R.id.lowPriceTV);
        closePriceTV = (TextView) findViewById(R.id.closePriceTV);
        volumeTV = (TextView) findViewById(R.id.volumeTV);

        stockTickerET = (EditText) findViewById(R.id.stockTickerET);

        Intent intent = getIntent();

        try
        {
            /* TEMPORRAY
           stockHashMap = (HashMap<String, Stock>)intent.getSerializableExtra("stockHashMap");
            //stockHashMap = ((ArrayList<HashMap<String, Stock>>) bundle.get( "stockHashMapList" )).get( 0 );
            Stock A = stockHashMap.get( "A" );
            //Stock A = (Stock) bundle.get( "A" );
            System.out.println( "HI... " + A.getOpenPrice());
            */

            //TEMPORARY
            stockHashMap = new HashMap<String,Stock>();
            Stock A = (Stock) intent.getExtras().get( "A" );
            stockHashMap.put( A.getTickerSymbol(), A );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void onBackPress(View view) {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity( intent );
    }

    public void onSearchPress(View view) {

        String tickerSymbol = stockTickerET.getText().toString().toUpperCase();

        if( stockHashMap != null ) {
            if (stockHashMap.containsKey(tickerSymbol)) {

                //TEMPORRRY
                System.out.println( tickerSymbol );

                Stock s = stockHashMap.get( tickerSymbol );

                double open = s.getOpenPrice();
                double high = s.getHighPrice();
                double low = s.getLowPrice();

                System.out.println( s.getTickerSymbol());
                System.out.println( open );
                System.out.println( high );
                System.out.println( low );
                //


                Stock theStock = stockHashMap.get(tickerSymbol);

                openPriceTV.setText( "Open: " + Double.toString(theStock.getOpenPrice()));
                highPriceTV.setText( "High: " + Double.toString(theStock.getHighPrice()));
                lowPriceTV.setText( "Low: " + Double.toString(theStock.getLowPrice()));
                closePriceTV.setText( "Close: " + Double.toString(theStock.getClosePrice()));
                volumeTV.setText( "Volume: " + Long.toString(theStock.getVolume()));

                Toast.makeText(this, "Here is your quote!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sorry, either the symbol doesn't exist or we don't have it :(", Toast.LENGTH_LONG).show();
            }
        }
    }
}
