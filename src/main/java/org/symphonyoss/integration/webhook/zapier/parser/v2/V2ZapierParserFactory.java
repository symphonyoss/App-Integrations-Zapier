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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.zapier.parser.ZapierParserFactory;

/**
 * Factory to build the parser related to the messageML v2.
 * Created by rsanchez on 05/04/17.
 */
@Component
public class V2ZapierParserFactory extends ZapierParserFactory {

  @Autowired
  public V2ZapierParserFactory(V2ZapierPostMessageParser parser) {
    super(parser);
  }

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V2.equals(version);
  }

}
