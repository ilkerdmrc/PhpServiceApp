package com.idemirci.php.app;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String SERVICE_URL = "http://www.gdoguturk.com/irun/addLocationPost.php";
    private static final String GET_SERVICE_URL = "http://www.gdoguturk.com/irun/getLocations.php";
    private Button btnAddLocation, btnGetLocations;
    private TextView txtData;

    HashMap<String , String> postDataParams = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddLocation = (Button) findViewById(R.id.btnAddLocation);
        btnGetLocations = (Button) findViewById(R.id.btnGetLocations);
        txtData = (TextView) findViewById(R.id.txtData);

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceJob job = new ServiceJob();
                job.execute();
            }
        });

        btnGetLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetDataJob job = new GetDataJob();
                job.execute();
            }
        });
    }




    private class ServiceJob extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            postDataParams.put("lat", "123");
            postDataParams.put("lng", "888");
            postDataParams.put("USERID", "fromAndroid");

            return performPostCall(SERVICE_URL, postDataParams);
        }

        @Override
        protected void onPostExecute(String message) {
            txtData.setText(message);
        }
    }


    private class GetDataJob extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return performGetData();
        }

        @Override
        protected void onPostExecute(String result) {
            txtData.setText(result);
        }
    }

    private String performGetData(){
        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            URL url = new URL(GET_SERVICE_URL);
            urlConnection = (HttpURLConnection) url.openConnection();

            int code = urlConnection.getResponseCode();

            if(code==200){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                if (in != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                }
                in.close();
            }

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            urlConnection.disconnect();
        }
        return result;
    }



    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;

                    Log.e("Res:", response);
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
