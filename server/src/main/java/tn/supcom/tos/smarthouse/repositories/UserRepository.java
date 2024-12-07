package tn.supcom.tos.smarthouse.repositories;

import tn.supcom.tos.smarthouse.entities.User;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

import java.util.stream.Stream;
import java.util.Optional;
@Repository
public interface UserRepository extends CrudRepository<User,String> {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Stream<User> findAll() ;
}