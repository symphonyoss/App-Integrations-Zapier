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
 * A container for all the fields used on Zapier payloads and entities.
 * Constants provided by this class can be used to access fields on the JSON payload as well as
 * add attributes and entities on the entityML.
 *
 * Created by ecarrenho on 22/09/16.
 */
public final class ZapierEntityConstants {

  /**
   * Declares a private constructor to avoid class instantiation.
   */
  private ZapierEntityConstants() {}

  public static final String MESSAGE_CONTENT = "message_content";

  public static final String MESSAGE_HEADER = "message_header";

  public static final String MESSAGE_ICON_URL = "message_icon";

  public static final String FIELDS = "fields";

  public static final String ACTION_FIELDS = "action_fields";

  public static final String ACTION_FIELDS_FULL = "action_fields_full";

  public static final String ACTION_FIELDS_RAW = "action_fields_raw";

  public static final String ZAP = "zap";

  public static final String LIVE = "live";

  public static final String LINK = "link";

  public static final String NAME = "name";

  public static final String INTEGRATION_NAME = "zapier";

  // MessageML v2

  public static final String TYPE_FIELD = "type";

  public static final String VERSION_FIELD = "version";

  public static final String ICON_FIELD = "icon";

  public static final String HEADER_FIELD = "header";

  public static final String BODY_FIELD = "body";

}
