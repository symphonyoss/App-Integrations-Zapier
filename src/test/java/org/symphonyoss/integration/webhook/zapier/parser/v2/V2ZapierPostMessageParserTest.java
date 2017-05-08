/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.zapier.parser.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.symphonyoss.integration.webhook.zapier.ZapierEntityConstants.INTEGRATION_NAME;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.POST_MESSAGE;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.ZAPIER_EVENT_TYPE_HEADER;


import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.model.yaml.IntegrationProperties;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class to validate {@link V2ZapierPostMessageParser}
 * Created by ecarrenho on 22/09/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class V2ZapierPostMessageParserTest {

  @Mock
  private IntegrationProperties properties;

  @InjectMocks
  private V2ZapierPostMessageParser parser;

  private Map<String, String> headers = new HashMap<>();

  private String expectedMessageML;

  @Before
  public void setup() throws IOException {
    headers.put("Content-Type", "application/json");
    headers.put(ZAPIER_EVENT_TYPE_HEADER, "post_message");

    this.expectedMessageML = readFile("templates/templatePostMessage.xml") + '\n';
  }

  private String readFile(String fileName) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return FileUtils.readFileToString(new File(classLoader.getResource(fileName).getPath()), "UTF-8");
  }

  private String readJsonFile(String fileName) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    JsonNode node = JsonUtils.readTree(classLoader.getResourceAsStream(fileName));

    return JsonUtils.writeValueAsString(node);
  }

  @Test
  public void testGetEvents() {
    List<String> events = parser.getEvents();

    assertNotNull(events);
    assertEquals(1, events.size());
    assertEquals(POST_MESSAGE, events.get(0));
  }

  @Test(expected = ZapierParserException.class)
  public void testFailReadingJSON() throws WebHookParseException {
      String body = "";

      WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), body);
      parser.parse(payload);
  }

  @Test
  public void testPostMessageEmptyTemplate() throws IOException, WebHookParseException {
    String body = readJsonFile("zapierHeaderContentIcon.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    assertNull(parser.parse(payload));
  }

  @Test
  public void testPostMessageHeaderContentIcon() throws IOException, WebHookParseException {
    parser.init();

    String body = readJsonFile("zapierHeaderContentIcon.json");
    String expectedEntityJson = readJsonFile("v2/entityJsonHeaderContentIcon.json");

    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    Message result = parser.parse(payload);

    assertEquals(expectedMessageML, result.getMessage());
    assertEquals(expectedEntityJson, result.getData());
  }

  @Test
  public void testPostMessageHeaderContent() throws IOException, WebHookParseException {
    parser.init();

    doReturn("http://test.symphony.com/apps/zapier").when(properties).getApplicationUrl(INTEGRATION_NAME);

    String body = readJsonFile("zapierHeaderContent.json");
    String expectedEntityJson = readJsonFile("v2/entityJsonHeaderContent.json");

    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    Message result = parser.parse(payload);

    assertEquals(expectedMessageML, result.getMessage());
    assertEquals(expectedEntityJson, result.getData());
  }

  @Test
  public void testPostMessageHeader() throws IOException, WebHookParseException {
    parser.init();

    String body = readJsonFile("zapierHeader.json");
    String expectedEntityJson = readJsonFile("v2/entityJsonHeader.json");

    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    Message result = parser.parse(payload);

    assertEquals(expectedMessageML, result.getMessage());
    assertEquals(expectedEntityJson, result.getData());
  }

  @Test(expected = ZapierParserException.class)
  public void testPostMessageNoHeaderNoContentWithIcon() throws IOException, WebHookParseException {
    parser.init();

    String body = readFile("zapierNoHeaderNoContentWithIcon.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    parser.parse(payload);
  }

  @Test(expected = ZapierParserException.class)
  public void testPostMessageNoHeaderNoContentNoIcon() throws IOException, WebHookParseException {
    parser.init();

    String body = readFile("zapierNoHeaderNoContentNoIcon.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    parser.parse(payload);
  }
}