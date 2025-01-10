var broker = {
    hostname: "3f8df013054946d4ab9fb4775293070e.s1.eu.hivemq.cloud",
    port: 8884,
    clientId: "smarthome"
};

var topic = "smarthome";

// MQTT client instance
var client = new Paho.MQTT.Client(broker.hostname, broker.port, broker.clientId);

function onConnect() {
    
    client.subscribe(topic);

}

export function toggleDevice(switchElement, endpoint) {
    const isChecked = switchElement.checked;
    console.log(`Device is now ${isChecked ? 'ON' : 'OFF'}`);
    isChecked ? sendMessage(`/smarthome/devices/${endpoint}`, 'ON') : sendMessage(`/smarthome/devices/${endpoint}`, 'OFF')

  }

function onFailure(message) {
    console.log("Connection to MQTT broker failed: " + message.errorMessage);
} 

function onMessageArrived(message) {
    console.log("Received message: " + message.payloadString); 
}

// Set callback functions
client.onConnectionLost = onFailure;
client.onMessageArrived = onMessageArrived;

// Connect to MQTT broker
// Function to publish a message to a topic
export function sendMessage(topic, message) {
    if (client.isConnected()) {
    var mqttMessage = new Paho.MQTT.Message(message);
    mqttMessage.destinationName = topic;
    client.send(mqttMessage);
    console.log(`Message sent to topic '${topic}': ${message}`);
    } else {
    console.error("Client is not connected. Unable to send message.");
    alert("Client is not connected. Please try again later.");
    }
    }

export function connectClient(){
    client.connect({
        onSuccess: onConnect,
        onFailure: onFailure,
        useSSL: true,
        userName: "SmartHomeCot",
        password: "SmartHomeCot2025*"
    });
}