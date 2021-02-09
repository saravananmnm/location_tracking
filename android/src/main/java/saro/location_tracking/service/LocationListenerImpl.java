package saro.location_tracking.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.util.Consumer;

public class LocationListenerImpl implements LocationListener {

    private LocationManager locationManager;
    private Context ctx;
    private Consumer<Location> onLocationChangedCallback;

    public LocationListenerImpl(Context ctx, Consumer<Location> onLocationChangedCallback) {
        this.ctx = ctx;
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        this.onLocationChangedCallback = onLocationChangedCallback;
    }


    @SuppressLint("MissingPermission")
    public void startListening(int intervalMillis, int minDistanceMeter) {
        System.out.println("start listening");

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(ctx,"GPS ENABLED", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(ctx,"GPS NOT ENABLED", Toast.LENGTH_SHORT).show();
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intervalMillis, minDistanceMeter, this);
    }

    public void stopListening(){
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        onLocationChangedCallback.accept(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
