package mqtt.client;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import java.util.UUID;

public class HiveMqttClient {

  Mqtt3BlockingClient client = null;

  public void start() {
    client = Mqtt3Client.builder()
        .identifier(UUID.randomUUID().toString())
        .serverHost("localhost")
        .serverPort(1883)
        .buildBlocking();

  }

  public void connect() {

    try {
      if (client != null) {
        client.connect();
        System.out.println("MQTT CLIENT : Hive MQTT client connected.");
      }
    } catch (Exception e) {
      disconnect();
    }
  }

  public void subscribe(String topic) {

    try {
      if (client != null && topic != null) {
        client.subscribeWith()
            .topicFilter(topic)
            .qos(MqttQos.AT_MOST_ONCE)
            .send();
      }
    } catch (Exception e) {
      disconnect();
    }
  }

  public void publishMessage(String topic, String payload) {

    try {
      if (client != null) {
        System.out.println("MQTT CLIENT : Publish sensor value to the MQTT broker.");
        client.publishWith()
            .topic(topic)
            .qos(MqttQos.AT_MOST_ONCE)
            .retain(true)
            .payload(payload.getBytes()).send();
      }
    } catch (Exception e) {
      disconnect();
    }
  }

  private void disconnect() {

    try {
      if (client != null) {
        client.disconnect();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
