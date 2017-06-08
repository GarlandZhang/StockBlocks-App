package com.example.gzhang.stockscreener;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    Button searchButton,
           screenerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenerButton = (Button) findViewById(R.id.screenerButton);
        searchButton = (Button) findViewById(R.id.searchButton);

        String dataBaseString = getData();

        //remove filler
        dataBaseString = dataBaseString.substring( 22, dataBaseString.length() - 642 );

        organizeData( dataBaseString );

        System.out.println( dataBaseString );
    }

    public static String getData() throws IOException
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd" );
        String TODAYS_DATE = df.format( c.getTime() ); //-YY//MM//DD
        TODAYS_DATE = "20170606"; //TEMPORARY

        String url = "https://www.quandl.com/api/v3/datatables/WIKI/PRICES.json?date=" + TODAYS_DATE + "&api_key=7hsNV69CDn_8SrPG2tqQ";
        URL urlConnection = new URL( url );

        InputStream is = urlConnection.openStream();
        Scanner sc = new Scanner( is );

        String dataBaseString = sc.next();

        sc.close();

        //remove filler
        dataBaseString = dataBaseString.substring( 22, dataBaseString.length() - 642 );

        return dataBaseString;
    }

    public static void organizeData( String dataBaseString )
    {
        Hashtable<String,Stock> dataBase = new Hashtable<String,Stock>();

        while( !dataBaseString.isEmpty() )
        {
            int lowIndex = dataBaseString.indexOf( "[" );
            int highIndex = dataBaseString.indexOf( "]" );

            String stockInfo = dataBaseString.substring( lowIndex + 1, highIndex );
            Stock newStock = new Stock( stockInfo );

            dataBase.put( newStock.getTickerSymbol() , newStock );

            dataBaseString = dataBaseString.substring( highIndex + 1 );
        }

    }

    public void onSearchClick(View view) {

        Intent intent = new Intent( this, SearchActivity.class );
        startActivity( intent );
    }

    public void onScreenerClick(View view) {

        Intent intent = new Intent( this, ScreenerActivity.class );
    }
}
