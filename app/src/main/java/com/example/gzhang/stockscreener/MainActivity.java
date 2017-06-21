package com.example.gzhang.stockscreener;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenerButton = (Button) findViewById(R.id.screenerButton);
        searchButton = (Button) findViewById(R.id.searchButton);

        stockHashMap = new HashMap<String, Stock>();

        try {
            getData();
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
        /*

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:21cb7913-0313-489d-9d1a-9770f893d803", // Identity Pool ID
                Regions.US_EAST_1 // Region
                //**ORIGINALLY US_EAST_2
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        String MY_BUCKET = "";
        String OBJECT_KEY = "QCOMstock";
        File file = new File("stockInfo.txt");

        TransferObserver observer = transferUtility.upload(
                MY_BUCKET,     /* The bucket to upload to /
                OBJECT_KEY,    /* The key for the uploaded object /
                MY_FILE        /* The file where the data to upload exists /
        );

        */


        while (!dataBaseString.isEmpty()) {
            int lowIndex = dataBaseString.indexOf("[");
            int highIndex = dataBaseString.indexOf("]");

            //gets all data between first quotation mark and before "]" sign
            String stockInfo = dataBaseString.substring(lowIndex + 1, highIndex);

            Stock newStock = new Stock();

            extractStockInfo(stockInfo, newStock);

            stockHashMap.put(newStock.getTickerSymbol(), newStock);

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

            organizeData(dataBaseString);
        }
    }
}
