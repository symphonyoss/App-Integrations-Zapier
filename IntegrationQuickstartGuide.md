# Integration Quickstart Guide

This document aims to allow developers to create their first integration using the Integration Bridge.

The quickstart guide covers: 

* Integration Bridge architecture
* Webhook Integration architecture
* Build instructions for the Java developer, using Intellij or Maven
* Application healthcheck
* YAML configuration file
* How to build a new integration, and validate it using PostMan
* How to build a new Configurator APP
* How to inspect incoming payloads
* Overview about MessageML v2

## Integration Bridge architecture
TODO

## Webhook Integration architecture
TODO

## Build instructions for the Java developer

### What you’ll build
You’ll build an integration module to be used with the [Integration Bridge](https://github.com/symphonyoss/App-Integrations-Core).

### What you’ll need
* JDK 1.8
* Maven 3.0.5+
* Node 6.10
* Gulp (globally installed)
* Webpack (globally installed)

### Build with maven
Integration Bridge is compatible with Apache Maven 3.0.5 or above. If you don’t already have Maven installed you can follow the instructions at [Maven Website](https://maven.apache.org).

To start from scratch, do the following:

1. Clone the source repository using Git: `git clone git@github.com:symphonyoss/App-Integrations-Zapier.git`
2. cd into App-Integrations-Zapier
3. Build using maven: `mvn clean install`

### Run locally

1. Create the 'certs' directory and put your application certificate over there
```
mkdir certs
cp $CERT_PATH/zapier.p12 certs/zapier.p12
```

2. Define your certificate file, password, and API endpoints in the environment shell script.
```
cp local-run/env.sh.sample env.sh
open env.sh
```

Make sure that
- Certificate filename and password are correct
- You can reach all Symphony Pod endpoints
- Service accounts exists and cert CNs match with account's usernames. **Note: The team is working on a integration-provisioning module that will automate this process; until further notice, please contact Symphony Support to get your Symphony integration deployed on your pod, as the pod will need an exact match of service account name, certs and app name in the pod for your app to be visible in your pod and usable. You will need to provide the (?)**
- `./env.sh`, `./application.yaml` and `./certs/` are ignored by Git and don't end up in any code repository

2. Run the integrations
```
./run.sh
```

This command will create an `application.yaml` file in the project root folder, using `local-run/application.yaml.template` as template.

## Expose local endpoint to a public host

In order to be able to create the app in the Foundation pod, you must provide a public `App Url`; you can use [ngrok](https://ngrok.com/) (or similar) to tunnel your local connection and expose it via a public DNS:
```
ngrok http 8080
```
Your local port 8080 is now accessible via `<dynamic_id>.ngrok.io`

If you have a paid subscription, you can also use
```
ngrok http -subdomain=my.static.subdomain 8080
```

## Add your locally running application to the Symphony Market

Adjust your [bundle.json](src/main/webapp/bundle.json) located src/main/webapp/ with the URL you are exposing via ngrok, the configuration and bot id's, and the application context.

**_Note: The team is working on a integration-provisioning module that will automate this process; until further notice, please contact Symphony Support to get your configuration and bot id's.

For the application context, it should match what you have on [application-zapier.yml](src/main/resources/application-zapier.yml)

For instance, see apps/zapier present in the URL's for the controller.html and appstore-logo.png, as well as in the **context** query parameter for the controller:

```
{
  "applications": [
    {
      "type": "sandbox",
      "id": "devZapierWebHookIntegration",
      "name": "Zapier",
      "blurb": "Zapier Webhook Integration in Development Mode",
      "publisher": "Symphony",
      "url": "https://d74a790c.ngrok.io/apps/zapier/controller.html?configurationId=58598bf8e4b057438e69f517&botUserId=346621040656485&id=devZapierWebHookIntegration&context=apps/zapier",
      "icon": "https://d74a790c.ngrok.io/apps/zapier/img/appstore-logo.png",
      "domain": ".ngrok.io"
    }
  ]
}
```

Access the application icon on your browser to make sure it works and to accept any unsafe certificates (if necessary). In the above example, the URL to acces is https://d74a790c.ngrok.io/img/appstore-logo.png).

**Run your application again as indicated above, to get the new bundle.js information packaged.**

Launch the Symphony client on your browser, adding your bundle.js as path of the query parameters in the URL. For instance, using the Foundation Dev Pod with the above ngrok sample URL: https://foundation-dev.symphony.com?bundle=https://d74a790c.ngrok.io/apps/zapier/bundle.json.

Access the Symphony Market on the browser, and you should be notified to allow unauthorized apps. That is your development app added through bundle.json. Accept the notification and you should see your application in the application list, with the name and description provided in the bundle.json.

### Running with Intellij
TODO

## Application healthcheck
To validate if your application were bootstrapped successfully you can reach on the application health check using
the URL http://localhost:8080/integration/health

The application health will indicate the connectivity and compatibility for each service you want to communicate like POD, Key Manager, and Agent. You also could check the application health, the configurator URL's, application flags, and latest posted message timestamp.

This is the result from healthcheck:

```
{
  "status": "UP",
  "message": "Success",
  "version": "0.13.0-SNAPSHOT",
  "services": {
    "Agent": {
      "connectivity": "UP",
      "currentVersion": "1.46.0",
      "minVersion": "1.44.0",
      "compatibility": "OK"
    },
    "POD": {
      "connectivity": "UP",
      "currentVersion": "1.46.0",
      "minVersion": "1.44.0",
      "compatibility": "OK"
    },
    "Key Manager": {
      "connectivity": "UP",
      "currentVersion": "1.46.0",
      "minVersion": "1.44.0",
      "compatibility": "OK"
    }
  },
  "applications": [
    {
      "name": "zapier",
      "version": "0.13.0-SNAPSHOT",
      "status": "ACTIVE",
      "message": "Success",
      "configurator": {
        "loadUrl": "https://d74a790c.ngrok.io/apps/zapier/controller.html",
        "iconUrl": "https://d74a790c.ngrok.io/apps/zapier/img/appstore-logo.png"
      },
      "flags": {
        "parser_installed": "OK",
        "configurator_installed": "OK",
        "certificate_installed": "OK",
        "user_authenticated": "OK"
      },
      "latestPostTimestamp": "2017-05-29T16:30:44Z-0300"
    }
  ]
}
```

## YAML Configuration File
TODO

## Build a new integration
TODO

## Build the Configurator APP
TODO

## Inspect incoming payloads
TODO

## MessageML v2
TODO