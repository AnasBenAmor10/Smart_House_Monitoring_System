package tn.cot.smarthome.services;

import jakarta.enterprise.context.ApplicationScoped;
import tn.cot.smarthome.entities.Identity;
import tn.cot.smarthome.entities.Sensor;
import jakarta.inject.Inject;
import tn.cot.smarthome.repositories.SensorRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SensorServices {

    @Inject
    private SensorRepository sensorDataRepository;

    public List<Sensor> getAllSensorData() {
        return sensorDataRepository.findAll().collect(Collectors.toList());
    }

    public List<Sensor> getSensorDataByType(String type) {
        return sensorDataRepository.findByType(type).collect(Collectors.toList());
    }

    public Sensor getSensorDataById(String id) {
        return sensorDataRepository.findById(id).orElse(null);
    }

    public Sensor createSensorData(Sensor sensorData) {
        return sensorDataRepository.save(sensorData);
    }

    public Sensor updateSensorData(String id, Sensor updatedSensorData) {
        Sensor existingSensorData = sensorDataRepository.findById(id).orElse(null);
        if (existingSensorData != null) {
            updatedSensorData.setId(id);
            return sensorDataRepository.save(updatedSensorData);
        }
        return null;
    }

    public boolean deleteSensorData(String id) {
        Sensor existingSensorData = sensorDataRepository.findById(id).orElse(null);
        if (existingSensorData != null) {
            sensorDataRepository.deleteById(id);
            return true;
        }
        return false;
    }
    private SensorRepository sensorRepository;

    public Sensor getMostRecentSensorByType(String type) {
        return sensorDataRepository.findByType(type)
                .max(Comparator.comparing(Sensor::getMeasurementTime))
                .orElse(null);
    }


}
