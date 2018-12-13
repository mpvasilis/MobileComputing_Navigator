package pw.vasilis.navigator;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TrafficActivity extends AppCompatActivity implements HttpPostAsyncResponse {
    double lat = 0;
    double longt = 0;

    ImageButton trafficlow;
    ImageButton trafficnormal;
    ImageButton trafficdense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lat = getIntent().getDoubleExtra("lat", 0);
        longt = getIntent().getDoubleExtra("long", 0);

        trafficlow = findViewById(R.id.trafficlow);
        trafficnormal = findViewById(R.id.trafficnormal);
        trafficdense = findViewById(R.id.trafficdense);


        trafficlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("Low traffic condition", R.drawable.trafficlow);
            }
        });


        trafficnormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("Normal traffic condition", R.drawable.trafficnormal);

            }
        });


        trafficdense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("Dense traffic condition", R.drawable.trafficdense);

            }
        });
    }

    public void showDialog(String title, int image) {

        final Dialog dialog = new Dialog(TrafficActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);

        TextView titleText = dialog.findViewById(R.id.title_text);
        dialog.setCancelable(false);
        dialog.show();

        titleText.setText(title);

        final String trafficmsg = title.toLowerCase();

        Button cancelbtn = dialog.findViewById(R.id.cancelbtn);
        Button okbtn = dialog.findViewById(R.id.okbtn);
        ImageView trafficimage = dialog.findViewById(R.id.trafficimage);
        trafficimage.setImageResource(image);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> postData = new HashMap<>();
                postData.put("longitude", lat + "");
                postData.put("latitude", longt + "");
                postData.put("traffic", trafficmsg);
                Log.d("TRAFFIC", "Posting: lat:" + lat + " long:" + longt + " traffic:" + trafficmsg);
                if (isNetworkAvailable()) {
                    new HttpPostAsyncTask(postData, HttpPostType.TRAFFIC_POST, TrafficActivity.this).execute("http://160.40.60.207:8080/navigator.ws/server/postTrafficData");
                    Toast.makeText(TrafficActivity.this, "Sending traffic condition to server....", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TrafficActivity.this, "No internet connection. Cannot send traffic condition to server.", Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();

            }
        });
    }

    @Override
    public void postfinished(HttpPostType type, String result) {

        if (type == HttpPostType.TRAFFIC_POST) {

            Toast.makeText(TrafficActivity.this, "Traffic condition sent successfully.", Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonObj = new JSONObject(result);
                //TODO get http status code (int)

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("GPS_HTTP_POST_TRAFFIC", result);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
