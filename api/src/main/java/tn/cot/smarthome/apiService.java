package tn.cot.smarthome;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class apiService {

    public String hello(String name) {
        return String.format("Hello '%s'.", name);
    }
}