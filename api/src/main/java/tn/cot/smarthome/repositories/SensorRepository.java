package tn.cot.smarthome.repositories;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import tn.cot.smarthome.entities.Sensor;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface SensorRepository extends CrudRepository<Sensor, String> {
    Optional<Sensor> findById(String id);;
    Stream<Sensor> findAll() ;
    Stream<Sensor> findByType(String type);
}