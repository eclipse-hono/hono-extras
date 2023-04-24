# Device Management UI

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 15.2.0. This is an example
implementation for a Google based deployment in order to give users the possibility to operate with the [Device Registry
API](https://www.eclipse.org/hono/docs/api/management/) as well as with the [Device Communication API](../device-communication).
We are recommending using Identity-Aware Proxy ([IAP](https://cloud.google.com/iap/docs)) for your API security. Make
sure to configure the [OAuth consent](https://developers.google.com/workspace/guides/configure-oauth-consent) screen.
This UI will provide the possibility to:

1. Access the Device Registry API containing actions like:
   - listing tenants
   - creating a new tenant with an ID and messaging-type
   - deleting a tenant
   - updating a tenant with another messaging-type
   - listing devices of a tenant
   - creating a new device with an ID
   - deleting a device
   - listing credentials of a device
   - creating [Username/Password](https://www.eclipse.org/hono/docs/concepts/device-identity/#usernamepassword-based-authentication) or [JSON Web Token](https://www.eclipse.org/hono/docs/concepts/device-identity/#json-web-token-based-authentication) based credentials
   - updating JSON Web Token based credentials
   - deleting credentials


2. Access the Device Communication API containing actions like:
   - listing configs
   - updating a config
   - listing states
   - sending a command

**_NOTE:_** This UI cannot be run without further adjustments! If one wants to use this UI in other environments than on
Google Cloud, adjustments have to be made
to **not** include the GoogleService and to update the url suffixes of the services.

### Development server

The development server uses the Proxy Configuration file [proxy.config.json](proxy.config.json). So the target address
must be updated
with the address the Hono API is hosted. To run the dev server `ng serve` must be executed. The UI can be then accessed
via `http://localhost:4200/`.
The application will automatically reload if any of the source files is changed.

#### Google APIs

If the Device Registry API and/or the Device Communication API is hosted in GCP with Identity Aware Proxy,
the Google Client ID of the enabled IAP must be provided in the environment
file [environment.development.ts](../device-management-ui/src/environments/environment.development.ts).

### Build

#### Google APIs

If the UI, the Device Registry API and/or the Device Communication API is hosted in GCP with Identity Aware Proxy,
the Google Client ID of the enabled IAP must be provided inside an environment variable `ENV_GOOGLE_CLIENT_ID`.
This environment variable is then set as `googleClientId` inside the environment
file [environment.ts](../device-management-ui/src/environments/environment.ts).

#### Docker Image

A docker image can be built by executing: `docker build -t {REGISTRY}/{IMAGE_NAME}:{TAG} .`.
The docker image is installing all the dependencies and building the project with ng build automatically.
Also, a nginx based image is used for building the server.

### Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).
