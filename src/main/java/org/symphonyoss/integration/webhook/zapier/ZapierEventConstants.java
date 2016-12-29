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

/**
 * Holds the list of supported Zapier events.
 *
 * Created by ecarrenho on 22/09/16.
 */
public final class ZapierEventConstants {

  /**
   * Declares a private constructor to avoid class instantiation.
   */
  private ZapierEventConstants() {
  }

  /**
   * The request header that carries the Zapier event type received by the Zapier WHIB.
   */
  public static final String ZAPIER_EVENT_TYPE_HEADER = "zapier-event-type";

  public static final String POST_MESSAGE = "post_message";

}
