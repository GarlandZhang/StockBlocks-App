package com.example.gzhang.stockscreener;

import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

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

    ListView newsLV;

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

        newsLV = (ListView) findViewById(R.id.newsLV);

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

        String stockTickerETText = stockTickerET.getText().toString().toUpperCase();

        Scanner sc = new Scanner( stockTickerETText );

        String tickerSymbol = sc.next();

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
                new ChartAndLogoRetriever( theStock ).execute();
                new NewsRetriever( theStock ).execute();

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

    private class ChartAndLogoRetriever extends AsyncTask< Void, Void, Bitmap[] >
    {
        Stock theStock;

        private ChartAndLogoRetriever( Stock theStock )
        {
            this.theStock = theStock;
        }

        @Override
        protected Bitmap[] doInBackground( Void...params ) {
            try {

                String webSiteURL = "http://www.nasdaq.com/symbol/" + theStock.getTickerSymbol().toLowerCase() + "/stock-chart?intraday=on&timeframe=intra&splits=off&earnings=off&movingaverage=None&lowerstudy=volume&comparison=off&index=&drilldown=off";

                //Connect to the website and get the html
                Document doc = Jsoup.connect(webSiteURL).get();

                //Get all elements with img tag ,
                Elements img = doc.getElementsByTag("img");

                Element el = img.get(3);

                Bitmap[] bitmaps = new Bitmap[ 2 ];

                //checks if it does not contain this url. This means this is a logo
                if (!el.absUrl("src").contains("http://charting.nasdaq.com")) {

                    bitmaps[1] = getBitmap(el);
                    el = img.get(4);
                }

                //this is for the chart
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

    private class NewsRetriever extends AsyncTask< Void, Void, Void >
    {

        Stock theStock;

        ArrayList<String> newsLinks,
                 titles;

        private NewsRetriever( Stock theStock )
        {
            this.theStock = theStock;
            newsLinks = new ArrayList<String>();
            titles = new ArrayList<String>();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String link = "https://feeds.finance.yahoo.com/rss/2.0/headline?s=" + theStock.getTickerSymbol() + "&region=US&lang=en-US";
                URL resURL = new URL(link);
                BufferedReader in = new BufferedReader(new InputStreamReader(resURL.openStream()));
                String line;

                while ((line = in.readLine()) != null) {

                    String title = "",
                            linkURL = "";

                    if (line.contains("<item>")) {

                        boolean isTitle = false,
                                isLink = false;

                        while( !(line = in.readLine() ).contains( "</item>" ) && line != null )
                        {
                            //get string between <title> and </title>
                            if( line.contains( "<title>" ) )
                            {
                                isTitle = true;
                            }

                            if( line.contains( "</title>" ) )
                            {
                                isTitle = false;
                                title = title + line;
                                int firstPos = line.indexOf("<title>");
                                int lastPos = line.indexOf("</title>");
                                title = line.substring(firstPos, lastPos);
                                title = title.replace("<title>", "");
                                titles.add( title );
                            }

                            if( isTitle )
                            {
                                title = title + line;
                            }


                            //get string between <link> and </link>
                            if( line.contains( "<link>" ) )
                            {
                                isLink = true;
                            }

                            if( line.contains( "</link>" ) )
                            {
                                isLink = false;
                                linkURL = linkURL + line;
                                int firstPos = line.indexOf("<link>");
                                int lastPos = line.indexOf("</link>");
                                linkURL = line.substring(firstPos, lastPos);
                                linkURL = linkURL.replace("<link>", "");
                                newsLinks.add( linkURL );
                            }

                            if( isLink )
                            {
                                linkURL = linkURL + line;
                            }

                        }
                    }

                }

                in.close();

            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            ListAdapter listAdapter = new ArrayAdapter<String>( getApplicationContext(),android.R.layout.simple_list_item_1, titles );

            newsLV.setAdapter( listAdapter );

            newsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText( getApplicationContext(), "Go to this website: " + newsLinks.get( position ), Toast.LENGTH_LONG ).show();
                }
            });
        }
    }
}

