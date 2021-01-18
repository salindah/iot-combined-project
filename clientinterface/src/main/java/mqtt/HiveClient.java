package mqtt;


import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import java.util.UUID;

public class HiveClient {

  Mqtt3AsyncClient client;

  public void start() {
    client = MqttClient.builder()
        .useMqttVersion3()
        .identifier(UUID.randomUUID().toString())
        .serverHost("localhost")
        .serverPort(1883)
        .buildAsync();

  }

  public void connect() {
    if (client != null) {
      client.connect()
          .whenComplete((connAck, throwable) -> {
            if (throwable != null) {
              System.out.println("MQTT CLIENT : An error occurred while connecting to the broker.");
            } else {
              System.out.println("MQTT CLIENT : Connected to the broker.");
              subscribe("weather/temperature");
            }
          });
    }
  }

  public void subscribe(String topic) {
    if (client != null && topic != null) {
      client.subscribeWith()
          .topicFilter(topic)
          .callback(publish -> {
            String payload = new String(publish.getPayloadAsBytes());
            System.out.println("MQTT CLIENT : Message received : " + payload);
          })
          .send()
          .whenComplete((subAck, throwable) -> {
            if (throwable != null) {
              // Handle failure to subscribe
            } else {
              // Handle successful subscription, e.g. logging or incrementing a metric
            }
          });
    }
  }

  public void publishMessage(String topic, String payload) {
//    if (client != null) {
//      System.out.println("MQTT CLIENT : Publish sensor value to the MQTT broker.");
//      client.publishWith().topic(topic).qos(MqttQos.AT_LEAST_ONCE).payload(payload.getBytes())
//          .send();
//    }
  }

  public void disconnect() {
    if (client != null) {
      client.disconnect();
    }
  }
}
