package acme;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import xyz.jetdrone.vertx.lambda.Lambda;

/**
 * This is your main function, implement the handle method with your function.
 *
 * If function composition is required, add more functions and register them
 * in the META-INF/services/io.vertx.core.Handler
 */
public class MyFunction implements Lambda<JsonObject> {

  @Override
  public void init(Vertx vertx) {
    // If the dependency is enabled in the pom.xml then activate the provider here 
    // com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider.install();
  }

  @Override
  public void handle(Message<JsonObject> event) {
    System.out.println("HEADERS: " + event.headers());
    System.out.println("BODY: " + event.body());

    // Here your business logic...

    event.reply(
      new JsonObject()
        .put("message", "Hello OpenFaaS!"));
	}

}
