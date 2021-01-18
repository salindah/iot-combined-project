package sensor.server;

import com.mbed.coap.exception.CoapCodeException;
import com.mbed.coap.packet.Code;
import com.mbed.coap.server.CoapExchange;
import com.mbed.coap.utils.CoapResource;

public class HumidityResource extends CoapResource {

  private int sensor = 0;

  @Override
  public void get(CoapExchange coapExchange) throws CoapCodeException {

    int sensor = getSensor();
    int humidityValue = getHumidityValue(sensor);
    long currentTime = System.currentTimeMillis();

    String payload = sensor + ":" + humidityValue + ":" + currentTime;
    coapExchange.setResponseBody(payload);
    coapExchange.setResponseCode(Code.C205_CONTENT);
    coapExchange.sendResponse();
  }

  private int getSensor(){
    if(sensor == 6){
      sensor = 0;
    }
    sensor++;
    return sensor;
  }

  private int getHumidityValue(int sensor){

    int humidityLevel = 0;
    switch (sensor){
      case 1:
        humidityLevel = getRandomNumber(0, 40);
        break;
      case 2:
        humidityLevel = getRandomNumber(20, 50);
        break;
      case 3:
        humidityLevel = getRandomNumber(40, 70);
        break;
      case 4:
        humidityLevel = getRandomNumber(80, 100);
        break;
      case 5:
        humidityLevel = getRandomNumber(50, 80);
        break;
      case 6:
        humidityLevel = getRandomNumber(0, 20);
        break;
    };
    return humidityLevel;
  }

  public int getRandomNumber(int min, int max) {
    return (int) ((Math.random() * (max - min)) + min);
  }
}
