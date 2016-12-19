package org.symphonyoss.integration.webhook.zapier;

import com.symphony.logging.ISymphonyLogger;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.logging.IntegrationBridgeCloudLoggerFactory;
import org.symphonyoss.integration.webhook.WebHookIntegration;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierNullParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * Implementation of a WebHook to integrate Zapier to Symphony.
 *
 * Created by ecarrenho on 22/09/16.
 */
@Component
public class ZapierWebHookIntegration extends WebHookIntegration {

  private static final ISymphonyLogger LOG =
      IntegrationBridgeCloudLoggerFactory.getLogger(ZapierWebHookIntegration.class);

  private Map<String, ZapierParser> parsers = new HashMap<>();

  @Autowired
  private ZapierNullParser defaultZapierParser;

  @Autowired
  private List<ZapierParser> zapierParserBeans;

  /**
   * Setup the available parsers for Zapier.
   */
  @PostConstruct
  public void init() {
    for (ZapierParser parser : zapierParserBeans) {
      List<String> events = parser.getEvents();
      for (String eventType : events) {
        this.parsers.put(eventType, parser);
      }
    }
  }

  /**
   * Parser method for the incoming Zapier payloads.
   * @param input Incoming Zapier payload.
   * @return The messageML resulting from the incoming payload parser.
   * @throws WebHookParseException when any exception occurs when parsing the payload.
   */
  @Override
  public String parse(WebHookPayload input) throws WebHookParseException {
    try {
      String webhookEvent = input.getHeaders().get(ZapierEventConstants.ZAPIER_EVENT_TYPE_HEADER);
      JsonNode rootNode = JsonUtils.readTree(input.getBody());
      ZapierParser parser = getParser(webhookEvent);
      String message = parser.parse(webhookEvent, rootNode);
      return buildMessageML(message, webhookEvent);
    } catch (IOException e) {
      throw new ZapierParserException("Something went wrong while trying to validate a message from Zapier", e);
    }
  }

  /**
   * Get the Zapier Parser based on the event.
   * @param webhookEvent Event received by the webhook
   * @return Specific zapier parser to handle the event or a default parser if no specific parser
   * found.
   */
  private ZapierParser getParser(String webhookEvent) {
    ZapierParser parser = parsers.get(webhookEvent);
    return parser != null ? parser : defaultZapierParser;
  }
}
