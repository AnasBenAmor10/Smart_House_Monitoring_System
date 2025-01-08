package tn.cot.smarthome.controllers;

import tn.cot.smarthome.entities.Sensor;
import tn.cot.smarthome.services.SensorServices;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/sensors")
public class SensorController {

    @Inject
    private SensorServices sensorDataServices;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors() {
        List<Sensor> sensors = sensorDataServices.getAllSensorData();
        return Response.ok(sensors).build();
    }

    @GET
    @Path("/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorsByType(@PathParam("type") String type) {
        List<Sensor> sensors = sensorDataServices.getSensorDataByType(type);
        return Response.ok(sensors).build();
    }

    @GET
    @Path("/id/{_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("_id") String id) {
        Sensor sensor = sensorDataServices.getSensorDataById(id);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(sensor).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensorData) {
        Sensor created = sensorDataServices.createSensorData(sensorData);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSensor(@PathParam("_id") String id, Sensor sensorData) {
        Sensor updated = sensorDataServices.updateSensorData(id, sensorData);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSensor(@PathParam("_id") String id) {
        boolean deleted = sensorDataServices.deleteSensorData(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
