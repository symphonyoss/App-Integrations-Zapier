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
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.parser.WebHookParser;
import org.symphonyoss.integration.webhook.zapier.ZapierEventConstants;

import java.io.IOException;
import java.util.List;

/**
 * Base class that Zapier parsers should extend.
 *
 * Created by ecarrenho on 22/09/16.
 */
public abstract class ZapierParser implements WebHookParser {

  @Override
  public Message parse(WebHookPayload payload) throws WebHookParseException {
    try {
      String webhookEvent = payload.getHeaders().get(ZapierEventConstants.ZAPIER_EVENT_TYPE_HEADER);
      JsonNode rootNode = JsonUtils.readTree(payload.getBody());

      return buildMessage(webhookEvent, rootNode);
    } catch (IOException e) {
      throw new ZapierParserException(
          "Something went wrong while trying to validate a message from Zapier", e);
    }
  }

  protected abstract Message buildMessage(String webhookEvent, JsonNode rootNode);

}

