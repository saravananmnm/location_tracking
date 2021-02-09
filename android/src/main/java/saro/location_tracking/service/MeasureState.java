package saro.location_tracking.service;

import java.util.HashMap;
import java.util.Map;

public class MeasureState {
    private boolean isRunning;
    private boolean isBinded;
    private boolean isSaveToRepoEnabled;
    private Map<String, Object> metadata;
    private boolean permissionGranted;
    private int locationsStored;

    public MeasureState(boolean isRunning, boolean isBinded, boolean isSaveToRepoEnabled,
                        Map<String, Object> metada, boolean permissionStatus, int locationsStored){
        this.isRunning = isRunning;
        this.isBinded = isBinded;
        this.isSaveToRepoEnabled = isSaveToRepoEnabled;
        this.metadata = metada;
        this.permissionGranted = permissionStatus;
        this.locationsStored = locationsStored;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isBinded() {
        return isBinded;
    }

    public boolean isSaveToRepoEnabled() {
        return isSaveToRepoEnabled;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public boolean isPermissionGranted() {
        return permissionGranted;
    }

    public int getLocationsStored() {
        return locationsStored;
    }

    public Map<String,Object> toHashMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("isRunning", isRunning);
        map.put("isBinded", isBinded);
        map.put("isSaveToRepoEnabled", isSaveToRepoEnabled);
        map.put("metada", metadata);
        map.put("permissionGranted", permissionGranted);
        map.put("locationsStored", locationsStored);

        return map;
    }

    @Override
    public String toString() {
        return "MeasureState{" +
                "isRunning=" + isRunning +
                ", isBinded=" + isBinded +
                ", isSaveToRepoEnabled=" + isSaveToRepoEnabled +
                ", metadata=" + metadata +
                ", permissionGranted=" + permissionGranted +
                ", locationsStored=" + locationsStored +
                '}';
    }
}
