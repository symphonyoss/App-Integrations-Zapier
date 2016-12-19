package org.symphonyoss.integration.webhook.zapier.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Interface that Zapier parsers should follow.
 *
 * Created by ecarrenho on 22/09/16.
 */
public interface ZapierParser {

  /**
   * Returns the list of handled events.
   * @return List of events handled by the given parser.
   */
  List<String> getEvents();

  /**
   * Returns the messageML document resulting from parsing the Zapier payload.
   * @param eventType The type of the event to be parsed
   * @param payload Zapier payload
   * @return messageML resulting from the payload parsing
   */
  String parse(String eventType, JsonNode payload) throws ZapierParserException;
}

