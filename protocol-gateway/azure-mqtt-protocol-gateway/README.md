# "Azure IoT Hub" Protocol Gateway

This Protocol Gateway shows how to use Hono's Protocol Gateway Template to implement a production-ready protocol gateway. 
The MQTT-API of "Azure IoT Hub" serves as a working example. Parts of its API are mapped to Hono's communication patterns.

Full compatibility with the Azure IoT Hub is not a design goal of this example. It is supposed to behave similarly for 
the "happy path", but cannot treat all errors or misuse in the same way as the former.

Supported are the following types of messages:

## Mapping of Azure IoT Hub messages to Hono messages

**Device-to-cloud communication**

| Azure IoT Hub message | Hono message | Limitations |
|---|---|---|
| [Device-to-cloud](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-devguide-d2c-guidance) with QoS 0 (*AT MOST ONCE*) | [Telemetry](https://www.eclipse.org/hono/docs/api/telemetry/#forward-telemetry-data) | Messages are not brokered | 
| [Device-to-cloud](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-devguide-d2c-guidance) with QoS 1 (*AT LEAST ONCE*) | [Event](https://www.eclipse.org/hono/docs/api/event/#forward-event) | Messages are not brokered | 


**Cloud-to-device communication**

| Hono message | Azure IoT Hub message | Limitations |
|---|---|---|
| [One-way Command](https://www.eclipse.org/hono/docs/api/command-and-control/#send-a-one-way-command) | [Cloud-to-device](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-devguide-messages-c2d) | Messages are not brokered (ignores CleanSession flag) | 
| [Request/Response Command](https://www.eclipse.org/hono/docs/api/command-and-control/#send-a-request-response-command) | [Direct method](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-devguide-direct-methods) | |

## Limitations

Not supported are the following features of Azure IoT Hub:
 
 * "device twins"
 * file uploads 
 * message brokering 
 * "jobs"
 * the back-end application API 
 * device authentication with client certificates

## Device Authentication

A Hono protocol gateway is responsible for the authentication of the devices.
This example implementation does not provide or require data storage for device credentials. 
Instead, it can only be configured to use a single demo device, which must already be present in Hono's device registry (see below).
Client certificate based authentication is not implemented.

Since there is only one device in this example implementation anyway, the credentials for the tenant's gateway client are not looked up dynamically, but are taken from the configuration.
 

## Prerequisites

### Registering Devices

The demo device and the gateway need to be registered in Hono's device registry. For the gateway, credentials must be created. 
The [Getting started](https://www.eclipse.org/hono/docs/getting-started/#registering-devices) guide shows how to do this.

Alternatively, the script `scripts/create_demo_devices.sh` can be used to register the devices and create credentials:
~~~sh
# in directory: protocol-gateway/azure-mqtt-protocol-gateway/scripts/
bash create_demo_devices.sh
~~~

After completion the script prints the configuration properties. Copy the output into the 
file `protocol-gateway/azure-mqtt-protocol-gateway/src/main/resources/application.properties`. 


### Configuration

The protocol gateway needs the configuration of:

1. the AMQP adapter of a running Hono instance to connect to
2. the MQTT server 
3. the demo device to use.

By default, the gateway will connect to the AMQP adapter of the [Hono Sandbox](https://www.eclipse.org/hono/sandbox/).
However, it can also be configured to connect to a local instance.
The default configuration can be found in the file `protocol-gateway/azure-mqtt-protocol-gateway/src/main/resources/application.properties` 
and can be customized using [Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config). 

If connecting to a local Hono instance deployed via the [IoT Packages](https://www.eclipse.org/packages/) Hono Helm chart,
the `hono.client.amqp.host` configuration property has to be set to the IP of the AMQP adapter service (obtainable e.g.
via `kubectl get service eclipse-hono-adapter-amqp-vertx --output=jsonpath="{.status.loadBalancer.ingress[0]['hostname','ip']}" -n hono`).
If the example certificates provided with the chart are used, `hono.client.amqp.trustStorePath` has to set to the CA
certificates file (i.e. [trusted-certs.pem](https://github.com/eclipse/packages/blob/master/charts/hono/example/certs/trusted-certs.pem))
and `hono.client.amqp.hostnameVerificationRequired` has to be set to `false`.
 
 
### Starting a Receiver

Telemetry and event messages need an application that consumes the messages. 
The [Getting started](https://www.eclipse.org/hono/docs/getting-started/#starting-the-example-application) guide shows how to start the example application that receives the messages.
 
 
## Starting the Protocol Gateway

Build the template project:
~~~sh
# in directory: protocol-gateway/mqtt-protocol-gateway-template/
mvn clean install
~~~

and start the protocol gateway:
~~~sh
# in directory: protocol-gateway/azure-mqtt-protocol-gateway/
mvn spring-boot:run
~~~

 
## Enable TLS 

Azure IoT Hub only provides connections with TLS and only offers port 8883. To start the protocol gateway listening
on this port with TLS enabled, first adapt the `src/main/resources/application-ssl.properties` configuration file and
enter certificate and private key file paths in `hono.server.mqtt.certPath` and `hono.server.mqtt.keyPath`.
Then run:

~~~sh
# in directory: protocol-gateway/azure-mqtt-protocol-gateway/
mvn spring-boot:run -Dspring-boot.run.profiles=ssl
~~~
**NB** Do not forget to build the template project before, as shown above.

With the [Eclipse Mosquitto](https://mosquitto.org/) command line client, for example, sending an event message would
then look like this, with the `--cafile` parameter adapted to reference the CA certificates file corresponding to the
used server certificate:

~~~sh
# in directory: protocol-gateway/azure-mqtt-protocol-gateway
mosquitto_pub -d -h localhost -p 8883 -i '4712' -u 'demo1' -P 'demo-secret' -t "devices/4712/messages/events/" -m "hello world" -V mqttv311 --cafile <path>/trusted-certs.pem
~~~

Existing hardware devices might need to be configured to accept the used certificate. 

## Example Requests

With the [Eclipse Mosquitto](https://mosquitto.org/) command line client the requests look like the following.
 
**Telemetry**
 
~~~sh
mosquitto_pub -d -h localhost -i '4712' -u 'demo1' -P 'demo-secret' -t 'devices/4712/messages/events/?foo%20bar=b%5Fa%5Fz' -m "hello world" -V mqttv311 -q 0
~~~
 
**Events**
 
~~~sh
mosquitto_pub -d -h localhost -i '4712' -u 'demo1' -P 'demo-secret' -t 'devices/4712/messages/events/?foo%20bar=b%5Fa%5Fz' -m '{"alarm": 1}' -V mqttv311 -q 1
~~~
 
### Commands 

The example application can be used to send commands. 
The [Getting started](https://www.eclipse.org/hono/docs/getting-started/#advanced-sending-commands-to-a-device) shows a walk-through example.

**Subscribe for one-way commands**
 
~~~sh
mosquitto_sub -v -h localhost -u "demo1" -P "demo-secret" -t 'devices/4712/messages/devicebound/#' -q 1
~~~
 
**Subscribe for request-response commands**
 
~~~sh
mosquitto_sub -v -h localhost -u "demo1" -P "demo-secret" -t '$iothub/methods/POST/#' -q 1
~~~

When Mosquitto receives the command, the output in the terminal should look like this: 
~~~sh
$iothub/methods/POST/setBrightness/?$rid=0100bba05d61-7027-4131-9a9d-30238b9ec9bb {"brightness": 87}
~~~

**Respond to a command**
 
When sending a response, the request id must be added. The ID after `rid=` can be copied from the received message 
and pasted into a new terminal to publish the response like this:
~~~sh
export RID=0100bba05d61-7027-4131-9a9d-30238b9ec9bb
mosquitto_pub -d -h localhost -u 'demo1' -P 'demo-secret' -t "\$iothub/methods/res/200/?\$rid=$RID" -m '{"success": true}' -q 1
~~~
Note that the actual identifier from the command must be used.
