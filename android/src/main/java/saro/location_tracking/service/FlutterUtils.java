package saro.location_tracking.service;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlutterUtils {
    /**
     * Converts lis tof locations to list of map
     * @param locations
     * @return
     */
    public static List<Map<String, Object>> locationsToListOfMaps(List<Location> locations) {
        List<Map<String, Object>> map_list = new ArrayList<>();

        for(Location l : locations){
            map_list.add(FlutterUtils.locationToMap(l));
        }

        return  map_list;
    }

    /**
     * converts single location to map of strings and objects
     * @param location
     * @return
     */
    public static Map<String, Object> locationToMap(Location location){
        Map<String, Object> map = new HashMap<>();

        map.put("latitude", location.getLatitude());
        map.put("longitude", location.getLongitude());
        map.put("time", location.getTime());
        map.put("altitude", location.getAltitude());
        map.put("bearing", location.getBearing());
        map.put("speed", location.getSpeed());
        map.put("accuracy",location.getAccuracy());
        map.put("provider", location.getProvider());

        return map;
    }
}
