# Smart_House_Monitoring_System

# Configuring Virtual Hosts in WildFly

This document explains the process of setting up virtual hosts in WildFly using the `jboss-cli` command-line interface and configuring corresponding artifacts. Additionally, it covers enabling CORS (Cross-Origin Resource Sharing) for all domains.

## Prerequisites
- WildFly server installed and running.
- Administrative access to the WildFly server.
- CLI tool (`jboss-cli.sh` or `jboss-cli.bat`) available.

---

## Step 1: Configure Virtual Hosts

### Add Virtual Hosts
Execute the following commands in the WildFly CLI:

#### API Host Configuration
```bash
/subsystem=undertow/server=default-server/host=api-host:add(alias=["api.yourdomain.me"],default-web-module="api-1.0.war")
/subsystem=undertow/server=default-server/host=api-host/setting=access-log:add
/subsystem=undertow/server=default-server/host=api-host/setting=access-log:write-attribute(name=pattern,value="combined")
/subsystem=undertow/server=default-server/host=api-host/setting=access-log:write-attribute(name=prefix,value="api-yourapp")
/subsystem=undertow/server=default-server/host=api-host/filter-ref=hsts:add(predicate="equals(%p,8443)")
/subsystem=undertow/server=default-server/host=api-host/filter-ref=http-to-https:add(predicate="equals(%p,8080)")
```

#### IAM Host Configuration
```bash
/subsystem=undertow/server=default-server/host=iam-host:add(alias=["iam.yourdomain.me"],default-web-module="iam-1.0.war")
/subsystem=undertow/server=default-server/host=iam-host/setting=access-log:add
/subsystem=undertow/server=default-server/host=iam-host/setting=access-log:write-attribute(name=pattern,value="combined")
/subsystem=undertow/server=default-server/host=iam-host/setting=access-log:write-attribute(name=prefix,value="iam-yourapp")
/subsystem=undertow/server=default-server/host=iam-host/filter-ref=hsts:add(predicate="equals(%p,8443)")
/subsystem=undertow/server=default-server/host=iam-host/filter-ref=http-to-https:add(predicate="equals(%p,8080)")
```

---

## Step 2: Configure `jboss-web.xml`

Include the virtual host name in the `jboss-web.xml` file of each artifact. Example for `iam-1.0.war`:

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

---

## Step 3: Enable CORS for All Domains

### Add CORS Headers
Run the following commands to enable CORS headers:

```bash
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Origin":add(header-name="Access-Control-Allow-Origin",header-value="*")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Methods":add(header-name="Access-Control-Allow-Methods",header-value="GET, POST, OPTIONS, HEAD, PUT, PATCH, DELETE")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Headers":add(header-name="Access-Control-Allow-Headers",header-value="accept, authorization, content-type, x-requested-with")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Expose-Headers":add(header-name="Access-Control-Expose-Headers",header-value="strict-transport-security, content-security-policy, content-type, content-encoding, date, location, last-modified, etag")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Allow-Credentials":add(header-name="Access-Control-Allow-Credentials",header-value="true")
/subsystem=undertow/configuration=filter/response-header="Access-Control-Max-Age":add(header-name="Access-Control-Max-Age",header-value="1")
```

### Add CORS Filters to Virtual Hosts
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
Repeat the above commands, replacing `api-host` with `iam-host`.

---

## Conclusion
Follow these steps to successfully configure virtual hosts in WildFly and enable CORS for all domains. Ensure that all configurations are thoroughly tested to verify functionality.

