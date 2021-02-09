package saro.location_tracking;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import saro.location_tracking.service.FlutterUtils;
import saro.location_tracking.service.MeasureManager;
import saro.location_tracking.service.MeasureState;

import static android.content.ContentValues.TAG;

/** LocationTrackingPlugin */
public class LocationTrackingPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity _activity;
  private MeasureManager _measureManager;
  private final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "location_tracking_plugin");
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "location_tracking_plugin");
    channel.setMethodCallHandler(new LocationTrackingPlugin());
  }


  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    String methodName = call.method;
    String successMsg = "OK";

    try{
      initializeMeasureManager();

      if(methodName.equals("startLocationTracking")){
        _measureManager.startMeasure();
        result.success(successMsg);
      }
      else if(methodName.equals("stopLocationTracking")){
        _measureManager.stopMeasure();
        result.success(successMsg);
      }

      else if(methodName.equals("requestPermissions")){
        _measureManager.requestPermissions();
        result.success(successMsg);
      }
      else if(methodName.equals("getAllLocations")){
        List<Location> locations = _measureManager.getMeasureLocationsRepository().getAllLocations();
        List<Map<String, Object>> locationsMap = FlutterUtils.locationsToListOfMaps(locations);
        result.success(locationsMap);
      }
      else if(methodName.equals("getAllLocationsWithTimeBiggerThan")){
        long time = call.argument("time");
        List<Location> locations = _measureManager.getMeasureLocationsRepository().getAllLocationWhereTimeBiggerThan(time);
        List<Map<String, Object>> locationsMap = FlutterUtils.locationsToListOfMaps(locations);
        result.success(locationsMap);
      }

      else {
        result.notImplemented();
      }
    }catch (Exception ex){
      result.error("-1", "ERROR for method:  " + methodName + " : " + ex.getMessage(), ex);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void initializeMeasureManager(){
    if(_measureManager == null){
      _measureManager = new MeasureManager(_activity, 500, 5);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    System.out.println("onAttachedToActivity");
    _activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    System.out.println("onDetachedFromActivityForConfigChanges");
    _activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    System.out.println("onReattachedToActivityForConfigChanges");
    _activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    System.out.println("onDetachedFromActivity");
    _activity = null;
  }
}
