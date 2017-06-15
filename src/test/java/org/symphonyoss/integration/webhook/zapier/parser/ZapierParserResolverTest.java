package org.symphonyoss.integration.webhook.zapier.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.event.MessageMLVersionUpdatedEventData;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.zapier.parser.v1.V1ZapierParserFactory;
import org.symphonyoss.integration.webhook.zapier.parser.v1.ZapierPostMessageParser;
import org.symphonyoss.integration.webhook.zapier.parser.v2.V2ZapierParserFactory;
import org.symphonyoss.integration.webhook.zapier.parser.v2.V2ZapierPostMessageParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crepache on 12/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZapierParserResolverTest {

  @Spy
  private List<ZapierParserFactory> factories = new ArrayList<>();

  private V1ZapierParserFactory v1ParserFactory;

  private V2ZapierParserFactory v2ParserFactory;

  private ZapierPostMessageParser zapierPostMessageParser = new ZapierPostMessageParser();

  private V2ZapierPostMessageParser v2ZapierPostMessageParser = new V2ZapierPostMessageParser();

  @InjectMocks
  private ZapierParserResolver resolver;

  @Before
  public void setup() {
    v1ParserFactory = new V1ZapierParserFactory(zapierPostMessageParser);
    v2ParserFactory = new V2ZapierParserFactory(v2ZapierPostMessageParser);

    factories.add(v1ParserFactory);
    factories.add(v2ParserFactory);
  }

  @Test
  public void testInit() {
    resolver.init();

    assertEquals(v1ParserFactory, resolver.getFactory());
  }

  @Test
  public void testHandleMessageMLV1() {
    MessageMLVersionUpdatedEventData event = new MessageMLVersionUpdatedEventData(MessageMLVersion.V1);
    resolver.handleMessageMLVersionUpdatedEvent(event);

    assertEquals(v1ParserFactory, resolver.getFactory());
  }

  @Test
  public void testHandleMessageMLV2() {
    MessageMLVersionUpdatedEventData event = new MessageMLVersionUpdatedEventData(MessageMLVersion.V2);
    resolver.handleMessageMLVersionUpdatedEvent(event);

    assertEquals(v2ParserFactory, resolver.getFactory());
  }
}