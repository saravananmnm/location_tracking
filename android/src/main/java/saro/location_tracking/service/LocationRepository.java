package saro.location_tracking.service;

import android.location.Location;

import java.util.List;

public interface LocationRepository {

    public void insertLocation(final Location location);

    public List<Location> getAllLocations();

    public List<Location> getAllLocationWhereTimeBiggerThan(long time);

    public void clearAll();

}