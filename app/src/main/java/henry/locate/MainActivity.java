package henry.locate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Build;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = "Locate";
    private GoogleApiClient mGoogleApiClient;
    public static final String PREFS = "Settings" ;
    public static final String URL = "urlKey";
    public static final String Phone = "phoneKey";
    public static final String Email = "emailKey";
    public static final String Street = "streetKey";
    public static final String Place = "placeKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS, 4);
        String authkey = settings.getString("authkey","");
        if (authkey == "") {
         /*   mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();*/
            runSetup();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            startService(intent);        }

    }

    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
      //  mGoogleApiClient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void connectAccount(View view) {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        startService(intent);
        final String tmDevice, tmSerial, androidId, deviceModel;
        deviceModel = Build.MODEL;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        Log.v(TAG, deviceId);
        Log.v(TAG, deviceModel);
        String postParameters, email, password, serverURL, model, deviceid, lat, longitude;
        EditText txtEmail, txtPassword, txtURL;
        txtEmail = (EditText) this.findViewById(R.id.txtEmail);
        txtPassword = (EditText) this.findViewById(R.id.txtPassword);
        txtURL = (EditText) this.findViewById(R.id.txtURL);


        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        serverURL = txtURL.getText().toString();
        Location mLastLocation;
        mGoogleApiClient.connect();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        lat = String.valueOf(mLastLocation.getLatitude());
        longitude = String.valueOf(mLastLocation.getLongitude());
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("deviceid", deviceId)
                .appendQueryParameter("model", deviceModel)
                .appendQueryParameter("password", password)
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("long", longitude)
                .appendQueryParameter("email", email);
        String query = builder.build().getEncodedQuery();
        new RegisterApplication().execute(query);

  /*      try {
            url = new URL("http://127.0.0.1/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
       /* try {
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            URL url1 = new URL("http://www.google.com/");
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("deviceid", deviceId)
                    .appendQueryParameter("model", deviceModel)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("lat", lat)
                    .appendQueryParameter("long", longitude)
                    .appendQueryParameter("email", email);
            String query = builder.build().getEncodedQuery();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();*/
       /*     List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("deviceid", deviceId));
            params.add(new BasicNameValuePair("model", deviceModel));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("lat", lat));
            params.add(new BasicNameValuePair("long", longitude));
// read the response
            System.out.println("Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = IOUtils.toString(in, "UTF-8");
            System.out.println(response);
*/


        //   urlConnection.setFixedLengthStreamingMode(
        //      params.toString().getBytes().length);
        //  try (OutputStream output = urlConnection.getOutputStream()) {
        //    output.write(getQuery(params));
        //}



     /*   } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

    private class RegisterApplication extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                URL url1 = new URL("http://www.google.com/");
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(params[0]);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                InputStream stream = null;
                stream = conn.getInputStream();
                String content = readIt(stream,500);
                Log.v(TAG,"content = " + content);
                return content;
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, result);
        }
    }

        @Override
        public void onConnected(Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

}