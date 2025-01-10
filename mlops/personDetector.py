import cv2
from picamera2 import Picamera2
from ultralytics import YOLO
import paho.mqtt.client as mqtt
import json
import time

# MQTT setup
mqtt_broker = "localhost"
mqtt_port = 1883
mqtt_topic = "/smarthome/camera"

client = mqtt.Client()
client.connect(mqtt_broker, mqtt_port)

# Set up the camera with Picam
picam2 = Picamera2()
picam2.preview_configuration.main.size = (640, 640)
picam2.preview_configuration.main.format = "RGB888"
picam2.preview_configuration.align()
picam2.configure("preview")
picam2.start()

# Load YOLOv8
model = YOLO("yolov8n.pt")

while True:
    # Capture a frame from the camera
    frame = picam2.capture_array()

    # Run YOLO model on the captured frame
    results = model(frame)
    detections = results[0].boxes.data  # Bounding box data

    # Check if a person is detected
    person_detected = False
    for detection in detections:
        label = int(detection[-1])  # YOLO class ID
        if label == 0:  # 0 is usually the class ID for 'person'
            person_detected = True
            break

    if person_detected:
        # Publish alert to MQTT
        alert_message = {
            "alert": "Person detected",
            "timestamp": time.time()
        }
        client.publish(mqtt_topic, json.dumps(alert_message))
        print("Person detected! Alert sent.")

    # Annotate frame with detection results
    annotated_frame = results[0].plot()

    # Display the frame
    cv2.imshow("Camera", annotated_frame)

    # Exit if 'q' is pressed
    if cv2.waitKey(1) == ord("q"):
        break

# Cleanup
cv2.destroyAllWindows()
client.disconnect()
