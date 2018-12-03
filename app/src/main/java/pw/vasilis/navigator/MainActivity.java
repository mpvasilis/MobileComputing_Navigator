package pw.vasilis.navigator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements GPSCallback{
    private GPSManager gpsManager = null;
    private double speed = 0.0;
    Boolean isGPSEnabled=false;
    LocationManager locationManager;
    double currentSpeed,kmphSpeed;
    TextView txtview;
    ImageButton imageButton;
    ConstraintLayout constraintLayout;
    String check_day_night = "day";
    double lat;
    double longt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout= findViewById(R.id.clayout);
        txtview = findViewById(R.id.speed);
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
        } else {
            gpsManager.showSettingsAlert();
        }

        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra("lat",lat);
                i.putExtra("long",longt);
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
        Log.i("GPS_UPDATE", ""+lat+" "+longt+" "+currentSpeed+" "+kmphSpeed);

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
            } else {
                check_day_night = "night";
                constraintLayout.setBackgroundResource(R.drawable.bbg);
                imageButton.setImageResource(R.drawable.bmap);
            }

        System.out.println("Now is " + check_day_night);
    }


}
