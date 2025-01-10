# Smart House Monitoring System

## ✨ Project Overview
In today’s interconnected world, the concept of a “smart house” has gained significant traction. This project introduces a comprehensive IoT-based smart house monitoring system powered by a Raspberry Pi. The system is designed to oversee key household elements such as lighting, water usage, and security. It integrates sensors, alarms, and computer vision to create a connected and intelligent home environment.

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

## 🚀 Technologies
### Backend
- **MQTT:** Lightweight messaging protocol for IoT components.
- **MongoDB:** NoSQL database for data storage.
- **HiveMQ:** MQTT broker optimized for IoT communication.

### Middleware
- **Jakarta EE:** Java-based framework for secure API management.
- **WildFly:** Java application server for hosting middleware.

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

#### 5. Launch the Application
- Deploy the artifacts (`api-1.0.war` and `iam-1.0.war`) to WildFly.
- Start the Raspberry Pi system to manage IoT devices.

#### 6. Access the Dashboard
- Open the PWA in your browser to monitor and control the system.

## ⚙️ Future Enhancements
- Add voice control for home automation.
- Implement predictive analytics for resource optimization.
- Expand the system to include additional IoT devices.


