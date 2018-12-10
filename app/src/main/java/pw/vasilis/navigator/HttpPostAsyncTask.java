package pw.vasilis.navigator;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.transform.Result;

public class HttpPostAsyncTask extends AsyncTask<String, String, String> {

    // This is the JSON body of the post
    public HttpPostAsyncResponse delegate = null;
    JSONObject postData;

    // This is a constructor that allows you to pass in the JSON body
    public HttpPostAsyncTask(Map<String, String> postData, HttpPostAsyncResponse delegate) {
        if (postData != null) {
            this.postData = new JSONObject(postData);
            this.delegate=delegate;
        }
    }

    @Override
    protected void onPreExecute() {
        Log.d("GPS_HTTP_POST", " GPS_HTTP_POST is about to start...");

    }

   @Override
    protected void onPostExecute(String result) {
        delegate.postfinished(result);
    }

    @Override
    protected String doInBackground(String... params) {

        Log.d("GPS_HTTP_POST", "new post to "+params[0]);

       try {
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
           if (this.postData != null) {
               OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
               writer.write(postData.toString());
               writer.flush();
               writer.close();

           }
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                String response = convertInputStreamToString(inputStream);
                Log.d("GPS_HTTP_POST", response);
                return response;


            } else {

                Log.d("GPS_HTTP_POST", "Status code:"+statusCode);
            }

        } catch (Exception e) {
            Log.e("GPS_HTTP_POST", e.getLocalizedMessage());
        }
        return "";
    }



    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}