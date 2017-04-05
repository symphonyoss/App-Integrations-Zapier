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

package org.symphonyoss.integration.webhook.zapier.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

/**
 * Unit test for {@link ZapierNullParser}
 * Created by rsanchez on 05/04/17.
 */
public class ZapierNullParserTest {

  private ZapierNullParser parser = new ZapierNullParser();

  @Test
  public void testGetEvents() {
    List<String> events = parser.getEvents();

    assertNotNull(events);
    assertTrue(events.isEmpty());
  }

  @Test
  public void testParse() {
    assertNull(parser.parse(null));
  }

}
