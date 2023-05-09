# Device Communication API

Device communication API enables users and applications to send configurations and commands to devices via HTTP(S)
endpoints.

![img.png](img.png)

### Application

The application is reactive and uses Quarkus Framework for the application and Vertx tools for the HTTP server.

### Hono internal communication

API uses [Google's PubSub](https://cloud.google.com/pubsub/docs/overview?hl=de) service to communicate with the command
router.

## API endpoints

#### commands/{tenantId}/{deviceId}

- POST : post a command for a specific device (NOT IMPLEMENTED YET)

<p>

#### states/{tenantId}/{deviceId}?numStates=(int 0 - 10)

- GET : list of device states

#### configs/{tenantId}/{deviceId}?numVersion=(int 0 - 10)

- GET : list of device config versions

- POST: create a device config version

For more information please see resources/api/openApi file.

## Pub/Sub - Internal Messaging

API communicates with hono components via the internal messaging interface (implemented from Google's PubSub).
All the settings for the InternalMessaging component are in the application.yaml file. By publish/subscribe to a topic
application sends or expects some message attributes.

### Events

API will subscribe to all tenants' event topic at startup.

Expected message Attributes:

- deviceId
- tenantId
- content-type

Application will <b>proceed only empty Notifications events (content-type is
application/vnd.eclipse-hono-empty-notification)</b>.

### States

API will subscribe to all tenants' state topic at startup.

Expected message Attributes:

- deviceId
- tenantId

States are read only.

### Configs

Application will publish the latest device configuration when:

- an empty Notifications event was received
- a new device config was created

Message will be published with the following attributes:

- deviceId
- tenantId
- config-version

The Body will be a JSON object with the device config object.

After publishing device configs, application subscribes to config_response topic and waits for the device to ack the
configs.

### Config ACK

Expected message attributes:

- deviceId
- tenantId
- configVersion (the config version received from device)

If configVersion is not set, application will ack always the latest config.

### Commands

A command will be published from API to the command topic.

Attributes:

- deviceId
- tenantId
- subject (always set to "command")

Body:

The command as string.

## Database

Application uses PostgresSQL database. All the database configurations can be found in application.yaml file.

### Tables

- device_configs <br>
  Is used for saving device config versions
- device_registrations <br>
  Is used for validating if a device exist
- device_status <br>
  Is used for saving device states

### Migrations

When Applications starts tables will be created by the DatabaseSchemaCreator service.

### Running postgresSQL container local

For running the PostgresSQL Database local with docker run:

``````

docker run -p 5432:5432 --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -d postgres

``````

After the container is running, log in to the container and with psql create the database. Then we have
to set the application settings.

Default postgresSQl values:

- userName = postgres
- password = mysecretpassword

## Build and Push API Docker Image

Mavens auto build and push functionality ca be enabled from application.yaml settings:

````

quarkus:
  container-image:
  builder: docker
  build: true
  push: true
  image: "gcr.io/sotec-iot-core-dev/hono-device-communication"

````

By running maven package, install or deploy will automatically build the docker image and if push is enabled it will
push the image
to the given registry.

## OpenApi Contract-first

For creating the endpoints, Vertx takes the openApi definition file and maps every endpoint operation-ID with a specific
Handler
function.

## Handlers

Handlers are providing callBack functions for every endpoint. Functions are going to be called automatically from vertx
server every time a request is received.

## Adding a new Endpoint

Adding new Endpoint steps:

1. Add Endpoint in openApi file and set an operationId
2. Use an existing const Class or create a new one under /config and set the operation id name
3. Implement an HttpEndpointHandler and set the Routes

## PubSub Events

Application subscribes and uses to the following topics:

1. TENANT_ID.command
2. TENANT_ID.command_response
3. TENANT_ID.config
4. TENANT_ID.config_response
5. TENANT_ID.event
6. TENANT_ID.event.state
7. registry-tenant.notification

## Automatically create PubSub topics and subscriptions

Application creates all tenants topics and subscriptions when:

1. Application starts if are not exist
2. New tenant is created



