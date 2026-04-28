package com.prayertimes.app.location;

/**
 * Represents a searchable city entry from the offline database.
 */
public class CityEntry {
    public final String city;
    public final String country;
    public final double latitude;
    public final double longitude;
    public final double elevation;

    public CityEntry(String city, String country, double latitude, double longitude, double elevation) {
        this.city      = city;
        this.country   = country;
        this.latitude  = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public String displayName() {
        return city + ", " + country;
    }
}
