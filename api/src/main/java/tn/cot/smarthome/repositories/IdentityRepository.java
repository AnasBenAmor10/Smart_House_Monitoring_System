package tn.cot.smarthome.repositories;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import tn.cot.smarthome.entities.Identity;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface IdentityRepository extends CrudRepository<Identity, String> {
    Optional<Identity> findById(String id);
    Optional<Identity> findByEmail(String email);
    Stream<Identity> findAll() ;
    Optional<Identity> findByUsername(String username);
}