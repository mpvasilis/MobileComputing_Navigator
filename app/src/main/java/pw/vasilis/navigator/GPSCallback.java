package pw.vasilis.navigator;

import android.location.Location;

public interface GPSCallback
{
void onGPSUpdate(Location location);
}