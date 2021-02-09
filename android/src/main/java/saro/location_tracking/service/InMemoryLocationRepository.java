package saro.location_tracking.service;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryLocationRepository implements LocationRepository {
    private List<Location> locations = new ArrayList<>();

    @Override
    public void insertLocation(Location location) {
        locations.add(location);
    }

    @Override
    public List<Location> getAllLocations() {
        return locations;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Location> getAllLocationWhereTimeBiggerThan(long time) {
        return locations.stream().filter(l -> l.getTime() > time).collect(Collectors.toList());
    }

    @Override
    public void clearAll(){
        locations.clear();
    }

}
