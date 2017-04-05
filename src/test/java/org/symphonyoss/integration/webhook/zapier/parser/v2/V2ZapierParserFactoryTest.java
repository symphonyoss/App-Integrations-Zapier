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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.POST_MESSAGE;
import static org.symphonyoss.integration.webhook.zapier.ZapierEventConstants.ZAPIER_EVENT_TYPE_HEADER;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierNullParser;
import org.symphonyoss.integration.webhook.zapier.parser.v1.V1ZapierParserFactory;
import org.symphonyoss.integration.webhook.zapier.parser.v1.ZapierPostMessageParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for {@link V2ZapierParserFactory}
 * Created by rsanchez on 05/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class V2ZapierParserFactoryTest {

  @Mock
  private ZapierNullParser defaultParser;

  private V2ZapierPostMessageParser postMessageParser = new V2ZapierPostMessageParser();

  private V2ZapierParserFactory parserFactory;

  @Before
  public void init() {
    this.parserFactory = new V2ZapierParserFactory(postMessageParser);

    ReflectionTestUtils.setField(parserFactory, "defaultParser", defaultParser);
  }

  @Test
  public void testAccept() {
    assertTrue(parserFactory.accept(MessageMLVersion.V2));
    assertFalse(parserFactory.accept(MessageMLVersion.V1));
  }

  @Test
  public void testGetDefaultParser() {
    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(),
        Collections.<String, String>emptyMap(), StringUtils.EMPTY);

    WebHookParser parser = parserFactory.getParser(payload);
    assertEquals(defaultParser, parser);
  }

  @Test
  public void testGetParser() {
    Map<String, String> headers = new HashMap<>();
    headers.put(ZAPIER_EVENT_TYPE_HEADER, POST_MESSAGE);

    WebHookPayload payload = new WebHookPayload(Collections.<String, String>emptyMap(), headers, StringUtils.EMPTY);

    WebHookParser parser = parserFactory.getParser(payload);
    assertEquals(postMessageParser, parser);
  }
}
