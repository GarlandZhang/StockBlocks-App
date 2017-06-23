package com.example.gzhang.stockscreener;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.Serializable;

/**
 * Created by GZhang on 2017-06-07.
 */
@DynamoDBTable(tableName = "Stocks")
public class Stock implements Serializable{

    String tickerSymbol;
    double openPrice;
    double highPrice;
    double lowPrice;
    double closePrice;
    long volume;

    //String information --> format: ["A","2017-06-06",60.14,60.545,60.1,60.1,1966245.0,0.0,1.0,60.14,60.545,60.1,60.1,1966245.0],["AA","2017-06-06",33.11,33.49,32.78,32.94,2295347.0,0.0,1.0,33.11,33.49,32.78,32.94,2295347.0]
    //
    public Stock()
    {

    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }
    
    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    @DynamoDBHashKey(attributeName = "Ticker Symbol")
    public String getTickerSymbol() {
        // TODO Auto-generated method stub
        return tickerSymbol;
    }

    @DynamoDBAttribute(attributeName = "Open")
    public double getOpenPrice() {
        return openPrice;
    }

    @DynamoDBAttribute(attributeName = "High")
    public double getHighPrice() {
        return highPrice;
    }

    @DynamoDBAttribute(attributeName = "Low")
    public double getLowPrice() {
        return lowPrice;
    }

    @DynamoDBAttribute(attributeName = "Close")
    public double getClosePrice() {
        return closePrice;
    }

    @DynamoDBAttribute(attributeName = "Volume")
    public long getVolume() {
        return volume;
    }
}
