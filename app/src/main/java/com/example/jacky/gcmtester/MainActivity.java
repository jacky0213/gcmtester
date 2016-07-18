package com.example.jacky.gcmtester;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.w3c.dom.Text;

import java.io.IOException;

public class  MainActivity extends Activity {
    public static final String BROADCAST_RECEIVER = "BroadcastReceiver";
    public int counter = 0;
    public int maxNumOfMsg = 50;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mMessageReceiver;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Registration success
                    token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                    TextView tokenTv = (TextView) findViewById(R.id.tokenTv);
                    tokenTv.setText(token);
                    Log.w(BROADCAST_RECEIVER, "REGISTRATION_SUCCESS");

                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
                    Log.w(BROADCAST_RECEIVER, "REGISTRATION_ERROR");

                }else {
                }
            }
        };

        final LinearLayout MessageLayout = (LinearLayout) findViewById(R.id.MessageLayout);

        TextView tv = new TextView(this);
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                String[] msg = new String[maxNumOfMsg];

                //Create TextView to display message on screen
                if (counter < maxNumOfMsg) {
                    msg[counter] = message;
                    MessageLayout.addView(createTextView(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, "#" + counter + " " + msg[counter]));
                    counter++;
                }
            }
        };



        //Click "Copy" Button to copy the token shown
        Button copyBtn = (Button) findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Token", token);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copy successful", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        EditText sendMsgEt = (EditText) findViewById(R.id.sendMsgEt);
        Button sendMsgBtn = (Button) findViewById(R.id.sendMsgBtn);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */

        //Check status of Google play service in device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode) {
            //Check type of error
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Start service
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(GCMPushReceiverService.MESSAGE_RECEIVED));

}

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    //Create TextView
    protected TextView createTextView(int layout_widh, int layout_height, String text) {

        TextView textView_item_name = new TextView(this);

        // LayoutParams layoutParams = new LayoutParams(
        // LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        // layoutParams.gravity = Gravity.LEFT;
        LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(
                layout_widh, layout_height);

        //_params.setMargins(margin, margin, margin, margin);
        //_params.addRule(align);
        //textView_item_name.setLayoutParams(_params);

        textView_item_name.setText(text);
        //textView_item_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        //textView_item_name.setTextColor(Color.parseColor("#000000"));
        // textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
        //textView_item_name.setPadding(padding, padding, padding, padding);

        return textView_item_name;

    }
}

