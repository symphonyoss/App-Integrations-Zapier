package org.symphonyoss.integration.webhook.zapier.parser;

import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ACTION_FIELDS;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ACTION_FIELDS_FULL;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ACTION_FIELDS_RAW;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.INTEGRATION_NAME;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_CONTENT;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_HEADER;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ZAP;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.POST_MESSAGE;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.entity.EntityBuilder;
import org.symphonyoss.integration.exception.EntityXMLGeneratorException;
import org.symphonyoss.integration.parser.ParserUtils;
import org.symphonyoss.integration.parser.SafeString;
import org.symphonyoss.integration.parser.SafeStringUtils;
import org.symphonyoss.integration.webhook.zapier.model.ZapierMessageDescriptor;
import org.symphonyoss.integration.webhook.zapier.model.ZapierZap;

import java.util.Arrays;
import java.util.List;

/**
 * Parses a post message action from Zapier and creates the corresponding presentation and entityML.
 * Created by ecarrenho on 22/09/16.
 */
@Component
public class ZapierPostMessageParser implements ZapierParser {

  /**
   * Zapier message content to be displayed to the user. Formats the message text as:
   *   message header
   *
   * Parameters:
   *   1st - message header text
   */
  private static final String POST_MESSAGE_FORMATTED_TEXT_HEADER = "%s";

  /**
   * Zapier message content to be displayed to the user. Formats the message text as:
   *   message content
   *
   * Parameters:
   *   1st - message body text
   */
  private static final String POST_MESSAGE_FORMATTED_TEXT_BODY = "%s";

  /**
   * Zapier message content to be displayed to the user. Formats the message text as:
   *   message header [line break]
   *   message content
   *
   * Parameters:
   *   1st - message header text
   *   2nd - message body text
   */
  private static final String POST_MESSAGE_FORMATTED_TEXT_HEADER_BODY = "%s<br/>%s";

  /**
   * Returns the Zapier events handled by this parser.
   */
  @Override
  public List<String> getEvents() {
    return Arrays.asList(POST_MESSAGE);
  }

  /**
   * Creates the presentation and entityML for the incoming payload. As follows:
   * <pre>
   * {code
   * <messageML>
   *   <entity type="com.symphony.integration.zapier.event.post_message" version="1.0">
   *     <presentationML>
   *       New Trello Card Created
   *       <br />
   *       &lt;b&gt;Card Name:&lt;/b&gt; Card added for symphony innovate
   *       <br />
   *       &lt;b&gt;Card Link:&lt;/b&gt;
   *       <a href="https://trello.com/c/8Md51YdW/15-card-added-for-symphony-innovate" />
   *     </presentationML>
   *     <entity type="com.symphony.integration.zapier.zap" version="1.0">
   *       <attribute name="name" type="org.symphonyoss.string" value="Card Created" />
   *       <attribute name="link" type="com.symphony.uri" value="https://zapier.com/app/edit/12156591" />
   *       <attribute name="live" type="org.symphonyoss.string" value="true" />
   *     </entity>
   *       <entity name="action_fields" type="com.symphony.integration.zapier.fields" version="1.0">
   *       <attribute name="message_header" type="org.symphonyoss.string" value="New Trello Card Created" />
   *       <attribute name="message_content" type="org.symphonyoss.string" value="&lt;b&gt;Card Name:&lt;/b&gt; Card added for symphony innovate&lt;br/&gt;&lt;b&gt;Card Link:&lt;/b&gt; https://trello.com/c/8Md51YdW/15-card-added-for-symphony-innovate" />
   *       <attribute name="message_icon" type="com.symphony.uri" value="" />
   *     </entity>
   *       <entity name="action_fields_full" type="com.symphony.integration.zapier.fields" version="1.0">
   *       <attribute name="message_header" type="org.symphonyoss.string" value="full - New Trello Card Created" />
   *       <attribute name="message_content" type="org.symphonyoss.string" value="full - &lt;b&gt;Card Name:&lt;/b&gt; Card added for symphony innovate&lt;br/&gt;&lt;b&gt;Card Link:&lt;/b&gt; https://trello.com/c/8Md51YdW/15-card-added-for-symphony-innovate" />
   *       <attribute name="message_icon" type="com.symphony.uri" value="" />
   *     </entity>
   *     <entity name="action_fields_raw" type="com.symphony.integration.zapier.fields" version="1.0">
   *       <attribute name="message_header" type="org.symphonyoss.string" value="New Trello Card Created" />
   *       <attribute name="message_content" type="org.symphonyoss.string" value="&lt;b&gt;Card Name:&lt;/b&gt; {{12156591__name}}&lt;br/&gt;&lt;b&gt;Card Link:&lt;/b&gt; {{12156591__url}}" />
   *       <attribute name="message_icon" type="com.symphony.uri" value="" />
   *     </entity>
   *   </entity>
   * </messageML>
   * }
   * </pre>
   */
  @Override
  public String parse(String eventType, JsonNode payload) throws ZapierParserException {
    final SafeString formattedText = createFormattedText(payload);
    if (SafeStringUtils.isEmpty(formattedText)) {
      return null;
    }

    try {
      final EntityBuilder eventBuilder = createBuilderWithEntities(payload, eventType);
      final String entityMl = eventBuilder
          .presentationML(formattedText)
          .generateXML();
      return entityMl;
    } catch (EntityXMLGeneratorException e) {
      throw new ZapierParserException("Something went wrong while building the message for Zapier.", e);
    }
  }

  /**
   * Creates the EntityBuilder with nested entities: zap and action fields for a post message event.
   */
  private EntityBuilder createBuilderWithEntities(JsonNode payload, String eventType) {

    final ZapierZap zap = new ZapierZap(payload.path(ZAP));
    final ZapierMessageDescriptor descriptor = new ZapierMessageDescriptor(payload.path
        (ACTION_FIELDS));
    final ZapierMessageDescriptor descriptorFull = new ZapierMessageDescriptor(payload.path
        (ACTION_FIELDS_FULL));
    final ZapierMessageDescriptor descriptorRaw = new ZapierMessageDescriptor(payload.path
        (ACTION_FIELDS_RAW));

    final EntityBuilder eventBuilder = EntityBuilder.forIntegrationEvent(INTEGRATION_NAME,
        eventType)
        .nestedEntity(zap.toEntity())
        .nestedEntity(descriptor.toEntity(ACTION_FIELDS))
        .nestedEntity(descriptorFull.toEntity(ACTION_FIELDS_FULL))
        .nestedEntity(descriptorRaw.toEntity(ACTION_FIELDS_RAW));

    return eventBuilder;
  }

  /**
   * Formats the text message for the Zapier event, with message header and body.
   */
  private SafeString createFormattedText(JsonNode payload) {

    // Get message header and content from the payload
    final JsonNode actionFields = payload.path(ACTION_FIELDS);
    String messageHeader = actionFields.path(MESSAGE_HEADER).textValue();
    String messageContent = actionFields.path(MESSAGE_CONTENT).textValue();

    if (StringUtils.isNotBlank(messageHeader)) {
      // Header is not blank, formats the message with the header and content (if not blank)
      if (StringUtils.isBlank(messageContent)) {
        return ParserUtils.presentationFormat(POST_MESSAGE_FORMATTED_TEXT_HEADER, messageHeader);
      } else {
        return ParserUtils.presentationFormat(POST_MESSAGE_FORMATTED_TEXT_HEADER_BODY,
            messageHeader, messageContent);
      }
    } else if (StringUtils.isNotBlank(messageContent)) {

      // Header is blank, content is not, formats the message with only the received content
      return ParserUtils.presentationFormat(POST_MESSAGE_FORMATTED_TEXT_BODY, messageContent);
    }

    // Header and content are blank, formatted text is not returned
    return SafeString.EMPTY_SAFE_STRING;
  }
}

