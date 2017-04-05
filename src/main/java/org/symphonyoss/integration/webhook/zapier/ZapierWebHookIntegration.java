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

package org.symphonyoss.integration.webhook.zapier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookIntegration;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserResolver;

/**
 * Implementation of a WebHook to integrate Zapier to Symphony.
 *
 * Created by ecarrenho on 22/09/16.
 */
@Component
public class ZapierWebHookIntegration extends WebHookIntegration {

  @Autowired
  private ZapierParserResolver parserResolver;

  @Override
  public void onCreate(String integrationUser) {
    super.onCreate(integrationUser);
    parserResolver.healthCheckAgentService();
  }

  /**
   * Parser method for the incoming Zapier payloads.
   * @param input Incoming Zapier payload.
   * @return The messageML resulting from the incoming payload parser.
   * @throws WebHookParseException when any exception occurs when parsing the payload.
   */
  @Override
  public Message parse(WebHookPayload input) throws WebHookParseException {
    WebHookParser parser = getParser(input);
    return parser.parse(input);
  }

  /**
   * Get the Zapier Parser based on the event.
   * @param payload Payload received by the webhook
   * @return Specific zapier parser to handle the event or a default parser if no specific parser
   * found.
   */
  private WebHookParser getParser(WebHookPayload payload) {
    return parserResolver.getFactory().getParser(payload);
  }
}
