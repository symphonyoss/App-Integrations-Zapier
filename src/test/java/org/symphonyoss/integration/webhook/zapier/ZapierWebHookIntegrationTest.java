package org.symphonyoss.integration.webhook.zapier;

import static java.util.Collections.EMPTY_MAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants
    .ZAPIER_EVENT_TYPE_HEADER;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierNullParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserException;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierPostMessageParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class to validate {@link ZapierWebHookIntegration}
 * Created by ecarrenho on 22/09/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZapierWebHookIntegrationTest {

  @Spy
  private static List<ZapierParser> beans = new ArrayList<>();

  @Spy
  ZapierNullParser defaultZapierParser = new ZapierNullParser();

  @InjectMocks
  private ZapierWebHookIntegration zapierWebHookIntegration = new ZapierWebHookIntegration();

  private Map<String, String> headers = new HashMap<>();

  private String readFile(String fileName) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    String expected =
        FileUtils.readFileToString(new File(classLoader.getResource(fileName).getPath()));
    return expected.replaceAll("\n", "");
  }

  @BeforeClass
  public static void init() {
    beans.add(new ZapierNullParser());
    beans.add(new ZapierPostMessageParser());
  }

  @Before
  public void setup() {
    headers.put("Content-Type", "application/json");
    headers.put(ZAPIER_EVENT_TYPE_HEADER, "post_message");

    zapierWebHookIntegration.init();
  }

  @Test
  public void testUnknownEvent() throws IOException, WebHookParseException {
    String body = readFile("zapierHeaderContentIcon.json");

    Map<String, String> unknownEventHeaders = new HashMap<>();
    unknownEventHeaders.put("Content-Type", "application/json");
    unknownEventHeaders.put(ZAPIER_EVENT_TYPE_HEADER, "read_message");

    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, unknownEventHeaders, body);
    assertNull(zapierWebHookIntegration.parse(payload));
  }

  @Test
  public void testNoEventPayload() throws IOException, WebHookParseException {
    String body = readFile("zapierHeaderContentIcon.json");

    Map<String, String> noEventHeaders = new HashMap<>();
    noEventHeaders.put("Content-Type", "application/json");

    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, noEventHeaders, body);
    assertNull(zapierWebHookIntegration.parse(payload));
  }

  @Test(expected = ZapierParserException.class)
  public void testFailReadingJSON() throws WebHookParseException {
      String body = "";

      WebHookPayload payload = new WebHookPayload(EMPTY_MAP, EMPTY_MAP, body);
      zapierWebHookIntegration.parse(payload);
  }

  @Test
  public void testPostMessageHeaderContentIcon() throws IOException, WebHookParseException {
    String expected = readFile("zapierHeaderContentIcon.xml");
    String body = readFile("zapierHeaderContentIcon.json");
    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, headers, body);

    String result = zapierWebHookIntegration.parse(payload);

    assertEquals(expected, result);
  }

  @Test
  public void testPostMessageHeaderContent() throws IOException, WebHookParseException {
    String expected = readFile("zapierHeaderContent.xml");
    String body = readFile("zapierHeaderContent.json");
    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, headers, body);

    String result = zapierWebHookIntegration.parse(payload);

    assertEquals(expected, result);
  }

  @Test
  public void testPostMessageHeader() throws IOException, WebHookParseException {
    String expected = readFile("zapierHeader.xml");
    String body = readFile("zapierHeader.json");
    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, headers, body);

    String result = zapierWebHookIntegration.parse(payload);

    assertEquals(expected, result);
  }

  @Test
  public void testPostMessageContent() throws IOException, WebHookParseException {
    String expected = readFile("zapierContent.xml");
    String body = readFile("zapierContent.json");
    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, headers, body);

    String result = zapierWebHookIntegration.parse(payload);

    assertEquals(expected, result);
  }

  @Test
  public void testPostMessageNoHeaderNoContentWithIcon() throws IOException, WebHookParseException {
    String body = readFile("zapierNoHeaderNoContentWithIcon.json");
    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, headers, body);

    assertNull(zapierWebHookIntegration.parse(payload));
  }

  @Test
  public void testPostMessageNoHeaderNoContentNoIcon() throws IOException, WebHookParseException {
    String body = readFile("zapierNoHeaderNoContentNoIcon.json");
    WebHookPayload payload = new WebHookPayload(EMPTY_MAP, headers, body);

    assertNull(zapierWebHookIntegration.parse(payload));
  }

}