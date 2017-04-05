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

package org.symphonyoss.integration.webhook.zapier.parser.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.POST_MESSAGE;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.ZAPIER_EVENT_TYPE_HEADER;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.zapier.ZapierWebHookIntegration;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class to validate {@link ZapierWebHookIntegration}
 * Created by ecarrenho on 22/09/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZapierPostMessageParserTest {

  @InjectMocks
  private ZapierPostMessageParser parser;

  private Map<String, String> headers = new HashMap<>();

  @Before
  public void setup() {
    headers.put("Content-Type", "application/json");
    headers.put(ZAPIER_EVENT_TYPE_HEADER, "post_message");
  }

  private String readFile(String fileName) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    String expected =
        FileUtils.readFileToString(new File(classLoader.getResource(fileName).getPath()));
    return expected.replaceAll("\n", "");
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
  public void testPostMessageHeaderContentIcon() throws IOException, WebHookParseException {
    String expected = readFile("zapierHeaderContentIcon.xml");
    String body = readFile("zapierHeaderContentIcon.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    Message result = parser.parse(payload);

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testPostMessageHeaderContent() throws IOException, WebHookParseException {
    String expected = readFile("zapierHeaderContent.xml");
    String body = readFile("zapierHeaderContent.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    Message result = parser.parse(payload);

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testPostMessageHeader() throws IOException, WebHookParseException {
    String expected = readFile("zapierHeader.xml");
    String body = readFile("zapierHeader.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    Message result = parser.parse(payload);

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testPostMessageContent() throws IOException, WebHookParseException {
    String expected = readFile("zapierContent.xml");
    String body = readFile("zapierContent.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    Message result = parser.parse(payload);

    assertEquals(expected, result.getMessage());
  }

  @Test
  public void testPostMessageNoHeaderNoContentWithIcon() throws IOException, WebHookParseException {
    String body = readFile("zapierNoHeaderNoContentWithIcon.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    assertNull(parser.parse(payload));
  }

  @Test
  public void testPostMessageNoHeaderNoContentNoIcon() throws IOException, WebHookParseException {
    String body = readFile("zapierNoHeaderNoContentNoIcon.json");
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, body);

    assertNull(parser.parse(payload));
  }

}