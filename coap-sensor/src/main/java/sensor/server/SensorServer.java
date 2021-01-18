package sensor.server;

import com.mbed.coap.server.CoapHandler;
import com.mbed.coap.server.CoapServer;
import java.io.IOException;

public class SensorServer {

  public static void main(String[] args) {

    CoapServer server = CoapServer.builder().transport(5683).build();
    try {
      CoapHandler handler0 = new HumidityResource();
      server.addRequestHandler("/humidity", handler0);

      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
