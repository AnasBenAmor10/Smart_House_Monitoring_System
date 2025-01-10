# Smart House Monitoring System

## ✨ Project Overview
In today’s interconnected world, the concept of a “smart house” has gained significant traction. This project introduces a comprehensive IoT-based smart house monitoring system powered by a Raspberry Pi. The system is designed to oversee key household elements such as lighting, water usage, and security. It integrates sensors, alarms, and computer vision to create a connected and intelligent home environment.
## 🏗️ Architecture 
![Dashboard Screenshot](/images/architecture.png)
## 🔧 Features
- **Resource Management:** Monitor household parameters such as lighting and water usage.
- **Security Enhancements:** Use computer vision and proximity sensors to detect unauthorized activities.
- **Real-time Monitoring:** View live data through a Progressive Web Application (PWA).
- **Automated Alerts:** Receive notifications for unusual or critical events.

## ⚖️ Equipment
- **Raspberry Pi 4 (8GB):** Central controller.
- **ESP32CAM:** Provides computer vision for security monitoring.
- **DHT22 Sensors:** Measure temperature and humidity.
- **Gas Sensor:** Detects hazardous fumes.
- **Proximity Sensors:** Detect movement around entry points.
- **Water Flow Sensors:** Monitor water usage in various areas.
- **Electrovalve**: Controls water flow and can shut off supply during emergencies.
- **Servo Motor**: Used to automate the opening and closing of doors (garage and house doors).
- **Garage Door System**: Automated using servo motors, controlled remotely via the PWA.
- **House Door Lock**: Automated with servo motors for secure locking/unlocking.
- **Motion-Activated Light**: Lights turn on automatically when a person is detected.
- **Command-Activated Light**: Lights can also be controlled via commands from the PWA.
- **Alarm System:** Alerts the user in case of unauthorized activity or critical events

## 🚀 Technologies
### Backend
- **MQTT:** Lightweight messaging protocol for IoT components.
- **MongoDB:** NoSQL database for data storage.
- **HiveMQ:** MQTT broker optimized for IoT communication.

### Middleware
- **Jakarta EE:** Java-based framework for secure API management.
- **WildFly:** Java application server for hosting middleware.
### MLOps Part
An MLOps pipeline will streamline the ongoing training, deployment, and monitoring of the
object detection model. Key features include:

- **Automated Training and Deployment**: The MLOps framework will periodically
train and update the model with relevant data, deploying it to ensure real-time detec-
tion capabilities on the ESP32-CAM.
- **Performance Monitoring**: Automated monitoring will track the model’s accuracy
and detect performance drift, ensuring reliable person detection across different condi-
tions.
- **Alerts and Notifications**: Upon detecting a person, the model will trigger alerts and
predefined actions to enhance home security.
### Node-Redd-Flow
![Dashboard Screenshot](/images/node-red_flow.png)

### Frontend
- **PWA:** Responsive web app for cross-platform accessibility.

### IoT Integration
- **Node-RED:** For workflow automation.
- **YOLO:** For computer vision-based security monitoring.

## ⚡ How to Run the Project

### Prerequisites
1. Install **WildFly**, **Java**, and **Maven**.
2. Configure virtual hosts in WildFly.
3. Ensure Node-RED and HiveMQ are set up.

### Steps

#### 1. Set Up WildFly
- Start WildFly and access the CLI tool (`jboss-cli.sh` or `jboss-cli.bat`).
- Add virtual hosts for API and IAM:

```bash
/subsystem=undertow/server=default-server/host=api-host:add(alias=["api.yourdomain.me"],default-web-module="api-1.0.war")
/subsystem=undertow/server=default-server/host=api-host/setting=access-log:add
/subsystem=undertow/server=default-server/host=api-host/setting=access-log:write-attribute(name=pattern,value="combined")
/subsystem=undertow/server=default-server/host=api-host/setting=access-log:write-attribute(name=prefix,value="api-yourapp")
/subsystem=undertow/server=default-server/host=api-host/filter-ref=hsts:add(predicate="equals(%p,8443)")
/subsystem=undertow/server=default-server/host=api-host/filter-ref=http-to-https:add(predicate="equals(%p,8080)")
```
```bash
/subsystem=undertow/server=default-server/host=iam-host:add(alias=["iam.yourdomain.me"],default-web-module="iam-1.0.war")
/subsystem=undertow/server=default-server/host=iam-host/setting=access-log:add
/subsystem=undertow/server=default-server/host=iam-host/setting=access-log:write-attribute(name=pattern,value="combined")
/subsystem=undertow/server=default-server/host=iam-host/setting=access-log:write-attribute(name=prefix,value="iam-yourapp")
/subsystem=undertow/server=default-server/host=iam-host/filter-ref=hsts:add(predicate="equals(%p,8443)")
/subsystem=undertow/server=default-server/host=iam-host/filter-ref=http-to-https:add(predicate="equals(%p,8080)")
```

#### 2. Configure Virtual Hosts in `jboss-web.xml`
Example for `iam-1.0.war`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web xmlns="http://www.jboss.com/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.jboss.com/xml/ns/javaee https://www.jboss.org/j2ee/schema/jboss-web_10_0.xsd"
           version="10.0">
    <context-root>/</context-root>
    <enable-websockets>true</enable-websockets>
    <virtual-host>iam-host</virtual-host>
</jboss-web>
```

#### 3. Enable CORS
Add the following headers:
```bash
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Origin":add(header-name="Access-Control-Allow-Origin",header-value="*")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Methods":add(header-name="Access-Control-Allow-Methods",header-value="GET, POST, OPTIONS, HEAD, PUT, PATCH, DELETE")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Headers":add(header-name="Access-Control-Allow-Headers",header-value="accept, authorization, content-type, x-requested-with")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Expose-Headers":add(header-name="Access-Control-Expose-Headers",header-value="strict-transport-security, content-security-policy, content-type, content-encoding, date, location, last-modified, etag")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Credentials":add(header-name="Access-Control-Allow-Credentials",header-value="true")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Max-Age":add(header-name="Access-Control-Max-Age",header-value="1")
```
#### 4. Add CORS Filters to Virtual Hosts
Include CORS filters for each host:

#### API Host
```bash
/subsystem=undertow/server=default-server/host=api-host/filter-ref="Access-Control-Allow-Origin":add(predicate="regex(pattern='^(https:\/\/(?:.+\.)?yourdomain.me(?::\d{1,5})?)(\/.*\/?)?$',value=%{i,Origin},full-match=true)")
/subsystem=undertow/server=default-server/host=api-host/filter-ref="Access-Control-Allow-Methods":add(predicate="regex(pattern='^(https:\/\/(?:.+\.)?yourdomain.me(?::\d{1,5})?)(\/.*\/?)?$',value=%{i,Origin},full-match=true)")
/subsystem=undertow/server=default-server/host=api-host/filter-ref="Access-Control-Allow-Headers":add(predicate="regex(pattern='^(https:\/\/(?:.+\.)?yourdomain.me(?::\d{1,5})?)(\/.*\/?)?$',value=%{i,Origin},full-match=true)")
/subsystem=undertow/server=default-server/host=api-host/filter-ref="Access-Control-Expose-Headers":add(predicate="regex(pattern='^(https:\/\/(?:.+\.)?yourdomain.me(?::\d{1,5})?)(\/.*\/?)?$',value=%{i,Origin},full-match=true)")
/subsystem=undertow/server=default-server/host=api-host/filter-ref="Access-Control-Allow-Credentials":add(predicate="regex(pattern='^(https:\/\/(?:.+\.)?yourdomain.me(?::\d{1,5})?)(\/.*\/?)?$',value=%{i,Origin},full-match=true)")
/subsystem=undertow/server=default-server/host=api-host/filter-ref="Access-Control-Max-Age":add(predicate="regex(pattern='^(https:\/\/(?:.+\.)?yourdomain.me(?::\d{1,5})?)(\/.*\/?)?$',value=%{i,Origin},full-match=true)")
```

#### IAM Host
Repeat the above commands, replacing api-host with iam-host.

---


#### 4. Set Frontend Path
Point the virtual host to your frontend:
```bash
subsystem=undertow/configuration=handler/file=welcome-content:write-attribute(name=path,value="<PATH_TO_WWW>/yourfront")
```

#### 5. Configure MongoDB and MQTT
Place the following configuration in `META-INF/resources`:

```properties
# MongoDB database
jnosql.document.database=CoT_Project
jnosql.mongodb.host=localhost:27017
jnosql.document.provider=org.eclipse.jnosql.databases.mongodb.communication.MongoDBDocumentConfiguration

# Key Pair Configuration
key.pair.lifetime.duration=10800
key.pair.cache.size=3

# JWT Configuration
jwt.lifetime.duration=1020
jwt.issuer=urn:smarthomecot:iam
jwt.audiences=urn:smarthomecot.lme:api,urn:smarthomecot.lme:erp
jwt.claim.roles=groups
mp.jwt.realm=urn:smarthomecot.lme:iam

# Argon2 Configuration
argon2.saltLength=32
argon2.hashLength=128
argon2.iterations=23
argon2.memory=97579
argon2.threads=2

# MQTT Configuration
mqtt.uri=ssl://3f8df....................eu.hivemq.cloud:8883
mqtt.username=***********
mqtt.password=**********

# Email Service Configuration
smtp.host=smtp.gmail.com
smtp.port=587
smtp.username=***********
smtp.password=**********
smtp.starttls.enable=true

# Roles
roles=Client,Admin
```

#### 5. Launch the Application
- Deploy the artifacts (`api-1.0.war` and `iam-1.0.war`) to WildFly.
- Start the Raspberry Pi system to manage IoT devices.

#### 6. Access the Dashboard
- Open the PWA in your browser to monitor and control the system.

### 🖥️ Frontend
#### Welcome Page
![Welcome Page Screenshot](/images/home.png)

#### Sign In / Sign Up
![Sign In Screenshot](/images/login.png)  
![Sign Up Screenshot](/images/signup.png)

#### Dashboard
![Dashboard Screenshot](/images/dashboard.png)

#### 🧩 Prototype
##### Features
- 🚀 **Real-time Monitoring**: Track and manage your home in real-time.
- 📷 **Computer Vision**: Secure your home using ESP32CAM and YOLO for AI-based detection.
- 💧 **Water Management**: Monitor water usage efficiently with flow sensors.
- 🌡️ **Environmental Sensors**: DHT22 provides insights on temperature and humidity.
- 🔒 **Security Alerts**: Proximity sensors detect unauthorized activities.

![Dashboard Screenshot](/images/pipeline.jpg)
![Dashboard Screenshot](/images/pipeline2.jpg)

## 🔒 SSL Certification
Smarthome ensures secure communication with SSL certification. Below is a screenshot verifying the SSL certification:
![Dashboard Screenshot](/images/SSL.png)
## ⚙️ Future Enhancements
- Add voice control for home automation.
- Implement predictive analytics for resource optimization.
- Expand the system to include additional IoT devices.


## **🧑‍💻 Project By**

<a href="https://github.com/AnasBenAmor10/Smart_House_Monitoring_System/graphs/contributors">
    <img src="https://contrib.rocks/image?repo=AnasBenAmor10/Smart_House_Monitoring_System" />
</a>

---










