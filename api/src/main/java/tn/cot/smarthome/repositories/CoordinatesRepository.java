package tn.cot.smarthome.repositories;

import tn.cot.smarthome.entities.Coordinates;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

import java.util.Optional;


@Repository
public interface CoordinatesRepository extends CrudRepository<Coordinates, String> {
    Optional<Coordinates> findByEmail(String email);
}