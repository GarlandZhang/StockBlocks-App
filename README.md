# StockBlocks-App

How to use: Upon opening the app, you will be at the main menu. Navigate to the 'Search stock' page. Enter the ticker symbol you wish to gather information about. After pressing the search button, you will be given common quote info about the stock as well as any relevant newsfeeds, charts, and logo.

Tools/Frameworks/Databases/APIs used: 
Using the RESTful API, we gathered stock data from the Quandl website.
We reformatted the data and stored it into our DynamoDB database (AWS). 
Using jsoup, we collected additional data not provided by Quandl, such as stock logo, charts, and newsfeeds, by webscraping data from Yahoo Finance. 

TODO: we hope to implement our own stock screener, a handy tool that filters for stocks based on certain parameters. For example, we want to filter for all stocks that are <$3, gained 10% in the past week, and have a market share of over $3 billion.

Reflection: I had a lot of fun building this app with my friend. I hope to do more app projects in the future.
