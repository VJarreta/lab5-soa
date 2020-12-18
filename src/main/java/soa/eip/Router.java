package soa.eip;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

// https://camel.apache.org/manual/latest/processor.html
@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .log("Searching twitter for \"${body}\"!").process(exchange -> {
        String payload = exchange.getIn().getBody(String.class);
        // do something with the payload and/or exchange here
        if (payload.matches("^[a-zA-Z0-9 ]+ max:[0-9]+$")) {
          //It contains "max:x"
          int count = Integer.parseInt(payload.split(":")[1]);
          //Delete "max:x" from search first
          exchange.getIn().setBody(payload.replace("max:" + count, ""));
          // Get x and set it as header
          exchange.getIn().setHeader("count", count);
        } else {
          //Default count value
          exchange.getIn().setHeader("count", 5);
        }
      })
      //Set header value as count in search
      .toD("twitter-search:${body}?count=${header.count}")
      .log("Body now contains the response from twitter:\n${body}");
  }
}
