package tn.cot.smarthome.repositories;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import tn.cot.smarthome.entities.Tenant;


@Repository
public interface TenantRepository extends CrudRepository<Tenant, String> {
    Tenant findByName(String name);
}
