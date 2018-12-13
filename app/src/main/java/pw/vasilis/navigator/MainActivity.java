package pw.vasilis.navigator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements GPSCallback, HttpPostAsyncResponse{
    private GPSManager gpsManager = null;
    private double speed = 0.0;
    Boolean isGPSEnabled=false;
    LocationManager locationManager;
    double currentSpeed,kmphSpeed;
    TextView txtview;

    ImageButton imageButton;
    ImageButton trafficbutton;

    ConstraintLayout constraintLayout;
    String check_day_night = "day";
    double lat;
    double longt;

    TextView estimatedtxt;
    TextView remainingTimetxt;
    TextView remainingKmtxt;
    TextView scheduledtxt;

    ImageView arrowimg;

    String points = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout= findViewById(R.id.clayout);

        txtview = findViewById(R.id.speed);
        estimatedtxt = findViewById(R.id.estimatedTime);
        remainingTimetxt = findViewById(R.id.remainingTime);
        remainingKmtxt = findViewById(R.id.remainingKm);
        scheduledtxt= findViewById(R.id.scheduledTime);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSManager(MainActivity.this);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSEnabled) {
            gpsManager.startListening(getApplicationContext());
            gpsManager.setGPSCallback(this);
            Toast.makeText(this, "Please wait for GPS to fix. This may take a while.", Toast.LENGTH_SHORT).show();

        } else {
            gpsManager.showSettingsAlert();
            Toast.makeText(this, "Please enable GPS.", Toast.LENGTH_SHORT).show();
        }

        imageButton = findViewById(R.id.imageButton);
        trafficbutton = findViewById(R.id.trafficbutton);

        arrowimg = findViewById(R.id.arrow);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra("lat",lat);
                i.putExtra("long",longt);
                i.putExtra("points", points);

                startActivity(i);
            }
        });

        trafficbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TrafficActivity.class);
                i.putExtra("lat", lat);
                i.putExtra("long", longt);
                startActivity(i);
            }
        });

        try {
            checkDayNight();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onGPSUpdate(Location location) {
        speed = location.getSpeed();
        lat = location.getLatitude();
        longt = location.getLongitude();
        currentSpeed = round(speed,3,BigDecimal.ROUND_HALF_UP);
        kmphSpeed = round((currentSpeed*3.6),0,BigDecimal.ROUND_HALF_UP);
        int kmph_int= (int) kmphSpeed;
        txtview.setText(kmph_int+"");
        Map<String, String> postData = new HashMap<>();
        postData.put("speed", speed+"");
        postData.put("longitude", lat+"");
        postData.put("latitude", longt+"");

        if (isNetworkAvailable()) {
            new HttpPostAsyncTask(postData, HttpPostType.GET_DATA_POST, this).execute("http://vasilis.pw/mobilecomputing/getData.php"); //160.40.60.207:8080/navigator.ws/server/getData
        } else {
            Log.d("MAIN", "No internet connection - Cannot post to server GPS data");

        }
        Log.i("GPS_UPDATE", ""+lat+" "+longt+" "+currentSpeed+" "+kmphSpeed);

    }


    @Override
    public void postfinished(HttpPostType type, String result) {

        if (type == HttpPostType.GET_DATA_POST) {

            JsonParser data = new JsonParser(result);

            estimatedtxt.setText(data.getEstimated());

            scheduledtxt.setText(data.getScheduled());

            remainingTimetxt.setText(data.getRtrip());

            remainingKmtxt.setText(data.getRdistance());

            points = data.getPoints().toString();

            try {
                setArrow(Integer.parseInt(data.getPspeed()), Integer.parseInt(data.getCspeed()), data.getScheduled(), data.getEstimated());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.i("GPS_HTTP_POST_MAIN", result);
        }

    }

    @Override
    protected void onDestroy() {
        gpsManager.stopListening();
        gpsManager.setGPSCallback(null);
        gpsManager = null;
        super.onDestroy();
    }
    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    public void checkDayNight() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
        Date date_from = formatter.parse("06:00");
        Date date_to = formatter.parse("18:00");
        Date dateNow = formatter.parse(formatter.format(new Date()));

        System.out.println("date_to: " + date_from);
        System.out.println("date_to: " + date_to);
        System.out.println("dateNow: " + dateNow);


            if (dateNow.after(date_from) && dateNow.before(date_to)) {
                check_day_night = "day";
                constraintLayout.setBackgroundResource(R.drawable.wbg);
                imageButton.setImageResource(R.drawable.wmap);
                trafficbutton.setImageResource(R.color.white);
                trafficbutton.setBackgroundResource(R.color.white);

            } else {
                check_day_night = "night";
                constraintLayout.setBackgroundResource(R.drawable.bbg);
                imageButton.setImageResource(R.drawable.bmap);
                trafficbutton.setImageResource(R.color.black);
                trafficbutton.setBackgroundResource(R.color.black);


            }

        System.out.println("Now is " + check_day_night);
    }

    public void setArrow(int pspeed, int cspeed, String scheduledtime, String estimatedtime) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
        Date scheduled = formatter.parse(scheduledtime);
        Date estimated = formatter.parse(estimatedtime);

        if ((pspeed - cspeed) > 5) {
            setArrowUp();

        } else if ((cspeed - pspeed) > 5) {
            setArrowDown();
        } else if (estimated.after(scheduled)) {
            estimatedtxt.setTextColor(Color.RED);
            setArrowUp();
        } else {
            setArrowNothing();

        }

        if (estimated.after(scheduled)) {
            estimatedtxt.setTextColor(Color.RED);
        } else {
            estimatedtxt.setTextColor(Color.parseColor("#01aabf"));
        }
    }

    public void setArrowUp() {
        arrowimg.setImageResource(R.drawable.up);
    }

    public void setArrowDown() {
        arrowimg.setImageResource(R.drawable.down);
    }

    public void setArrowNothing() {
        arrowimg.setImageResource(android.R.color.transparent);
    }




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
