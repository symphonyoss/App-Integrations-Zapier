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
public abstract class BaseZapierParserFactory implements WebHookParserFactory {

  private List<WebHookParser> parsers = new ArrayList<>();

  @Autowired
  private ZapierNullParser defaultParser;

  public BaseZapierParserFactory(WebHookParser... parsers) {
    if (parsers != null) {
      this.parsers = Arrays.asList(parsers);
    }
  }

  @Override
  public void onConfigChange(IntegrationSettings settings) {
    // Do nothing
  }

  @Override
  public WebHookParser getParser(WebHookPayload payload) {
    String webhookEvent = payload.getHeaders().get(ZapierEventConstants.ZAPIER_EVENT_TYPE_HEADER);

    for (WebHookParser parser : parsers) {
      if (parser.getEvents().contains(webhookEvent)) {
        return parser;
      }
    }

    return defaultParser;
  }

}
