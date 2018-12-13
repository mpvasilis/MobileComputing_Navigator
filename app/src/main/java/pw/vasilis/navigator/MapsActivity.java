package pw.vasilis.navigator;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat=0;
    double longt=0;
    String points = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lat = getIntent().getDoubleExtra("lat", 0);
        longt = getIntent().getDoubleExtra("long", 0);
        points = getIntent().getStringExtra("points");
        Log.i("MAPS_ACTIVITY", points);

        if (lat == 0 && longt == 0)
            Toast.makeText(this, "No GPS location. Please wait GPS to fix.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng mylocation = new LatLng(lat, longt);
        mMap.addMarker(new MarkerOptions().position(mylocation).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

        if (isJSONValid(points)) {

            ArrayList<LatLng> coordList = new ArrayList<LatLng>();

            JSONArray arr = null;
            try {
                arr = new JSONArray(points);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < arr.length(); i++) {
                try {
                    coordList.add(new LatLng(Double.parseDouble(arr.getString(i).split(" ")[0].replace(",", "")), Double.parseDouble(arr.getString(i).split(" ")[1])));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(coordList);
            polylineOptions
                    .width(5)
                    .color(Color.RED);

            mMap.addPolyline(polylineOptions);
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
