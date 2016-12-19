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
