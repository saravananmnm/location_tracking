package saro.location_tracking.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Map;
import java.util.Optional;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MeasureManager {
    //    private static final List<String> PERMISSIONS_LIST = Collections.singletonList(ACCESS_FINE_LOCATION);

    private static final int PERMISSION_REQUEST_CODE = 100;

    private final Activity activity;

    private Optional<LocationTrackingService> bgLocationService = Optional.empty();

    private boolean isServiceBinded = false;

    private Intent serviceIntent;

    private int gpsUpdateIntervalMillis;
    private int gpsUpdateMinDistanceMeters;


    /**
     * Constructor
     * @param activity
     * @param gpsIntervalMillis
     * @param gpsMinDistanceMeters
     */
    public MeasureManager(Activity activity, int gpsIntervalMillis, int gpsMinDistanceMeters ){
        this.activity = activity;
        this.gpsUpdateIntervalMillis = gpsIntervalMillis;
        this.gpsUpdateMinDistanceMeters = gpsMinDistanceMeters;
        serviceIntent = new Intent(activity.getApplication(), LocationTrackingService.class);
    }

    /**
     * To be called by activity - after receiving Request permission result
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @throws Exception
     */
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) throws Exception {
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                permissionStatus = PermissionStatus.GRANTED;
//            }else{
//                permissionStatus = PermissionStatus.NOT_GRANTED;
//            }
////            activate();
//        }
    }

    // ---------- public interface methods -------------

    /**
     * When measure is paused, but app closed, then on reopen I see service running, but not binded
     * and in order to know if service is actually savign to repo, i have to run it.. nonsense.. !!!
     * Need to get real information about saving to repo...
     * @return
     */

    public MeasureState getMeasureState(){
        boolean serviceRunning = isServiceRunning();
        boolean serviceBinded = isServiceBinded();
        boolean saveToRepo = isSavingToRepoEnabled();
        int positionsStored = bgLocationService.map(backgroundLocationService ->
                backgroundLocationService.getLocationRepository().getAllLocations().size())
                .orElse(-1);

        return new MeasureState(serviceRunning, serviceBinded, saveToRepo, readMeasureData(), arePermissionsGranted(), positionsStored);
    }


    /**
     * This method should start service, bind to it,
     * and start location tracking (saving to repo all the time)
     *
     * @throws Exception
     */
    public void startMeasure() throws Exception {
        activate();
    }


    /**
     * This method should just stop GPS listener (no positions receiving from GPS - no storing)
     */
    public void pauseMeasure() throws Exception {
        if(!bgLocationService.isPresent()) {
            throw new Exception("Not binded to service !");
        }
        bgLocationService.get().setSaveToRepo(false);
    }

    /**
     * This method should just turn on GPS location tracking
     */
    public void continueMeasure(){
        bgLocationService.get().setSaveToRepo(true);
    }

    /**
     * This method should stop Service and unbind from it
     */
    public void stopMeasure() throws Exception {
        deactivate();

    }

    public InMemoryLocationRepository getMeasureLocationsRepository() throws Exception {
        if(!bgLocationService.isPresent()) {
            throw new Exception("Not binded to service !");
        }
        return bgLocationService.get().getLocationRepository();
    }

    public Map<String, Object> readMeasureData(){
        return LocationTrackingService.metadata;
    }

    public void setMeasureData(Map<String, Object> metadata) throws Exception {
        LocationTrackingService.metadata = metadata;
    }

    public void requestPermissions() throws Exception {
        if (activity.checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            System.out.println("requesting permissions");
            activity.requestPermissions( new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }else{
            System.out.println("not requestign permissions");
        }
    }

    public void onActivityDestroy(){
        unBindService();
    }


    // -------------------------------------------------------------




    //  --------------    state  ---------------

    private boolean isServiceRunning(){
        return LocationTrackingService.isServiceRunning;
    }

    private boolean isSavingToRepoEnabled(){
        return LocationTrackingService.isSavingToRepoEnabled;
    }

    private boolean isServiceBinded(){
        return isServiceBinded;
    }



    /**
     * To call when Service is Running, but not binded..
     */
    private void bindToService(){
        activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * To call when destroying Activity
     */
    private void unBindService(){
        try {
            activity.unbindService(serviceConnection);
            isServiceBinded = false;
        }catch (IllegalArgumentException ex){
            System.out.println("service already unbinded");
        }
    }


    /**
     * Internal method invoked when background service binded - triggered by activate method
     */
    private void onServiceBinded(LocationTrackingService service){
        bgLocationService = Optional.of(service);
        isServiceBinded = true;

        bgLocationService.get().startLocationTracking(gpsUpdateIntervalMillis, gpsUpdateMinDistanceMeters);
    }

    /**
     * This method start background service and activate GPS signal tracking
     *
     * @throws Exception if already activated or user declined permissions
     */
    private void activate() throws Exception {
        System.out.println("try to activate service");
        if(!bgLocationService.isPresent()){
            if(!arePermissionsGranted()) {
                throw new Exception("Permissions not granted !");
            }
            startBackgroundLocationService();
        }else{
            throw new Exception("Already activated!");
        }
    }

    /**
     * Deactivate service: stop background geolocation service
     * @throws Exception
     */
    private void deactivate() throws Exception {
        if(bgLocationService.isPresent()){
            stopBackgroundLocationService();
            bgLocationService = Optional.empty();
        }else{
            throw new Exception("Already deactivated!");
        }
    }

    private boolean arePermissionsGranted(){
        return activity.checkSelfPermission( ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }







    private void startBackgroundLocationService(){
        System.out.println("startBackgroundLocationService");
        if(!isServiceRunning()){
            System.out.println("starting service");
            activity.startService(serviceIntent);
        }

        if(!isServiceBinded()){
            System.out.println("binding to service");
            bindToService();
        }
    }

    private void stopBackgroundLocationService(){
        bgLocationService.get().stopLocationTracking();
        unBindService();
        activity.stopService(new Intent(activity.getApplication(), LocationTrackingService.class));
    }





    private ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onServiceConnected(ComponentName className, IBinder service) {
            System.out.println("service binded");
            onServiceBinded(((LocationTrackingService.LocationServiceBinder) service).getService());
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onServiceDisconnected(ComponentName className) {
            bgLocationService = Optional.empty();
        }
    };
}

