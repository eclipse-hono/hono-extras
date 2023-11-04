<a href="https://eclipse.org/hono/">
  <img src="https://www.eclipse.org/hono/img/HONO-Logo_Bild-Wort_quer-s-310x120px.svg" alt="Hono Logo" width="100%" height="180">
</a>

# Eclipse Hono Extras

Eclipse Hono Extras provides additional resources for [Eclipse Hono&trade;](https://www.eclipse.org/hono).

## Hono Protocol Gateways

See the [protocol-gateway](protocol-gateway) directory for a template and an example implementation for
a [Hono protocol gateway](https://www.eclipse.org/hono/docs/concepts/connecting-devices/#connecting-via-a-protocol-gateway).

## Device registry migration

See the [device-registry-migration](device-registry-migration) directory for example migration scripts from one device
registry type to another.

## Device communication API

See the [device-communication](device-communication) directory for api specifications and implementation.

## Device Management User Interface

See the [device-management-ui](device-management-ui) directory for an example User Interface containing a list
of tenants with the option to create, update and delete a tenant. Each tenant contains a list of its devices with the
option to create and delete a device. Furthermore, the credentials of a device can be listed, created and deleted.
Currently only [Password Credentials](https://www.eclipse.org/hono/docs/concepts/device-identity/#usernamepassword-based-authentication)
and [RPK Credentials](https://www.eclipse.org/hono/docs/concepts/device-identity/#json-web-token-based-authentication)
are supported. This UI also contains the functionality to send a configuration or command through the [Device Communication API](device-communication).
