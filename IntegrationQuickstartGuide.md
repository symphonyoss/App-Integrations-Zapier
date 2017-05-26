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

## General Integration Workflow
As of providing the mentioned structure above, we'll detail here what is the general workflow when the core receives a message from an integrated app, let's say GitHub, for this example:

> 1. Expose an endpoint where it will receive the message.
> 2. Identify where this message is coming from through the URL parameters it received (configurationId and instanceId)
> 3. If the message is trying to reach a valid integration and a configured instance, it will delegate the message to the specific integration code implemented separately across the other Integration repositories.
> 4. The integration GitHub logic will now determine which event it is dealing with through the received message header parameter, and based on this will determine which [parser](#parsers) it must use to treat the message properly.
> 5. The parser will then convert the message to a [Message ML format](#the-message-ml-format), extracting the needed information from the payload received.
> 6. The parsed message will return to the Integration Core and post the message to the Symphony platform

### Parsers
Integrations will most of the times need a parser to work properly.
Those special classes will need to deal with the content coming from the related application, parsing this data into a format readable by the Symphony platform.

This format is called Symphony Message ML and it may contain a set of tags. More details below.

### The Message ML format
A Message ML is a Symphony XML format that defines XML elements and attributes necessary to compose a message that can be posted within a chat room.
The most basic message one can send may be as simple as ``<messageML>simple message</messageML>`` or as detailed as it can get. What determines this is what system we are integrating with.

These elements and attributes will be briefly detailed in the next topics as reference. The specific integration formats can be found in their separate repositories "Readme" files.

## Entity (MessageMLv1.0)
_**note: MessageMLv1.0 has been superseded by MessageMLv2.0. MessageMLv1.0 is still supported, however MessageMLv2.0 will allow you to create a rich render in a more seamlessly manner, with less steps and no front end code. Please see a JIRA ticket rendered using messageMLv2.0 here for as a complex example: https://symphonyoss.atlassian.net/wiki/display/WGFOS/Single+Jira+Ticket+-+Templated+PresentationML & see [Zapier](https://github.com/symphonyoss/App-Integrations-Zapier/tree/db-hackathon) as a simple example of a richly rendererd message._**

An entity is a special element contained in a ``<messageML>``, it may also be nested within other entities as another element, and so on.

Entities must have a "type" and a "version", and may also have a "name" for itself, all of those as XML attributes.

The first entity in a messageML MUST have an element called "presentationML".

The ``<presentationML>`` is a special element that must hold all content that would be otherwise drawn on Symphony by other elements, represented as a single string on its content.
This particular text must follow the rules presented [here](https://rest-api.symphony.com/docs/message-format/).

It is important that it contains matching information as it is used for visualising a message when a specific renderer is not present, on Symphony mobile apps or content export.

Entities may also have ``<attribute>``s as their XML elements, which in turn must have a "name", a "type" and a "value" as attributes.

Here's an example of a valid MessageML, containing all of the mentioned above:

```xml
<messageML>
    <entity type="sample.event.core" version="1.0">
        <presentationML>test message for:<br/>application core</presentationML>
        <attribute name="message" type="org.symphonyoss.string" value="test message"/>
        <entity name="application" type="sample.application">
            <attribute name="appName" type="org.symphonyoss.string" value="core"/>
        </entity>
    </entity>
</messageML>
```

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
Integration Core is compatible with Apache Maven 3.0.5 or above. If you don’t already have Maven installed you can follow the instructions at maven.apache.org.

To start from scratch, do the following:

1. Build the _App-Integrations-Zapier dependencies, on this order (so you have them in your Maven local repository):

> 1. [_App-Integrations-Commons_](https://github.com/symphonyoss/App-Integrations-Commons)
> 2. [_App-Integrations-Universal_](https://github.com/symphonyoss/App-Integrations-Universal)
> 3. [_App-Integrations-Github_](https://github.com/symphonyoss/App-Integrations-Github)
> 4. [_App-Integrations-Jira_](https://github.com/symphonyoss/App-Integrations-Jira)
> 5. [_App-Integrations-Salesforce_](https://github.com/symphonyoss/App-Integrations-Salesforce)
> 6. [_App-Integrations-Trello_](https://github.com/symphonyoss/App-Integrations-Trello)
> 7. [_App-Integrations-Zapier_](https://github.com/symphonyoss/App-Integrations-Zapier)

2. Clone the source repository using Git: `git clone git@github.com:symphonyoss/App-Integrations-Zapier.git`
3. cd into _App-Integrations-Zapier
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


## Running with Intellij 
Here are the initial steps to get your project configured to run using the Intellij IDEA IDE. The instructions bellow assume that you have cloned App-Integrations-Core under ``/workspace/App-Integrations-Core``. Adjust the indicated procedure according to your actual path.

1. Import this project into your IDE as a ``maven`` project and be sure to choose JDK 1.8 to run it with.
2. Import any other Integration projects the same way as above (like, let's say, App-Integrations-Github or App-Integrations-Commons), but those are not required.
3. Copy [this file](docs/configuration/idea/Integration_Bridge.xml) to your App-Integrations-Core source folder under /workspace/App-Integrations-Core/.idea/runConfigurations (feel free to create the runConfigurations if you don't have it yet).
4. Go to ``Run > Edit Configurations...`` and select check the one called "Integration Bridge".
5. Check that the referenced folders do exist, they should all be pointing to ``/workspace/App-Integrations-Core/docs/configuration/boot/`` folders, to exemplify the structure you need.
6. Obtain valid, PKCS#12 user certificates to your POD and copy those to ``/workspace/App-Integrations-Core/docs/configuration/boot/certs``, you'll need one for each integration.
7. Configure valid addresses to connect the application to on the file [application.yaml](docs/configuration/boot/application.yaml)
8. Copy the JAR files for each integration to ``/workspace/App-Integrations-Core/docs/configuration/boot/libs/`` (or create a link). The JAR files have been generated on the "Build with maven" section above. For instance, Github JAR ``/workspace/App-Integrations-Github/integration-github-0.10.0-SNAPSHOT.jar`` should be copied to ``/workspace/App-Integrations-Core/docs/configuration/boot/libs/integration-github-0.10.0-SNAPSHOT.jar`` (or linked). Copy or link all the integrations you want to run on Intellij.
9. Run ``IntegrationBridgeApplication`` from the "Run" menu and start watching Intellij run output at the botton of your IDE, if everything works you should see last a message like this one:

> INFO  [org.symphonyoss.integration.core.bootstrap.IntegrationBootstrapContext] (pool-5-thread-1) lMXpkb:d8Gma6:rinXAT INFO Integration salesforceWebHookIntegration bootstrapped successfully
> INFO  [org.symphonyoss.integration.core.bootstrap.IntegrationBootstrapContext] (pool-5-thread-5) lMXpkb:d8Gma6:oOHPJ3 INFO Integration simpleWebHookIntegration bootstrapped successfully
> INFO  [org.symphonyoss.integration.core.bootstrap.IntegrationBootstrapContext] (pool-5-thread-2) lMXpkb:d8Gma6:YNMo9n INFO Integration zapierWebHookIntegration bootstrapped successfully
> INFO  [org.symphonyoss.integration.core.bootstrap.IntegrationBootstrapContext] (pool-5-thread-6) lMXpkb:d8Gma6:uAGbXe INFO Integration jiraWebHookIntegration bootstrapped successfully
> INFO  [org.symphonyoss.integration.core.bootstrap.IntegrationBootstrapContext] (pool-5-thread-3) lMXpkb:d8Gma6:5NWnjN INFO Integration trelloWebHookIntegration bootstrapped successfully
> INFO  [org.symphonyoss.integration.core.bootstrap.IntegrationBootstrapContext] (pool-5-thread-4) lMXpkb:d8Gma6:O9H1Te INFO Integration githubWebHookIntegration bootstrapped successfully

The Intellij run configuration provided as a sample is equivalent to:
```
java -Dloader.path=/workspace/App-Integrations-Core/docs/configuration/boot/libs/ -Dlog4j2.outputAllToConsole=true -Dlogs.basedir=/workspace/App-Integrations-Core/docs/configuration/boot/logs -Dfile.encoding=UTF-8 -jar /workspace/App-Integrations-Core/integration-web/target/integration.jar --spring.config.location=/workspace/App-Integrations-Core/docs/configuration/boot/ --server.tomcat.basedir=/workspace/App-Integrations-Core/docs/configuration/boot/tomcat
```
