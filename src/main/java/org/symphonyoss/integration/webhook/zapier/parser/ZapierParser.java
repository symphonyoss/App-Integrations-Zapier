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

