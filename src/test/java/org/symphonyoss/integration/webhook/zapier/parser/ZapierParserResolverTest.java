package org.symphonyoss.integration.webhook.zapier.parser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.event.MessageMLVersionUpdatedEventData;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.zapier.parser.v2.V2ZapierParserFactory;
import org.symphonyoss.integration.webhook.zapier.parser.v2.V2ZapierPostMessageParser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by crepache on 12/06/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZapierParserResolverTest {

  @Spy
  private List<ZapierParserFactory> factories = new ArrayList<>();

  private V2ZapierParserFactory v2ParserFactory;

  private V2ZapierPostMessageParser v2ZapierPostMessageParser = new V2ZapierPostMessageParser();

  @InjectMocks
  private ZapierParserResolver resolver;

  @Before
  public void setup() {
    v2ParserFactory = new V2ZapierParserFactory(v2ZapierPostMessageParser);

    factories.add(v2ParserFactory);
  }

  @Test
  public void testHandleMessageMLV2() {
    MessageMLVersionUpdatedEventData event = new MessageMLVersionUpdatedEventData(MessageMLVersion.V2);
    resolver.handleMessageMLVersionUpdatedEvent(event);

    assertEquals(v2ParserFactory, resolver.getFactory());
  }
}