package org.symphonyoss.integration.webhook.zapier.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * This parser will be used to handle unknown events from Zapier. It just returns a null message.
 *
 * Created by ecarrenho on 22/09/16.
 */
@Component
public class ZapierNullParser implements ZapierParser {

  /**
   * Returns an empty list, as this parser should not be used to validate valid events. Instead,
   * it is used to return a null message for unknown events.
   * @return An empty list.
   */
  @Override
  public List<String> getEvents() {
    return Collections.emptyList();
  }

  /**
   * Returns a null message, as this parser handles unknown events.
   * @return Null
   */
  @Override
  public String parse(String eventType, JsonNode payload) throws ZapierParserException {
    return null;
  }

}
