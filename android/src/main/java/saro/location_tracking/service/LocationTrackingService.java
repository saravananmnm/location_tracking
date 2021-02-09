package saro.location_tracking.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Map;

public class LocationTrackingService extends Service {
    public static boolean isServiceRunning = false;
    public static boolean isSavingToRepoEnabled = true;
    public static Map<String, Object> metadata;


    LocationListenerImpl locationListener;
    private final LocationServiceBinder binder = new LocationServiceBinder();


    private static final int FOREGROUND_ID = 19930122;
    private InMemoryLocationRepository locationRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocationTrackingService.isServiceRunning = true;
        startForeground(FOREGROUND_ID, getNotification());
        locationRepository = new InMemoryLocationRepository();
        System.out.println("Creating bg service..");
        locationListener = new LocationListenerImpl(this, location -> {
            if(LocationTrackingService.isSavingToRepoEnabled){
                locationRepository.insertLocation(location);
            }else{
                System.out.println("not saving to repo..");
            }
        });

    }

    public void startLocationTracking(int intervalMillis, int minDistanceMeters){
        locationListener.startListening(intervalMillis,minDistanceMeters);
    }

    public void stopLocationTracking(){
        locationListener.stopListening();
    }

    public void setSaveToRepo(boolean saveToRepo) {
        LocationTrackingService.isSavingToRepoEnabled = saveToRepo;
    }

    public InMemoryLocationRepository getLocationRepository() {
        return locationRepository;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationListener.stopListening();
        LocationTrackingService.isServiceRunning = false;
    }

    public class LocationServiceBinder extends Binder {
        public LocationTrackingService getService() {
            return LocationTrackingService.this;
        }
    }


    private Notification getNotification() {

        System.out.println("getNotification");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_01", "Location_tracking", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_01")
                .setContentTitle("")
                .setContentText("Location tracking is active")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }
}
