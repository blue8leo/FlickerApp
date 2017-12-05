package com.flickrgallery;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.flickrgallery.AdapterCategory;
import com.flickrgallery.Category;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ToggleButton btnGPS;
    private TextView txtLongLat;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    txtLongLat.setText("Long: " + intent.getExtras().get("Long") + ", Lat: " + intent.getExtras().get("Lat"));
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGPS = (ToggleButton) findViewById(R.id.btnGPS);
        txtLongLat = (TextView) findViewById(R.id.txtLongLat);

        if(runtime_permissions()){
            enable_btnGPS();

            Intent i = new Intent(getApplicationContext(), GPS_Service.class);
            startService(i);
        }



        ArrayList<Category> category = new ArrayList<Category>();

        ListView lista = (ListView) findViewById(R.id.listView);

        AdapterCategory adapter = new AdapterCategory(this, category);

        lista.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void enable_btnGPS() {

        btnGPS.setEnabled(true);

        btnGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Declare the service


                if (isChecked) {
                    // The GPS is disabled
                    Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                    stopService(i);
                    Toast.makeText( getApplicationContext(), "GPS service has been stopped", Toast.LENGTH_SHORT).show();
                } else {

                    // The GPS is enable
                    Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                    startService(i);
                    Toast.makeText( getApplicationContext(), "GPS service has been activated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean runtime_permissions() {

        //If the SDK version is 23 or bigger, check the permissions
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //Don't have the permisssions, so request the permissions
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);

            return false;

        }else{
            //Don't need the permissions
            return true;
        }
    }

    //After the request the permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            //If the permissions were granted, enable the button
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_btnGPS();
            }else{
                //Else, request the permissions again
                runtime_permissions();
            }
        }
    }
}
