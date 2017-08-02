package com.example.gzhang.stockscreener;

import android.app.Activity;

import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.EditText;

/*
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
*/


/**
 * Created by leon_ on 2017-06-30.
 */

    public class CreateAccountActivity extends Activity {

        EditText usernameText,
                    passwordText1,
                    passwordText2;

        Button createAccountButton;

        @Override
        protected void onCreate(Bundle savedInstanceState)
            {super.onCreate(savedInstanceState);
            setContentView(R.layout.createaccount_layout);


        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText1 = (EditText) findViewById(R.id.passwordText1);
        passwordText2 = (EditText) findViewById(R.id.passwordText2);

            }
    }