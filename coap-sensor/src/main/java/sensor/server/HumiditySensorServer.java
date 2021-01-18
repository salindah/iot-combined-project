package sensor.server;

import com.google.iot.coap.Coap;
import com.google.iot.coap.Code;
import com.google.iot.coap.InboundRequest;
import com.google.iot.coap.InboundRequestHandler;
import com.google.iot.coap.LocalEndpoint;
import com.google.iot.coap.LocalEndpointManager;
import com.google.iot.coap.Resource;
import com.google.iot.coap.Server;
import com.google.iot.coap.UnsupportedSchemeException;
import java.io.IOException;

public class HumiditySensorServer {

  public static void main(String[] args) {

    try {
      LocalEndpointManager manager = new LocalEndpointManager();

      Server server = new Server(manager);

      LocalEndpoint udpEndpoint = manager.getLocalEndpointForScheme(Coap.SCHEME_UDP);

      server.addLocalEndpoint(udpEndpoint);

      Resource<InboundRequestHandler> rootResource = new Resource<>();


      InboundRequestHandler helloHandler = inboundRequest -> {

        String payload = getSensorValue(0,30);
        inboundRequest.sendSimpleResponse(Code.RESPONSE_CONTENT, payload);
        System.out.println("response sent");
      };

      rootResource.addChild("humidity", helloHandler);
      server.setRequestHandler(rootResource);

      server.start();
      System.out.println("Server started");
    } catch (IOException | UnsupportedSchemeException e) {
      e.printStackTrace();
    }
  }

  private static String getSensorValue(int min, int max){
    int random = (int) (Math.random() * ( max - min ));
    return random + "";
  }

}
