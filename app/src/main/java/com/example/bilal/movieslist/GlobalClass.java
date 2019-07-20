package com.example.bilal.movieslist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class GlobalClass {
    public static String baseUrl="https://api.themoviedb.org";
    public static String imageUrl="https://image.tmdb.org/t/p/w200/";
    public static AlertDialog alertDialog;


    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            // Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        } else {
            // showDialog(context);
            return false;
        }
        return false;
    }
    public static void showDialog(final Context mContext) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        alertDialogBuilder.setMessage("Internet is not available.Do you want to turn on Wifi Connection?");
        alertDialogBuilder.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //onBackPressed();

                        Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        mContext.startActivity(myIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();

                if (alertDialog != null) {
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            }
        });
        alertDialog = alertDialogBuilder.create();
        if ( alertDialog != null) {
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }

    }
}
