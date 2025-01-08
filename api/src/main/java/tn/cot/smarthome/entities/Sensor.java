package tn.cot.smarthome.entities;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity("sensors")
public class Sensor implements Serializable {

    @Id
    @Column("_id")
    private String id;

    @Column("type")
    private String type; // E.g., "temperature", "humidity", "water_consumption", etc.

    @Column("value")
    private double value;

    @Column("measurement_time")
    private LocalDateTime measurementTime;

    @Column("status")
    private String status; // E.g., "active", "inactive", "error", etc.

    // Constructors
    public Sensor() {
        this.id = UUID.randomUUID().toString();
    }

    public Sensor(String type, double value, LocalDateTime measurementTime, String status) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.value = value;
        this.measurementTime = measurementTime;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDateTime getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(LocalDateTime measurementTime) {
        this.measurementTime = measurementTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // toString Method
    @Override
    public String toString() {
        return "Temperature{" +
                "_id='" + id + '\'' +
                ", value=" + value +
                ", measurementTime=" + measurementTime +
                ", status='" + status + '\'' +
                '}';
    }
}
