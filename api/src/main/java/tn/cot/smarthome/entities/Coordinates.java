package tn.cot.smarthome.entities;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity("coordinates")
public class Coordinates {

    @Id
    @Column("_id")
    private String id;

    @Column("email")
    private String email;

    @Column("longitude")
    private double longitude;

    @Column("latitude")
    private double latitude;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // Constructor
    public Coordinates() {
        // Default constructor
    }

    public Coordinates(String email, double longitude, double latitude) {
        this.email = email;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
