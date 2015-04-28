package henry.locate;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by henry on 06/04/15.
 */
public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static final String PREFS_NAME = "Settings";
    private static final String TAG = "Locate";


    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"BIND");

        return null;
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
       super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }





    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Log.i(TAG,"STARTCMD");
        return Service.START_STICKY;
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "connected");
        String lat, longitude;
        Location mLastLocation;
       // mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                //mGoogleApiClient);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(300000);
        mLocationRequest.setFastestInterval(300000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
     // lat = String.valueOf(mLastLocation.getLatitude());

       // longitude = String.valueOf(mLastLocation.getLongitude());
      //  Log.v(TAG,lat);
    }

    private class SendLocation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                SharedPreferences settings = getSharedPreferences("Settings", 4);
                String authkey = settings.getString("authkey","");
                String url = settings.getString("url","");
                String email = settings.getString("email","");

                url = url + "/location";
                URL url1 = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                Log.i(TAG,"1");

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                Log.i(TAG, "2");
                conn.setDoOutput(true);
                Log.i(TAG, "3");

                OutputStream os = conn.getOutputStream();
                Log.i(TAG,"4");


                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.i(TAG,"5");

                Log.i(TAG,params[0]);
                Log.i(TAG,"6");

                writer.write(params[0]);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                InputStream stream = null;
                stream = conn.getInputStream();
               // String content = readIt(stream, 500);
                String content = "";
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                Log.i(TAG, String.valueOf(total));
                return String.valueOf(total);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid." + e.toString();
            }
        }
    }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        public void onLocationChanged(Location location) {
            Log.v(TAG, location.toString());
            String mLastUpdateTime, lat, longitude;
            lat = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            final String tmDevice, tmSerial, androidId, deviceModel;
            deviceModel = Build.MODEL;
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String authkey = settings.getString("authkey","");
            String email = settings.getString("email","");

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("deviceid", deviceId)
                    .appendQueryParameter("authkey", authkey)
                    .appendQueryParameter("lat", lat)
                    .appendQueryParameter("lng", longitude)
                    .appendQueryParameter("email", email);
            String query = builder.build().getEncodedQuery();
            new SendLocation().execute(query);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        @Override
        public void onDestroy() {
            // Cancel the persistent notification.
            Log.v(TAG, "DESTROYED");
            // Tell the user we stopped.
        }
        // Toast.makeText(getApplicationContext(), "Service Created",
        // Toast.LENGTH_SHORT).show();


    }
