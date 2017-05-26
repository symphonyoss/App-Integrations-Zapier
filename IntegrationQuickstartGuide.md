This is the Integration quickstart guide...work to be continued: 

The quickstart guide covers: 

* Webhook Integration architecture & > IBridge architecture
* Development environment
* Application bootstraping and building
* How to run one specific integration, using Intellij or via Maven/CLI,
* How to build a new parser, and validate it using PostMan
* How to inspect incoming payloads
* How to use the healthcheck
* How the YAML file works
* How to build a new Configurator APP. 
* Overview about MessageML v2

# Symphony Integration framework
An integration is a collection of several items that are linked together:
  * Service account
  * Parser
  * Configurator app
  * Certificate 

Integrations are configured by end-users in the configurator app. When the end-user configures the integration, by choosing where they want to receive notifications from, a webhook URL is generated. This URL is then placed into a 3rd-party system or in-house system, so that said system can emit a payload to the webhook. 

The integration is listening to that webhook. When the integration receives a payload from the emitting system, it then parses that payload. 

The payload is transformed from the original data structure, into a new data structure - messageML & EntityJSON.

MessageML determines what the message will look like and render within Symphony, and serves as a mapping for the data to render correctly.

EntityJSON is the data structure that the incoming message is transformed into. 

# Build instructions for the Java developer

## What you’ll build
You’ll build an integration module to be used with the [Integration Bridge](https://github.com/symphonyoss/App-Integrations-Core).

If you develop a new integration, to get it up and running you'll also need to add it to the core project's web pom file.

## What you’ll need
* JDK 1.8
* Maven 3.0.5+
* Node 6.10
* Gulp (globally installed)
* Webpack (globally installed)

## Build with maven
Zapier WebHook Integration is compatible with Apache Maven 3.0.5 or above. If you don’t already have Maven installed you can follow the instructions at maven.apache.org.

To start from scratch, do the following:

1. Build the _App-Integrations-Zapier_ dependencies in this order (so you have them in your Maven local repository):
> 1. [_App-Integrations-Commons_](https://github.com/symphonyoss/App-Integrations-Commons)
> 2. [_App-Integrations-Universal_](https://github.com/symphonyoss/App-Integrations-Universal)
> 3. [_App-Integrations-Zapier_](https://github.com/symphonyoss/App-Integrations-Zapier) *note: if you are building another integration, simply substitute Zapier for your integration, such as 
> 4. [_App-Integrations-Github_](https://github.com/symphonyoss/App-Integrations-Github)

2. Clone the source repository using Git: `git clone git@github.com:symphonyoss/App-Integrations-Zapier.git`
3. cd into _App-Integrations-Zapier_  (or your equivalent folder)
4. Build using maven: `mvn clean install`

## Run locally

1. Define your certificate paths and passwords
```
cp local-run/env.sh.sample env.sh
open env.sh
```

Make sure that
- Paths and passwords are correct
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

For the application context, you should always user app/<your app id> provided in the env.sh. That id should also match what you have on [application-zapier.yml](src/main/resources/application-zapier.yml)

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

Access the application icon on your browser to make sure it works and to accept any unsafe certificates (if necessary). In the above example, the URL to acces is https://6f3420e6.ngrok.io/img/appstore-logo.png).

**Run your application again as indicated above, to get the new bundle.js information packaged.**

Launch the Symphony client on your browser, adding your bundle.js as path of the query parameters in the URL. For instance, using the Foundation Dev Pod with the above ngrok sample URL: https://foundation-dev.symphony.com?bundle=https://d74a790c.ngrok.io/apps/zapier/bundle.json.

Access the Symphony Market on the browser, and you should be notified to allow unauthorized apps. That is your development app added through bundle.json. Accept the notification and you should see your application in the application list, with the name and description provided in the bundle.json.
