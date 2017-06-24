package com.example.gzhang.stockscreener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Created by GZhang on 2017-06-05.
 */
public class SearchActivity extends Activity {

    //UI
    Button backButton,
           searchButton;

    TextView openPriceTV, highPriceTV, lowPriceTV, closePriceTV, volumeTV;

    EditText stockTickerET;

    DynamoDBMapper mapper;

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

        //TODO: condense code...pass mapper object through activities without writing this over and over again
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:8c9a6d87-0e19-4e8d-9de0-76a73548db92", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );

        //more wonky shit
        AmazonDynamoDBClient ddbClient = Region.getRegion(Regions.US_EAST_2)
                .createClient(
                        AmazonDynamoDBClient.class,
                        credentialsProvider,
                        new ClientConfiguration()
                );

        //this is used to store and retrieve data...think of it like a hashtable; database (DynamoDB) is designed like a hashtable
        mapper = new DynamoDBMapper(ddbClient);
    }

    public void onBackPress(View view) {

        finish();
    }

    public void onSearchPress(View view) {

        String tickerSymbol = stockTickerET.getText().toString().toUpperCase();

        new JSONTask().execute( tickerSymbol );
    }

    private class JSONTask extends AsyncTask<String, Void, Stock> {

        @Override
        protected Stock doInBackground(String... params) {
            return mapper.load( Stock.class, params[ 0 ] );
        }

        @Override
        protected void onPostExecute(Stock stock) {
            Stock theStock = stock;

            if( theStock != null ) {
                openPriceTV.setText("Open: " + Double.toString(theStock.getOpenPrice()));
                highPriceTV.setText("High: " + Double.toString(theStock.getHighPrice()));
                lowPriceTV.setText("Low: " + Double.toString(theStock.getLowPrice()));
                closePriceTV.setText("Close: " + Double.toString(theStock.getClosePrice()));
                volumeTV.setText("Volume: " + Long.toString(theStock.getVolume()));

                Toast.makeText( getApplicationContext(), "Here is your quote!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText( getApplicationContext(), "PSYCHE, THAT'S THE WRONG SYMBOL!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
