package acme;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import xyz.jetdrone.vertx.lambda.Lambda;

/**
 * This is your main function, implement the handle method with your function.
 *
 * If function composition is required, add more functions and register them
 * in the META-INF/services/io.vertx.core.Handler
 */
public class TellMeAJoke implements Lambda<JsonObject> {

  WebClient client;
  @Override
  public void init(Vertx vertx) {
    // If the dependency is enabled in the pom.xml then activate the provider here
    // com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider.install();

    // create a single HTTP client for the lifecycle of this function
    client = WebClient.create(
      vertx,
      // the client by default will use SSL and trust all hosts
      new WebClientOptions().setSsl(true).setTrustAll(true));
  }

  /**
   * This Function will fetch a remote joke and return it as a JSON payload
   */
  @Override
  public void handle(Message<JsonObject> message) {
    client
      .get(443, "icanhazdadjoke.com", "/")
      .putHeader("Accept", "text/plain")
      // the remote request happens here
      .send(send -> {
        // error handling
        if (send.failed()) {
          message.fail(500, send.cause().getMessage());
          return;
        }

        // success handling
        HttpResponse<Buffer> response = send.result();
        message.reply(
          new JsonObject()
            .put("statusCode", response.statusCode())
          .put("joke", response.bodyAsString("UTF-8"))
        );
      });
	}
}
