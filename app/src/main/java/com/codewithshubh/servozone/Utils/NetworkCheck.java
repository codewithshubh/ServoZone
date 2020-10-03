package com.codewithshubh.servozone.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.codewithshubh.servozone.Constant.Constants;
import com.codewithshubh.servozone.R;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class NetworkCheck {
    private Context context;

    public NetworkCheck(Context context) {
        this.context = context;
    }

    public SweetAlertDialog noInternetDialog;

    public void showDialog(){
        noInternetDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        noInternetDialog.setCancelable(false);
        noInternetDialog.setCanceledOnTouchOutside(false);
        noInternetDialog
                .setTitleText("No Internet")
                .setContentText(context.getResources().getString(R.string.no_internet_msg))
                .setConfirmText("Data")
                .setCancelText("Wifi")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent i = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                        context.startActivity(i);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        context.startActivity(i);
                    }
                })
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void registerDefaultNetworkCallback(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            assert connectivityManager != null;
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull android.net.Network network) {
                    super.onAvailable(network);
                    Constants.IS_CONNECTED = true;
                    if (noInternetDialog!=null)
                        noInternetDialog.dismissWithAnimation();
                }

                @Override
                public void onLost(@NonNull android.net.Network network) {
                    super.onLost(network);
                    Constants.IS_CONNECTED = false;
                    if (noInternetDialog!=null)
                        noInternetDialog.dismissWithAnimation();
                    showDialog();

                }

                @Override
                public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
                    super.onBlockedStatusChanged(network, blocked);

                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }

                @Override
                public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                }

                @Override
                public void onLosing(@NonNull Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Constants.IS_CONNECTED = false;
                    if (noInternetDialog!=null)
                        noInternetDialog.dismissWithAnimation();
                    showDialog();
                }
            });
        } catch (Exception e) {
            Constants.IS_CONNECTED = false;
        }
    }

    public void registerNetworkCallback(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            assert connectivityManager != null;
            connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull android.net.Network network) {
                    super.onAvailable(network);
                    Constants.IS_CONNECTED = true;
                    if (noInternetDialog!=null){
                        noInternetDialog.dismissWithAnimation();
                    }
                }

                @Override
                public void onLost(@NonNull android.net.Network network) {
                    super.onLost(network);
                    Constants.IS_CONNECTED = false;
                    if (noInternetDialog!=null){
                        noInternetDialog.dismissWithAnimation();
                    }
                    showDialog();
                }

                @Override
                public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
                    super.onBlockedStatusChanged(network, blocked);

                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }

                @Override
                public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                }

                @Override
                public void onLosing(@NonNull Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Constants.IS_CONNECTED = false;
                    if (noInternetDialog!=null)
                        noInternetDialog.dismissWithAnimation();
                    showDialog();
                }
            });

        } catch (Exception e) {
            Constants.IS_CONNECTED = false;
        }
    }

    public void noInternetDialog(){
        // Register NetworkCallback -- need to call this when activity starts
        NetworkCheck network = new NetworkCheck(context);
        /*
         * registerNetworkCallback() is available from API-21 and above
         * But it will show status of all available network. Hence you would
         * to query onCapabilitiesChanged() to make sure your intended network is active or not.
         */
        // network.registerNetworkCallback();


        /*
         * network.registerDefaultNetworkCallback() is available from API-24
         * Unlike its counterpart, it will only register status of current active network
         * Hence no need to check with onCapabilitiesChanged(). But downside is that it
         * is available since API-24 while current recommendation (in 2019) is to maintain
         * minimum support for API-23 Marshmallo.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            network.registerDefaultNetworkCallback();
        }
        else {
            network.registerNetworkCallback();
        }
    }
}
