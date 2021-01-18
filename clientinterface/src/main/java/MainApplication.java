import mqtt.HiveClient;

public class MainApplication {

  public static void main(String[] args) {

    String topic = "weather/temperature";
    HiveClient hiveMqttClient = new HiveClient();
    hiveMqttClient.start();
    hiveMqttClient.connect();
    //hiveMqttClient.subscribe(topic);
  }
}
