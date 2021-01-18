package frontend;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HumidityLevelDashboard extends Application {

  private static final String TOPIC_HUMIDITY = "weather/humidity";
  private static final String TOPIC_WATER_LEVEL = "waterlevel";

  Mqtt3AsyncClient client;

  Rectangle rectangle1;
  Rectangle rectangle2;
  Rectangle rectangle3;

  Rectangle rectangle4;
  Rectangle rectangle5;
  Rectangle rectangle6;


  Queue<Long> latencyValues = new LinkedList<>();
  LineChart lineChart;
  XYChart.Series dataSeries1 = new XYChart.Series();

  int index = 1;

  @Override
  public void start(Stage stage) throws Exception {

    rectangle1 = new Rectangle();
    rectangle1.setX(40.0f);
    rectangle1.setY(25.0f);
    rectangle1.setWidth(250.0f);
    rectangle1.setHeight(120.0f);

    rectangle2 = new Rectangle();
    rectangle2.setX(330.0f);
    rectangle2.setY(25.0f);
    rectangle2.setWidth(250.0f);
    rectangle2.setHeight(120.0f);

    rectangle3 = new Rectangle();
    rectangle3.setX(620.0f);
    rectangle3.setY(25.0f);
    rectangle3.setWidth(250.0f);
    rectangle3.setHeight(120.0f);

    rectangle4 = new Rectangle();
    rectangle4.setX(40.0f);
    rectangle4.setY(180.0f);
    rectangle4.setWidth(250.0f);
    rectangle4.setHeight(120.0f);

    rectangle5 = new Rectangle();
    rectangle5.setX(330.0f);
    rectangle5.setY(180.0f);
    rectangle5.setWidth(250.0f);
    rectangle5.setHeight(120.0f);

    rectangle6 = new Rectangle();
    rectangle6.setX(620.0f);
    rectangle6.setY(180.0f);
    rectangle6.setWidth(250.0f);
    rectangle6.setHeight(120.0f);

    Text text1 = new Text("Field 1");
    text1.setX(140.0f);
    text1.setY(160.0f);

    Text text2 = new Text("Field 2");
    text2.setX(430.0f);
    text2.setY(160.0f);

    Text text3 = new Text("Field 3");
    text3.setX(730.0f);
    text3.setY(160.0f);


    Text text4 = new Text("Field 4");
    text4.setX(140.0f);
    text4.setY(320.0f);

    Text text5 = new Text("Field 5");
    text5.setX(430.0f);
    text5.setY(320.0f);

    Text text6 = new Text("Field 6");
    text6.setX(730.0f);
    text6.setY(320.0f);



    //Text text1 = new Text("Field 1");

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
          .topicFilter(TOPIC_HUMIDITY)
          .callback(publish -> {
            String payload = new String(publish.getPayloadAsBytes());
            if(payload != null){

              String[] values = payload.split(":");
              int sensor = Integer.parseInt(values[0]);
              int sensorValue = Integer.parseInt(values[1]);
              long startTime = Long.parseLong(values[2]);
              setRectangleColor(sensor, sensorValue);
              logRecord(payload, startTime);
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


    Text fieldTxt = new Text("Select the field");

    ComboBox fieldComboBox = new ComboBox();
    fieldComboBox.getItems().add("Field 1");
    fieldComboBox.getItems().add("Field 2");
    fieldComboBox.getItems().add("Field 3");
    fieldComboBox.getItems().add("Field 4");
    fieldComboBox.getItems().add("Field 5");
    fieldComboBox.getItems().add("Field 6");

    Text wlTxt = new Text("Select the water level");
    ComboBox wlComboBox = new ComboBox();
    wlComboBox.getItems().add("Level 1");
    wlComboBox.getItems().add("Level 2");
    wlComboBox.getItems().add("Level 3");

    Button waterLevelButton = new Button("Set Water Level");

    EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        String field = ((String) fieldComboBox.getValue()).split(" ")[1];
        String wl = ((String) wlComboBox.getValue()).split( " ")[1];

        String payload = field + ":" + wl;
        publishMessage(TOPIC_WATER_LEVEL, payload);
        System.out.println("clicked " + field + ":" + wl);
      }
    };

    // when button is pressed
    waterLevelButton.setOnAction(event);
    HBox hbox = new HBox(20, fieldTxt, fieldComboBox, wlTxt, wlComboBox, waterLevelButton);
    hbox.setLayoutX(50);
    hbox.setLayoutY(350);

    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("No of epochs");

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Latency");

    lineChart = new LineChart(xAxis, yAxis);

    lineChart.setMinSize(800, 300);
    lineChart.setMaxSize(800, 300);
    lineChart.setPrefSize(800, 300);
    dataSeries1.setName("Latency Variation");
    lineChart.getData().add(dataSeries1);
    VBox vbox = new VBox(lineChart);
    vbox.setLayoutX(25);
    vbox.setLayoutY(425);


    //Creating a Group object
    Group root = new Group(rectangle1, rectangle2, rectangle3, rectangle4, rectangle5, rectangle6,
        text1, text2, text3, text4, text5, text6, hbox, vbox);

    //Creating a scene object
    Scene scene = new Scene(root, 910, 750);

    //Setting title to the Stage
    stage.setTitle("Humidity Level Dashboard");

    //Adding scene to the stage
    stage.setScene(scene);

    //Displaying the contents of the stage
    stage.show();
  }

  private void setRectangleColor(int sensor, int sensorValue){
    if(sensorValue >= 80){
      setTileColor(sensor, Color.GREEN);
    } else if (sensorValue >= 60 && sensorValue < 80 ){
      setTileColor(sensor, Color.LIGHTGREEN);
    } else if (sensorValue >= 40 && sensorValue < 60) {
      setTileColor(sensor, Color.YELLOW);
    } else if (sensorValue >= 20 && sensorValue < 40){
      setTileColor(sensor, Color.SANDYBROWN);
    } else {
      setTileColor(sensor, Color.BROWN);
    }
  }

  private void setTileColor(int sensor, Color color){
    if(sensor == 1){
      rectangle1.setFill(color);
    } else if(sensor == 2){
      rectangle2.setFill(color);
    } else if(sensor == 3){
      rectangle3.setFill(color);
    } else if(sensor == 4){
      rectangle4.setFill(color);
    } else if(sensor == 5){
      rectangle5.setFill(color);
    } else if(sensor == 6){
      rectangle6.setFill(color);
    }
  }

  private  synchronized void logRecord(String payload, long startTime){
    long endTime = System.currentTimeMillis();
    long latency = endTime - startTime;
    System.out.println("MQTT CLIENT : Message received : " + payload + "  Latency : " + latency);

    try{
      if(index >= 5){
        dataSeries1.getData().add(new XYChart.Data(index, latency));
      }
    } catch (IllegalStateException e){
    }
    index++;
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

  public static void main(String[] args) {
    launch(args);

  }
}
