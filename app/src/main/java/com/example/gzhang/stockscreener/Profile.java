package com.example.gzhang.stockscreener;

/**
 * Created by leon_ on 2017-07-23.
 */

public class Profile {

    String username;
    String password; //Not secure at all. Should use auth token



    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password){
        this.password = password;
}

   // public void addStocks (String tickerSymbol;)

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }
}