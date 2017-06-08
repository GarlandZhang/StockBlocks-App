package com.example.gzhang.stockscreener;

/**
 * Created by GZhang on 2017-06-07.
 */

public class Stock {

    String tickerSymbol;
    double openPrice;
    double highPrice;
    double lowPrice;
    double closePrice;
    long volume;

    //String information --> format: ["A","2017-06-06",60.14,60.545,60.1,60.1,1966245.0,0.0,1.0,60.14,60.545,60.1,60.1,1966245.0],["AA","2017-06-06",33.11,33.49,32.78,32.94,2295347.0,0.0,1.0,33.11,33.49,32.78,32.94,2295347.0]
    //
    public Stock( String information )
    {
        extractInfo( information );
    }

    public String getTickerSymbol() {
        // TODO Auto-generated method stub
        return tickerSymbol;
    }

}
