package org.symphonyoss.integration.webhook.zapier.model;

import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.FIELDS;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.INTEGRATION_NAME;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_CONTENT;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_HEADER;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_ICON_URL;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.EntityBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Creates an entityML for a Zapier message descriptor, based on a JSON node containing Zapier
 * message fields.
 *
 * A Zapier message descriptor is an object containing the action fields provided by the user
 * on the Zapier action associated to the trigger/action recipe the user has created at Zapier.
 *
 * Created by ecarrenho on 20/09/16.
 */
public class ZapierMessageDescriptor {

  private JsonNode rootNode;

  /**
   * Instantiates a message descriptor object using the provided JSON node.
   * @param actionFieldsNode JSON node containing the message header, message body and icon URL.
   */
  public ZapierMessageDescriptor(JsonNode actionFieldsNode) {
    this.rootNode = actionFieldsNode;
  }

  public String getMessageContent() {
    return rootNode.path(MESSAGE_CONTENT).asText();
  }

  public String getMessageHeader() {
    return rootNode.path(MESSAGE_HEADER).asText();
  }

  /**
   * Returns the URL for the message icon, if present on the provided JSON node.
   * @return Icon URL.
   */
  public URI getIconUrl() {
    try {
      final String urlString = rootNode.path(MESSAGE_ICON_URL).textValue();
      if (!StringUtils.isBlank(urlString)) {
        return new URI(urlString);
      }
      return null;
    } catch (URISyntaxException e) {
      return null;
    }
  }

  /**
   * Builds the action fields entity as follows:
   *
   * <pre>
   * {@code
   * <entity name="action_fields" type="com.symphony.integration.zapier.fields" version="1.0">
   *   <attribute name="message_header" type="org.symphonyoss.string" value="message header" />
   *   <attribute name="message_content" type="org.symphonyoss.string" value="message content" />
   *   <attribute name="message_icon" type="com.symphony.uri" value="http://my.icon.com" />
   *   <attribute name="webhook_url" type="com.symphony.uri" value="https://requestb.in/ol98h8ol" />
   * </entity>
   * }
   * </pre>
   * @param name The entity name, e.g. "action_fields"
   * @return action fields entity with message content, message header and message icon.
   */
  public Entity toEntity(String name) {
    return EntityBuilder.forNestedEntity(INTEGRATION_NAME, name, FIELDS)
        .attribute(MESSAGE_HEADER, getMessageHeader())
        .attributeIfNotEmpty(MESSAGE_CONTENT, getMessageContent())
        .attributeIfNotNull(MESSAGE_ICON_URL, getIconUrl())
        .build();
  }
}
