package be.howest.ti.mars.logic.domain;

public class Location {

    private static final double STANDARD_LONGITUDE = 13.2692;

    private static final double STANDARD_LATITUDE = -38.9541;

    private final String locationName;

    private final double longitude;

    private final double latitude;

    public Location(String locationName, double longitude, double latitude) {
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location(String locationName){
        this(locationName, STANDARD_LONGITUDE, STANDARD_LATITUDE);
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public String toString() {
        return "Location:" +
                "\nlocationName='" + locationName +
                "\n longitude=" + longitude +
                "\n latitude=" + latitude;
    }
}
