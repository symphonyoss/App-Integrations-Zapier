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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.config.IntegrationSettings;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.parser.WebHookParserFactory;
import org.symphonyoss.integration.webhook.zapier.ZapierEventConstants;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierNullParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base factory class to build the Zapier parser.
 * Created by rsanchez on 05/04/17.
 */
public abstract class ZapierParserFactory implements WebHookParserFactory {

  private WebHookParser parser;

  @Autowired
  private ZapierNullParser defaultParser;

  public ZapierParserFactory(WebHookParser parser) {
    this.parser = parser;
  }

  @Override
  public void onConfigChange(IntegrationSettings settings) {
    // Do nothing
  }

  /**
   * Get the parser class based on the event received from Zapier.
   *
   * The HTTP header 'zapier-event-type' will be used to perform this selection.
   *
   * @param payload Zapier HTTP request
   * @return Parser class to handle the event
   */
  @Override
  public WebHookParser getParser(WebHookPayload payload) {
    String webhookEvent = payload.getHeaders().get(ZapierEventConstants.ZAPIER_EVENT_TYPE_HEADER);

    if (parser.getEvents().contains(webhookEvent)) {
      return parser;
    } else {
      return defaultParser;
    }
  }

}
