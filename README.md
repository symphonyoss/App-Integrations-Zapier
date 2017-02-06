_Note that this project depends on internal Symphony infrastructure (repository.symphony.com), and therefore it can only be built by Symphony LLC employees/partners._

# Zapier WebHook Integration
The Zapier WebHook Integration will allow you to add an ecosystem of 600+ apps to the Symphony platform. Zapier sends notifications and content to Symphony IMs or rooms from your favorite applications including GMail, Office 365, Trello, HubSpot, Twitter, LinkedIn, and hundreds of other productivity apps. 

## How it works
With access to a Zapier account, you can configure Zaps in order to receive notifications on Symphony.
A Zap is a blueprint for a workflow you want to do over and over again automatically. Creating a Zap involves choosing a *trigger* and adding one or more *action* steps.

Symphony supports Zapier **actions** to post messages to Symphony via Webhooks. *Symphony **cannot** be used as a trigger on Zapier.*

## What formats and events it supports and what it produces
Every integration will get a message sent in a specific format (depending on what system is it dealing with) and it will usually convert it into an "entity" before it reaches the Symphony platform.
It will also, usually, identify the kind of message it will deal with based on an "event" identifier, that varies based on which system is it integrating with.

We currently support any configured action via our **action** app on Zapier.
There, you can choose: an icon, a message header and a message body.

The message header and body must follow the rules for a Symphony Message ML, which can be accessed here, although you can safely insert just pure text and your triggering app info (like a trigger related name, title or anything that will translate to text on your message).

### Sample Action

Here we'll show you a sample payload that Zapier will send us when a configured Trello "card created" triggers, the generated entity and finally how the message will look like on Symphony. 

##### Trello payload

```json
{
  "auth_fields": {},
  "request": {
    "files": {},
    "url": "http://requestb.in/1miwh011",
    "headers": {
      "Content-Type": "application/json; charset=utf-8",
      "Accept": "application/json"
    },
    "params": {},
    "data": "{\"message_content\": \"Test Message Body:\\n* Card Test Trello have just been created\", \"message_header\": \"Test Message Header: Trello card Test Trello created\", \"webhook_url\": \"http://requestb.in/1miwh011\"}",
    "method": "POST"
  },
  "action_fields": {
    "message_content": "Test Message Body:\n* Card Test Trello have just been created",
    "message_header": "Test Message Header: Trello card Test Trello created",
    "webhook_url": "http://requestb.in/1miwh011"
  },
  "action_fields_full": {
    "message_content": "Test Message Body:\n* Card Test Trello have just been created",
    "message_header": "Test Message Header: Trello card Test Trello created",
    "webhook_url": "http://requestb.in/1miwh011"
  },
  "meta": {
    "frontend": true
  },
  "action_fields_raw": {
    "message_content": "Test Message Body:\n* Card {{15919238__name}} have just been created",
    "message_header": "Test Message Header: Trello card {{15919238__name}} created",
    "webhook_url": "http://requestb.in/1miwh011"
  },
  "url_raw": "{{webhook_url}}",
  "zap": {
    "live": true,
    "link": "https://zapier.com/app/editor/15919238",
    "name": "Test Trello!",
    "user": {
      "timezone": "Atlantic/South_Georgia"
    }
  }
}
```
##### Generated Symphony entity

```xml
<messageML>
  <entity type="com.symphony.integration.zapier.event.post_message" version="1.0">
    <presentationML>
      Test Message Header: Trello card Test Trello created<br/>
      Test Message Body:<br/>
      * Card Test Trello have just been created
    </presentationML>
    <entity type="com.symphony.integration.zapier.zap" version="1.0">
      <attribute name="name" type="org.symphonyoss.string" value="Test Trello!" />
      <attribute name="link" type="com.symphony.uri" value="https://zapier.com/app/editor/15919238" />
      <attribute name="live" type="org.symphonyoss.string" value="true" />
    </entity>
    <entity name="action_fields" type="com.symphony.integration.zapier.fields" version="1.0">
      <attribute name="message_header" type="org.symphonyoss.string" value="Test Message Header: Trello card Test Trello created" />
      <attribute name="message_content" type="org.symphonyoss.string" value="Test Message Body:&amp;lt;br/&amp;gt;* Card Test Trello have just been created" />
    </entity>
    <entity name="action_fields_full" type="com.symphony.integration.zapier.fields" version="1.0">
      <attribute name="message_header" type="org.symphonyoss.string" value="Test Message Header: Trello card Test Trello created" />
      <attribute name="message_content" type="org.symphonyoss.string" value="Test Message Body:&amp;lt;br/&amp;gt;* Card Test Trello have just been created" />
    </entity>
    <entity name="action_fields_raw" type="com.symphony.integration.zapier.fields" version="1.0">
      <attribute name="message_header" type="org.symphonyoss.string" value="Test Message Header: Trello card {{15919238__name}} created" />
      <attribute name="message_content" type="org.symphonyoss.string" value="Test Message Body:&amp;lt;br/&amp;gt;* Card {{15919238__name}} have just been created" />
    </entity>
  </entity>
</messageML>
```

##### Message rendered on Symphony

![Sample Action](src/docs/samples/sample_trello_action_rendered.png)