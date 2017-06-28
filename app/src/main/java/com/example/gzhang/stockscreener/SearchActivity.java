package com.example.gzhang.stockscreener;

import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by GZhang on 2017-06-05.
 */
public class SearchActivity extends Activity {

    //UI
    Button backButton,
           searchButton;

    TextView openPriceTV, highPriceTV, lowPriceTV, closePriceTV, volumeTV, quoteTitleTV;

    EditText stockTickerET;

    ImageView stockChartIV, logoIV;

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
        quoteTitleTV = (TextView) findViewById(R.id.quoteTitleTV);

        stockTickerET = (EditText) findViewById(R.id.stockTickerET);

        stockChartIV = (ImageView) findViewById(R.id.stockChartIV);
        logoIV = (ImageView) findViewById(R.id.logoIV);

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

        new DataDisplayer().execute( tickerSymbol );
    }

    private class DataDisplayer extends AsyncTask<String, Void, Stock> {

        @Override
        protected Stock doInBackground(String... params) {
            return mapper.load( Stock.class, params[ 0 ] );
        }

        @Override
        protected void onPostExecute(Stock stock) {
            Stock theStock = stock;

            if( theStock != null && !quoteTitleTV.getText().toString().equals( theStock.getName() ) ) {
                openPriceTV.setText("Open: " + Double.toString(theStock.getOpenPrice()));
                highPriceTV.setText("High: " + Double.toString(theStock.getHighPrice()));
                lowPriceTV.setText("Low: " + Double.toString(theStock.getLowPrice()));
                closePriceTV.setText("Close: " + Double.toString(theStock.getClosePrice()));
                volumeTV.setText("Volume: " + Long.toString(theStock.getVolume()));
                quoteTitleTV.setText( theStock.getName() );

                //load stock chart
                new ChartAndLogoRetriever().execute( theStock.getTickerSymbol() );

                Toast.makeText( getApplicationContext(), "Here is the quote for: " + theStock.getTickerSymbol() + ".", Toast.LENGTH_SHORT).show();
            }
            else if( theStock == null )
            {
                Toast.makeText( getApplicationContext(), "PSYCHE, THAT'S THE WRONG SYMBOL!", Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText( getApplicationContext(), "Current data shown is the quote for: " + theStock.getTickerSymbol() + ".", Toast.LENGTH_LONG).show();
            }
        }
    }


    //TODO: fix this inefficient use of AsyncTask. *Note: WHY IS IT CALLED DataDisplayer STILL.... ITS NOT EVEN JSON RELATED.
    private class ChartAndLogoRetriever extends AsyncTask< String, Void, Bitmap[] >
    {

        @Override
        protected Bitmap[] doInBackground(String... params) {
            try {

                String webSiteURL = "http://www.nasdaq.com/symbol/" + params[0].toLowerCase() + "/stock-chart?intraday=on&timeframe=intra&splits=off&earnings=off&movingaverage=None&lowerstudy=volume&comparison=off&index=&drilldown=off";

                //Connect to the website and get the html
                Document doc = Jsoup.connect(webSiteURL).get();

                //Get all elements with img tag ,
                Elements img = doc.getElementsByTag("img");

                Element el = img.get(3);

                Bitmap[] bitmaps = new Bitmap[ 2 ];
                
                if (!el.absUrl("src").contains("http://charting.nasdaq.com")) {

                    bitmaps[1] = getBitmap(el);
                    el = img.get(4);
                }
                else
                {
                    bitmaps[ 1 ] = null;
                }

                bitmaps[0] = getBitmap(el);

                return bitmaps;
            
            }catch( Exception e )
            {
                e.printStackTrace();
            }

            return null;
        }

        private Bitmap getBitmap(Element el) {
            //for each element get the srs url
            String src = el.absUrl("src");

            Bitmap bitmap = null;

            try {
                URL urlConnection = new URL( src );
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput( true );
                InputStream in = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmaps ) {
            stockChartIV.setImageBitmap( bitmaps[ 0 ] );
            
            if( bitmaps[ 1 ] != null )
            {
                logoIV.setImageBitmap( bitmaps[ 1 ] );
            }
            else
            {
                logoIV.setImageBitmap( null );
            }
        }
    }
/*
    private class NameRetriever extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {

            try
            {
                Document doc = Jsoup.connect("https://finance.yahoo.com/quote/" + params[ 0 ] ).get();
                String title = doc.title();
                title = title.substring( title.indexOf( "for" ) + 4, title.lastIndexOf( '-' ) );

                return title;

            }catch( Exception e )
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String title ) {

            quoteTitleTV.setText( title );

        }
    }
    */
}
