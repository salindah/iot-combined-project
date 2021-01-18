package gateway;

import coap.CoapClient;
import mqtt.broker.MqttServer;
import mqtt.client.HiveMqttClient;

public class GatewayManager {

  public static void main(String[] args) {

    CoapClient coapClient = new CoapClient();

    MqttServer mqttServer = new MqttServer();
    mqttServer.start();

    String topic = "weather/humidity";
    HiveMqttClient hiveMqttClient = new HiveMqttClient();
    hiveMqttClient.start();
    hiveMqttClient.connect();
    hiveMqttClient.subscribe(topic);

    while (true) {

      coapClient.connect();

      String uriPath = "humidity";
      String response = coapClient.sendRequest(CoapClient.REQUEST_TYPE_GET, uriPath, "");
      System.out.println("COAP CLIENT : Sensor value : " + response);

      coapClient.disconnect();

      hiveMqttClient.publishMessage(topic, response);

      try {
        Thread.sleep(1000 * 10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    //coapClient.disconnect();
  }
}
