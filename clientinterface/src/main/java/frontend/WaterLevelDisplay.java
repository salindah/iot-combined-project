package frontend;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import java.util.UUID;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WaterLevelDisplay extends Application {

  Mqtt3AsyncClient client;

  Text waterLevel1;
  Text waterLevel2;
  Text waterLevel3;
  Text waterLevel4;
  Text waterLevel5;
  Text waterLevel6;

  @Override
  public void start(Stage stage) throws Exception {

    Font font = new Font(24.0);
    Font font48 = new Font(48.0);

    Text text1 = new Text("Field 1");
    text1.setX(25.0f);
    text1.setY(50.0f);
    text1.setFont(font);

    Text text2 = new Text("Field 2");
    text2.setX(125.0f);
    text2.setY(50.0f);
    text2.setFont(font);

    Text text3 = new Text("Field 3");
    text3.setX(225.0f);
    text3.setY(50.0f);
    text3.setFont(font);

    Text text4 = new Text("Field 4");
    text4.setX(325.0f);
    text4.setY(50.0f);
    text4.setFont(font);

    Text text5 = new Text("Field 5");
    text5.setX(425.0f);
    text5.setY(50.0f);
    text5.setFont(font);

    Text text6 = new Text("Field 6");
    text6.setX(525.0f);
    text6.setY(50.0f);
    text6.setFont(font);
//////////////////////////////

    waterLevel1 = new Text("0");
    waterLevel1.setX(40.0f);
    waterLevel1.setY(125.0f);
    waterLevel1.setFont(font48);
    waterLevel1.setFill(Color.BLUEVIOLET);

    waterLevel2 = new Text("0");
    waterLevel2.setX(140.0f);
    waterLevel2.setY(125.0f);
    waterLevel2.setFont(font48);
    waterLevel2.setFill(Color.BLUEVIOLET);

    waterLevel3 = new Text("0");
    waterLevel3.setX(240.0f);
    waterLevel3.setY(125.0f);
    waterLevel3.setFont(font48);
    waterLevel3.setFill(Color.BLUEVIOLET);

    waterLevel4 = new Text("0");
    waterLevel4.setX(340.0f);
    waterLevel4.setY(125.0f);
    waterLevel4.setFont(font48);
    waterLevel4.setFill(Color.BLUEVIOLET);

    waterLevel5 = new Text("0");
    waterLevel5.setX(440.0f);
    waterLevel5.setY(125.0f);
    waterLevel5.setFont(font48);
    waterLevel5.setFill(Color.BLUEVIOLET);

    waterLevel6 = new Text("0");
    waterLevel6.setX(540.0f);
    waterLevel6.setY(125.0f);
    waterLevel6.setFont(font48);
    waterLevel6.setFill(Color.BLUEVIOLET);

    client = MqttClient.builder()
        .useMqttVersion3()
        .identifier(UUID.randomUUID().toString())
        .serverHost("localhost")
        .serverPort(1883)
        .buildAsync();

    if (client != null) {
      client.connect()
          .whenComplete((connAck, throwable) -> {
            if (throwable != null) {
              System.out.println("MQTT CLIENT : An error occurred while connecting to the broker.");
            } else {
              System.out.println("MQTT CLIENT : Connected to the broker.");

              client.subscribeWith()
                  .topicFilter("waterlevel")
                  .callback(publish -> {
                    String payload = new String(publish.getPayloadAsBytes());
                    if (payload != null) {

                      String[] values = payload.split(":");
                      int sensor = Integer.parseInt(values[0]);
                      String waterLevel = values[1];
                      setWaterLevel(sensor, waterLevel);
                    }

                  })
                  .send()
                  .whenComplete((subAck, throwable1) -> {
                    if (throwable1 != null) {
                      // Handle failure to subscribe
                    } else {
                      // Handle successful subscription, e.g. logging or incrementing a metric
                    }
                  });
            }
          });
    }

    //Creating a Group object
    Group root = new Group(text1, text2, text3, text4, text5, text6,
        waterLevel1, waterLevel2, waterLevel3, waterLevel4, waterLevel5, waterLevel6);

    //Creating a scene object
    Scene scene = new Scene(root, 625, 200);

    //Setting title to the Stage
    stage.setTitle("Water Level Display");

    //Adding scene to the stage
    stage.setScene(scene);

    //Displaying the contents of the stage
    stage.show();
  }

  private void setWaterLevel(int sensor, String waterLevel) {

    System.out.println(sensor + " : " + waterLevel);
    if (sensor == 1) {
      waterLevel1.setText(waterLevel);
      waterLevel1.setFill(getFill(waterLevel));
    } else if (sensor == 2) {
      waterLevel2.setText(waterLevel);
      waterLevel2.setFill(getFill(waterLevel));
    } else if (sensor == 3) {
      waterLevel3.setText(waterLevel);
      waterLevel3.setFill(getFill(waterLevel));
    } else if (sensor == 4) {
      waterLevel4.setText(waterLevel);
      waterLevel4.setFill(getFill(waterLevel));
    } else if (sensor == 5) {
      waterLevel5.setText(waterLevel);
      waterLevel5.setFill(getFill(waterLevel));
    } else if (sensor == 6) {
      waterLevel6.setText(waterLevel);
      waterLevel6.setFill(getFill(waterLevel));
    }
  }

  private Color getFill(String waterLevel){
    if( waterLevel.equalsIgnoreCase("3")){
      return Color.DARKGREEN;
    }
    if( waterLevel.equalsIgnoreCase("2")){
      return Color.DARKOLIVEGREEN;
    }
    if( waterLevel.equalsIgnoreCase("1")){
      return Color.LIGHTGREEN;
    }
    return Color.BROWN;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
