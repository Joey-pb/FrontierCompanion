package wgu.jbas127.frontiercompanion.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "exhibit")
public class Exhibit {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "era")
    private String era;
    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "image_res_name")
    private String imageResName;

    public Exhibit(String name, double latitude, double longitude,
                   String description, String era, String location,
                   String imageResName) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.era = era;
        this.location = location;
        this.imageResName = imageResName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageResName(String imageResName) {
        this.imageResName = imageResName;
    }

    public String getImageResName() {
        return imageResName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Exhibit)) return false;
        Exhibit exhibit = (Exhibit) o;
        return id == exhibit.id && Double.compare(latitude, exhibit.latitude) == 0 && Double.compare(longitude, exhibit.longitude) == 0 && Objects.equals(name, exhibit.name) && Objects.equals(description, exhibit.description) && Objects.equals(era, exhibit.era) && Objects.equals(location, exhibit.location) && Objects.equals(imageResName, exhibit.imageResName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, latitude, longitude, description, era, location, imageResName);
    }
}

