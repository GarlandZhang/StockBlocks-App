package com.example.gzhang.stockscreener;

//don't worry about these imports

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

/*TODO: Future improvements
* 24 hour alert to update data.
* user log-in
* Real-time stock scanner for news/information
*   ie: a news article mentioning "earnings" for "APPL".
* */

public class MainActivity extends AppCompatActivity {

    //UI
    Button searchButton,
            screenerButton,
            loginButton;

    //DynamoDBMapper mapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize UI
        screenerButton = (Button) findViewById(R.id.screenerButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        loginButton = (Button) findViewById(R.id.searchButton);

        /*
        //AWS setup
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:8c9a6d87-0e19-4e8d-9de0-76a73548db92", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );

        
            AmazonDynamoDBClient ddbClient = Region.getRegion(Regions.US_EAST_2)
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

        //this is used to store and retrieve data...think of it like a hashtable; database (DynamoDB) is designed like a hashtable
        mapper = new DynamoDBMapper(ddbClient);

        //retrieve data
        try {
            //TODO: TEMPORARY
            getAndStoreData();
        }catch ( Exception e )
        {
            e.printStackTrace();
        }
        */

    }

/*
    public void getAndStoreData() throws IOException {

        String TODAYS_DATE = getLastTradingDate();
        TODAYS_DATE = "20170606"; //TODO: change to find previous trading day. This requires determining if weekday and if American holiday.

        //url where we get daily stock info from
        String URL = "https://www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date=" + TODAYS_DATE + "&api_key=7hsNV69CDn_8SrPG2tqQ";

        //where data will be retrieved and later uploaded to database
        new DataRetriever().execute( URL );
    }

    private void organizeData(String dataBaseString) {

        //extract data
        while (!dataBaseString.isEmpty()) {

            int lowIndex = dataBaseString.indexOf("[");
            int highIndex = dataBaseString.indexOf("]") + 1;

            //gets all data between two brackets
            String stockInfo = dataBaseString.substring(lowIndex, highIndex);

            Stock newStock = new Stock();

            extractStockInfo(stockInfo, newStock); //extracts and stores all important info into fields

            //Stock object is stored in database
            mapper.save( newStock );

            //gets data after this set and comma
            dataBaseString = dataBaseString.substring(highIndex + 1);
        }
    }

    //TODO: nasty way of retrieving data. Try ENUM?
    private void extractStockInfo(String stockInfo, Stock newStock) {

        //remove redundant quotes
        stockInfo = stockInfo.replace( "\"", "" );

        //get ticker symbol
        int index = stockInfo.indexOf( ',' );
        String tickerSymbol = stockInfo.substring(1, index); //after '[' and before first ','
        newStock.setTickerSymbol(tickerSymbol);
        stockInfo = stockInfo.substring( index + 1 ); //update to be string after ','
        stockInfo = stockInfo.substring( stockInfo.indexOf( ',' ) + 1 ); //update to be string after ','

        //get open price
        index = stockInfo.indexOf(',');
        double openPrice = Double.parseDouble(stockInfo.substring(0, index));
        newStock.setOpenPrice(openPrice);
        stockInfo = stockInfo.substring(index + 1);

        //get high price
        index = stockInfo.indexOf(',');
        double highPrice = Double.parseDouble(stockInfo.substring(0, index));
        newStock.setHighPrice(highPrice);
        stockInfo = stockInfo.substring(index + 1);

        //get low price
        index = stockInfo.indexOf(',');
        double lowPrice = Double.parseDouble(stockInfo.substring(0, index));
        newStock.setLowPrice(lowPrice);
        stockInfo = stockInfo.substring(index + 1);

        //get close price
        index = stockInfo.indexOf(',');
        double closePrice = Double.parseDouble(stockInfo.substring(0, index));
        newStock.setClosePrice(closePrice);
        stockInfo = stockInfo.substring(index + 1);

        //get volume
        index = stockInfo.indexOf(',');
        long volume = (long) Double.parseDouble(stockInfo.substring(0, index));
        newStock.setVolume(volume);
    }

    //TODO: Fix
    public String getLastTradingDate() {

        //to format for todays date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String TODAYS_DATE = df.format(c.getTime()); //-YY//MM//DD


        return TODAYS_DATE;
    }

    //how data is retrieved from url
    private class DataRetriever extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection conn = null;

            String output = "";

            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream is = conn.getInputStream();

                Scanner sc = new Scanner(is);

                output = sc.next();

                sc.close();
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return output;
        }

        @Override
        protected void onPostExecute(String dataBaseString ) {

            //remove filler from formatted JSON data
            dataBaseString = dataBaseString.substring(22, dataBaseString.length() - 642);

            //upload to database
            new DataUploader().execute( dataBaseString );
        }
    }

    //how data is uploaded to database
    private class DataUploader extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params) {

            String dataBaseString = params[ 0 ];

            //organize data to properly extract and isolate stock info. Then upload to database
            organizeData( dataBaseString );

            return null;
        }
    }
    */

    //TODO: How to pass mapper object in order to access database in another activity
    public void onSearchClick(View view) {

        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    //TODO: Another activity (separate from stock search up) which will later be developed
    public void onScreenerClick(View view) {

        Intent intent = new Intent(this, ScreenerActivity.class);
        startActivity( intent );
    }

    public void onLoginClick (View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity( intent );

    }

    public void onAboutClick(View view) {

        Intent intent = new Intent( this, AboutActivity.class );
        startActivity( intent );
    }

}
