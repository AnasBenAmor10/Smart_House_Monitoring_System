package tn.cot.smarthome.mqtt;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import tn.cot.smarthome.entities.Sensor;
import tn.cot.smarthome.repositories.SensorRepository;

import javax.net.ssl.SSLSocketFactory;
import java.time.LocalDateTime;

@Singleton
@Startup
public class MqttConnection {
    @Inject
    private SensorRepository sensorRepository;

    private static final Config config = ConfigProvider.getConfig();
    private final String uri = config.getValue("mqtt.uri", String.class);
    private final String username = config.getValue("mqtt.username", String.class);
    private final String password = config.getValue("mqtt.password", String.class);

    public void sendMessage(MqttClient client, String msg, String topic) throws MqttException {
        MqttMessage message = new MqttMessage(msg.getBytes());
        client.publish(topic,message);
    }

    @PostConstruct
    public void start() {
        try {
            System.out.println("Connecting to MQTT broker...");

            MqttClient client = new MqttClient(
                    uri,
                    MqttClient.generateClientId(),
                    new MemoryPersistence()
            );

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
            mqttConnectOptions.setSocketFactory(SSLSocketFactory.getDefault());

            client.connect(mqttConnectOptions);
            System.out.println("Connected to MQTT broker at " + uri);

            // Subscribe to all sensor topics
            String topic = "/smarthome/sensors/#";
            client.subscribe(topic);
            System.out.println("Subscribed to topic: " + topic);

            // Set the callback for incoming messages
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("Message received on topic: " + topic);
                    System.out.println("Message: " + new String(message.getPayload()));
                    String messagePayload = new String(message.getPayload());
                    double value;
                    try {
                        value = Double.parseDouble(messagePayload);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid message payload for topic " + topic + ": " + messagePayload);
                        return;
                    }
                    LocalDateTime measurementTime = LocalDateTime.now();
                    String status = "active";
                    String type = null;

                    switch (topic) {
                        case "/smarthome/sensors/temperature":
                            type = "temperature";
                            break;
                        case "/smarthome/sensors/humidity":
                            type = "humidity";
                            break;
                        case "/smarthome/sensors/airquality":
                            type = "airquality";
                            break;
                        case "/smarthome/sensors/overallwaterconsumption":
                            type = "overallwaterconsumption";
                            break;
                        case "/smarthome/sensors/gardenwaterconsumption":
                            type = "gardenwaterconsumption";
                            break;
                        case "/smarthome/sensors/kitchenwaterconsumption":
                            type = "kitchenwaterconsumption";
                            break;
                        default:
                            System.err.println("Unknown topic: " + topic);
                            return;
                    }

                    // Create and save the sensor data
                    Sensor sensor = new Sensor(type, value, measurementTime, status);
                    sensorRepository.save(sensor);
                    System.out.println("Sensor data saved: " + sensor);
                }


                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete: " + token.getMessageId());
                }
            });

        } catch (MqttException e) {
            System.err.println("Error connecting to MQTT broker: " + e.getMessage());
        }
    }
}
