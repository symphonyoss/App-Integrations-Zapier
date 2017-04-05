package org.symphonyoss.integration.webhook.zapier.parser.v2;

import static org.symphonyoss.integration.parser.ParserUtils.MESSAGEML_LINEBREAK;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ACTION_FIELDS;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.BODY_FIELD;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.HEADER_FIELD;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.ICON_FIELD;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_CONTENT;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_HEADER;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.MESSAGE_ICON_URL;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.TYPE_FIELD;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.VERSION_FIELD;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.POST_MESSAGE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * Parses a post message action from Zapier and creates the corresponding messageML v2.
 * Created by rsanchez on 05/04/17.
 */
@Component
public class V2ZapierPostMessageParser extends ZapierParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(V2ZapierPostMessageParser.class);

  private static final String TEMPLATE_FILE = "templates/templatePostMessage.xml";

  private static final String EVENT_NAME = "zapierPostMessage";

  private static final String EVENT_TYPE = "com.symphony.integration.zapier.event.v2.postMessage";

  private static final String VERSION = "1.0";

  private String messageMLTemplate;

  @PostConstruct
  public void init() {
    readTemplateFile();
  }

  /**
   * Read template file.
   */
  private void readTemplateFile() {
    InputStream resource = getClass().getClassLoader().getResourceAsStream(TEMPLATE_FILE);

    if (resource == null) {
      LOGGER.error("Cannot read the template file {}. File not found.", TEMPLATE_FILE);
      return;
    }

    try(BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
      String line;
      StringBuilder responseData = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        responseData.append(line);
        responseData.append('\n');
      }

      this.messageMLTemplate = responseData.toString();
    } catch (IOException e) {
      LOGGER.error("Cannot read the template file " + TEMPLATE_FILE, e);
    }
  }

  /**
   * Returns the Zapier events handled by this parser.
   */
  @Override
  public List<String> getEvents() {
    return Arrays.asList(POST_MESSAGE);
  }

  @Override
  protected Message buildMessage(String webhookEvent, JsonNode rootNode) {
    if (StringUtils.isEmpty(messageMLTemplate)) {
      return null;
    }

    String entityJSON = getEntityJSON(rootNode);

    if (StringUtils.isNotEmpty(entityJSON)) {
      Message message = new Message();
      message.setMessage(messageMLTemplate);
      message.setData(entityJSON);
      message.setVersion(MessageMLVersion.V2);

      return message;
    }

    return null;
  }

  /**
   * Builds the entity JSON according to the Zapier payload. As follows:
   * <pre>
   *   {
   *    "zapierPostMessage": {
   *      "type": "com.symphony.integration.zapier.event.v2.postMessage",
   *      "version": "1.0",
   *      "header": "New Card Created",
   *      "body": "Card Name: Card added for symphony innovate<br/>Card Link: https://trello.com/c/8Md51YdW/card"
   *    }
   *   }
   * </pre>
   *
   * @param rootNode Zapier payload
   * @return Entity JSON
   */
  private String getEntityJSON(JsonNode rootNode) {
    final JsonNode actionFields = rootNode.path(ACTION_FIELDS);

    String messageHeader = actionFields.path(MESSAGE_HEADER).asText(StringUtils.EMPTY);

    String messageContent = actionFields.path(MESSAGE_CONTENT)
        .asText(StringUtils.EMPTY)
        .replace("\n", MESSAGEML_LINEBREAK);

    String messageIconUrl = actionFields.path(MESSAGE_ICON_URL).asText(StringUtils.EMPTY);

    ObjectNode entityJSON = JsonNodeFactory.instance.objectNode();

    ObjectNode event = entityJSON.putObject(EVENT_NAME);
    event.put(TYPE_FIELD, EVENT_TYPE);
    event.put(VERSION_FIELD, VERSION);
    event.put(HEADER_FIELD, messageHeader);
    event.put(BODY_FIELD, messageContent);
    event.put(ICON_FIELD, messageIconUrl);

    try {
      return JsonUtils.writeValueAsString(entityJSON);
    } catch (JsonProcessingException e) {
      throw new ZapierParserException("Fail to generate entityJSON", e);
    }
  }

}
