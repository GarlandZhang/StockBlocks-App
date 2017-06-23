package com.example.gzhang.stockscreener;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    Button searchButton,
            screenerButton;

    HashMap<String, Stock> stockHashMap;

    boolean isDataThrown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenerButton = (Button) findViewById(R.id.screenerButton);
        searchButton = (Button) findViewById(R.id.searchButton);

        stockHashMap = new HashMap<String, Stock>();

        Intent intent = getIntent();

            try {
                //TEMPORARY
                //isDataThrown = (boolean) intent.getExtras().get("isDataThrown");
                if( !isDataThrown ) {
                    getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void getData() throws IOException {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String TODAYS_DATE = df.format(c.getTime()); //-YY//MM//DD
        TODAYS_DATE = "20170606"; //TEMPORARY

        String url = "https://www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date=" + TODAYS_DATE + "&api_key=7hsNV69CDn_8SrPG2tqQ";

        new JSONTask().execute( url );
    }

    public void organizeData(String dataBaseString) {

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:9412c9ff-b08e-4189-8001-def0323ea1b8", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );

        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);


        while (!dataBaseString.isEmpty()) {
            int lowIndex = dataBaseString.indexOf("[");
            int highIndex = dataBaseString.indexOf("]");

            //gets all data between first quotation mark and before "]" sign
            String stockInfo = dataBaseString.substring(lowIndex + 1, highIndex);

            Stock newStock = new Stock();

            extractStockInfo(stockInfo, newStock);

            /*
            stockHashMap.put(newStock.getTickerSymbol(), newStock);
            */

            mapper.save( newStock );

            //TEMPORARY
            newStock = mapper.load( Stock.class, "A" );
            System.out.println( "I DID IT " + newStock.getOpenPrice() );

            //OUT OF BOUNDS???
            dataBaseString = dataBaseString.substring(highIndex + 1);
        }

    }


    //enum? IMPROVE CODE
    private void extractStockInfo(String stockInfo, Stock newStock) {

        //get ticker symbol

        int index = stockInfo.substring(1).indexOf('"') + 1;
        String tickerSymbol = stockInfo.substring(1, index);
        newStock.setTickerSymbol(tickerSymbol);
        stockInfo = stockInfo.substring( stockInfo.lastIndexOf( '"' ) + 2 );

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

    public void onSearchClick(View view) {

        Intent intent = new Intent(this, SearchActivity.class);

        //TEMPORARY
        Stock A = stockHashMap.get( "A" );
        intent.putExtra( "A", A );
        intent.putExtra( "isDataThrown", isDataThrown );

        //intent.putExtra( "stockHashMap", stockHashMap );


        startActivity(intent);
    }

    public void onScreenerClick(View view) {

        Intent intent = new Intent(this, ScreenerActivity.class);
    }

    private class JSONTask extends AsyncTask<String, String, String> {

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
            //remove filler
            dataBaseString = dataBaseString.substring(22, dataBaseString.length() - 642);

            //organizeData(dataBaseString);

            new JSONTask2().execute( dataBaseString );
        }
    }

    private class JSONTask2 extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String dataBaseString = params[ 0 ];

            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-2:8c9a6d87-0e19-4e8d-9de0-76a73548db92", // Identity Pool ID
                    Regions.US_EAST_2 // Region
            );

            AmazonDynamoDBClient ddbClient = Region.getRegion(Regions.US_EAST_2) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);


            while (!dataBaseString.isEmpty()) {
                int lowIndex = dataBaseString.indexOf("[");
                int highIndex = dataBaseString.indexOf("]");

                //gets all data between first quotation mark and before "]" sign
                String stockInfo = dataBaseString.substring(lowIndex + 1, highIndex);

                Stock newStock = new Stock();

                extractStockInfo(stockInfo, newStock);

            /*
            stockHashMap.put(newStock.getTickerSymbol(), newStock);
            */

                mapper.save( newStock );

                //TEMPORARY
                newStock = mapper.load( Stock.class, "A" );
                System.out.println( "I DID IT " + newStock.getOpenPrice() );

                //OUT OF BOUNDS???
                dataBaseString = dataBaseString.substring(highIndex + 1);
            }

            return null;
        }
    }

}
