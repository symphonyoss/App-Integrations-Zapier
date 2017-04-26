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

import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;

import java.util.Collections;
import java.util.List;

/**
 * This parser will be used to handle unknown events from Zapier. It just returns a null message.
 *
 * Created by ecarrenho on 22/09/16.
 */
@Component
public class ZapierNullParser implements WebHookParser {

  /**
   * Returns an empty list, as this parser should not be used to validate valid events. Instead,
   * it is used to return a null message for unknown events.
   * @return An empty list.
   */
  @Override
  public List<String> getEvents() {
    return Collections.emptyList();
  }

  /**
   * Returns a null message, as this parser handles unknown events.
   * @return Null
   */
  @Override
  public Message parse(WebHookPayload payload) throws WebHookParseException {
    return null;
  }

}
