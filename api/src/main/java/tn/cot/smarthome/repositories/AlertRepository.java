package tn.cot.smarthome.repositories;

import tn.cot.smarthome.entities.Alert;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

import java.util.Optional;

@Repository
public interface AlertRepository extends CrudRepository<Alert, String> {
    Optional<Alert> findbyid(String id);
}