package org.symphonyoss.integration.webhook.zapier.model;

import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.INTEGRATION_NAME;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.LINK;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.LIVE;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.NAME;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ZAP;

import com.fasterxml.jackson.databind.JsonNode;
import org.symphonyoss.integration.entity.Entity;
import org.symphonyoss.integration.entity.EntityBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Creates a Zap entityML element based on a JSON node containing Zap data.
 * A Zap is a recipe on Zapier, containing triggers and actions.
 *
 * Created by ecarrenho on 20/09/16.
 */
public class ZapierZap {

  private JsonNode rootNode;

  /**
   * Instantiates a Zapier Zap using the provided Zap JSON node.
   * @param zapNode JSON node containing the Zap name, link and live flag.
   */
  public ZapierZap(JsonNode zapNode) {
    this.rootNode = zapNode;
  }

  public String getName() {
    return rootNode.path(NAME).asText();
  }

  /**
   * Returns the URL for the Zap link, if present on the provided JSON node.
   * @return Zap link URL.
   */
  public URI getLink() {
    try {
      return new URI(rootNode.path(LINK).asText());
    } catch (URISyntaxException e) {
      return null;
    }
  }

  public String getLive() {
    return rootNode.path(LIVE).asText();
  }

  /**
   * Builds the Zap entity as follows:
   *
   * <pre>
   * {@code
   * <entity type="com.symphony.integration.zapier.zap" version="1.0">
   *   <attribute name="name" type="org.symphonyoss.string" value="Card Created" />
   *   <attribute name="link" type="com.symphony.uri" value="https://zapier.com/app/edit/12156591 />
   *   <attribute name="live" type="org.symphonyoss.string" value="true" />
   * </entity>
   * }
   * </pre>
   *
   * Link attribute may not be provided if the provided JSON has an invalid URL.
   * @return entityML Zap.
   */
  public Entity toEntity() {
    return EntityBuilder.forNestedEntity(INTEGRATION_NAME, ZAP)
        .attribute(NAME, getName())
        .attributeIfNotNull(LINK, getLink())
        .attribute(LIVE, getLive())
        .build();
  }
}
