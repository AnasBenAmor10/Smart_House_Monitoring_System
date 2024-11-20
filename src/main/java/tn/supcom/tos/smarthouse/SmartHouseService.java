package tn.supcom.tos.smarthouse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SmartHouseService {

    public String hello(String name) {
        return String.format("Hello '%s'.", name);
    }
}