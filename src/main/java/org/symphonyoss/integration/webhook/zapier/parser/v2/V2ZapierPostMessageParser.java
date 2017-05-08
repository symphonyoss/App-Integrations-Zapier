package org.symphonyoss.integration.webhook.zapier.parser.v2;

import static org.symphonyoss.integration.parser.ParserUtils.MESSAGEML_LINEBREAK;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ACTION_FIELDS;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_CONTENT;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_HEADER;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.POST_MESSAGE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.parser.ParserUtils;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.parser.metadata.MetadataParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Parses a post message action from Zapier and creates the corresponding messageML v2.
 * Created by rsanchez on 05/04/17.
 */
@Component
public class V2ZapierPostMessageParser extends MetadataParser implements WebHookParser {

  private static final String TEMPLATE_FILE = "templatePostMessage.xml";

  private static final String METADATA_FILE = "metadataPostMessage.xml";

  /**
   * Returns the Zapier events handled by this parser.
   */
  @Override
  public List<String> getEvents() {
    return Arrays.asList(POST_MESSAGE);
  }

  /**
   * Builds the entity JSON according to the Zapier payload. As follows:
   *
   * <pre>
   *   {
   *    "zapierPostMessage": {
   *      "type": "com.symphony.integration.zapier.event.v2.postMessage",
   *      "version": "1.0",
   *      "header": "New Card Created",
   *      "body": "Card Name: Card added for symphony innovate<br/>Card Link: https://trello.com/c/8Md51YdW/card"
   *      "icon": "https://icon.symphony.com"
   *    }
   *   }
   * </pre>
   *
   * @param payload Zapier payload
   * @return Entity JSON
   */
  @Override
  public Message parse(WebHookPayload payload) throws WebHookParseException {
    try {
      JsonNode rootNode = JsonUtils.readTree(payload.getBody());
      return parse(rootNode);
    } catch (IOException e) {
      throw new ZapierParserException(
          "Something went wrong while trying to validate a message from Zapier", e);
    }
  }

  /**
   * Replace the '\n' to <br/> tag on the message content.
   * @param input JSON input payload
   */
  @Override
  protected void preProcessInputData(JsonNode input) {
    JsonNode actionNode = input.path(ACTION_FIELDS);

    String messageHeader = actionNode.path(MESSAGE_HEADER).asText(StringUtils.EMPTY);
    String messageContent = actionNode.path(MESSAGE_CONTENT).asText(StringUtils.EMPTY);

    if ((StringUtils.isEmpty(messageHeader)) && (StringUtils.isEmpty(messageContent))) {
      String errorMessage = String.format("Fields {} and {} are empty.", MESSAGE_HEADER, MESSAGE_CONTENT);
      throw new ZapierParserException(errorMessage);
    }

    if (StringUtils.isNotEmpty(messageHeader)) {
      messageHeader = ParserUtils.markupLinks(messageHeader);
      ((ObjectNode) actionNode).put(MESSAGE_HEADER, messageHeader);
    }

    if (StringUtils.isNotEmpty(messageContent)) {
      messageContent = messageContent.replace("\n", MESSAGEML_LINEBREAK);
      messageContent = ParserUtils.markupLinks(messageContent);
      ((ObjectNode) actionNode).put(MESSAGE_CONTENT, messageContent);
    }
  }

  @Override
  protected String getTemplateFile() {
    return TEMPLATE_FILE;
  }

  @Override
  protected String getMetadataFile() {
    return METADATA_FILE;
  }

}
