package org.symphonyoss.integration.webhook.zapier.parser;

import org.symphonyoss.integration.webhook.exception.WebHookParseException;

/**
 * Exception to report the failures to validate Zapier messages.
 *
 * Created by ecarrenho on 22/09/16.
 */
public class ZapierParserException extends WebHookParseException {

  private static final String COMPONENT = "Trello Webhook Dispatcher";

  /**
   * A Zapier exception.
   * @param message The exception message.
   */
  public ZapierParserException(String message) {
    super(COMPONENT, message);
  }

  /**
   * A Zapier exception resulting from another exception.
   * @param message The exception message.
   */
  public ZapierParserException(String message, Throwable cause) {
    super(COMPONENT, message, cause);
  }

}
