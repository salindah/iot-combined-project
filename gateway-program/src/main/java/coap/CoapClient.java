package coap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class CoapClient {

  public static final int REQUEST_TYPE_GET = 1;
  public static final int REQUEST_TYPE_POST = 2;
  public static final int REQUEST_TYPE_PUT = 3;
  public static final int REQUEST_TYPE_DELETE = 4;


  public static final String INVALID_INPUT_MSG = "Invalid input was entered. Please enter one option put of above.";

  public static final String SERVER_NAME = "localhost";

  public static final int PORT_NUMBER = 5683;

  private DatagramSocket socket;

  private InetAddress address;

  public void connect() {
    try {
      socket = new DatagramSocket();
      address = InetAddress.getByName(SERVER_NAME);
      System.out.println("COAP CLIENT : Connected to the COAP Sensor server.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void disconnect(){
    try {
      if(socket != null){
        socket.close();
      }
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public String sendRequest(int requestType, String uriPath, String payload){

    if(requestType == REQUEST_TYPE_GET){
      return handleGetRequest(uriPath);
    } else if (requestType == REQUEST_TYPE_POST){
      return handlePostRequest(uriPath, payload);
    } else if (requestType == REQUEST_TYPE_PUT) {
      return handlePutRequest(uriPath, payload);
    } else if (requestType == REQUEST_TYPE_DELETE){
      return handleDeleteRequest(uriPath);
    } else {
      return "";
    }
  }

  private String handleGetRequest( String uriPath){
    CoapMessage message = constructMessage("GET", uriPath);
    DatagramPacket response = sendMessage(message);
    return decodeResponse(response);
  }

  private String handlePostRequest(String uriPath, String payload){

    CoapMessage message = constructMessage("POST", uriPath, payload);
    DatagramPacket response = sendMessage(message);
    return decodeResponse(response);
  }

  private String handlePutRequest(String uriPath, String payload){

    CoapMessage message = constructMessage("PUT", uriPath, payload);
    DatagramPacket response = sendMessage(message);
    return decodeResponse(response);
  }

  private String handleDeleteRequest(String uriPath){

    CoapMessage message = constructMessage("DELETE", uriPath);
    DatagramPacket response = sendMessage(message);
    return decodeResponse(response);
  }



  private boolean isInputValid(int input) {
    if (input == 1 || input == 2 || input == 3 || input == 4 || input == 5) {
      return true;
    }
    return false;
  }

  private CoapMessage constructMessage(String requestType, String uriPath){
    CoapMessage message = new CoapMessage();
    addHeader(message, requestType, uriPath);
    return message;
  }

  private CoapMessage constructMessage(String requestType, String uriPath, String payload){
    CoapMessage message = new CoapMessage();
    addHeader(message, requestType, uriPath);
    addPayload(message, payload);
    return message;
  }

  private void addHeader(CoapMessage message, String requestType, String uriPath){

    message.addToBuffer(convertToByte("01010000").byteValue());
    message.addToBuffer(getByteForRequestType(requestType));
    message.addToBuffer(convertToByte("10101010").byteValue());
    message.addToBuffer(convertToByte("01010101").byteValue());

    addOptions(message, uriPath);
  }

  private void addOptions(CoapMessage message, String uriPath){
    if(uriPath != null){

      Integer uriOptionType = new Integer(11);
      String uriOptionTypeStr = addLeadingZeros(Integer.toBinaryString(uriOptionType));

      Integer pathLength = uriPath.length();
      String uriPathStr = addLeadingZeros(Integer.toBinaryString(pathLength));

      message.addToBuffer(convertToByte(uriOptionTypeStr + uriPathStr).byteValue());

      char[] pathArray = uriPath.toCharArray();
      for(int i = 0; i < pathLength; i++ ){
        message.addToBuffer((byte) pathArray[i]);
      }
    }
  }

  private void addPayload(CoapMessage message, String payload){

    // Add the delimiter.
    message.addToBuffer(convertToByte("11111111").byteValue());

    // Append the payload.
    int length = payload.length();
    char[] payloadArray = payload.toCharArray();
    for(int i = 0; i < length; i++ ){
      message.addToBuffer((byte) payloadArray[i]);
    }
  }

  private byte getByteForRequestType(String type){

    String bitString = "00000000"; // 0.00
    if("GET".equals(type)){
      bitString = "00000001"; // 0.01
    } else if ("POST".equals(type)){
      bitString = "00000010"; // 0.02
    } else if ("PUT".equals(type)){
      bitString = "00000011"; // 0.03
    } else if ("DELETE".equals(type)){
      bitString = "00000100"; // 0.04
    }
    return convertToByte(bitString).byteValue();
  }

  private DatagramPacket sendMessage(CoapMessage coapMessage) {

    DatagramPacket request;
    DatagramPacket response = null;
    try {
      request = new DatagramPacket(coapMessage.getMessage(), coapMessage.getLength(), address, PORT_NUMBER);
      socket.send(request);
      //System.out.println("Packet send to coap server ...");

      byte[] receivingBuffer = new byte[1000];
      response = new DatagramPacket(receivingBuffer, receivingBuffer.length);
      socket.receive(response);

      //System.out.println("Response received from the server ...");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }


  private String decodeResponse(DatagramPacket response){

    String headerStr;
    String payloadStr = "";
    byte[] header;
    byte[] payload;
    int delimiterIndex = -1;

    byte[] temp = response.getData();
    if(response.getData() != null){

      for(int i = 0; i < response.getLength(); i++){
        int val = temp[i];
        if(val == -1){
          delimiterIndex = i;
        }
      }
      header = Arrays.copyOfRange(temp, 0, delimiterIndex);
      payload = Arrays.copyOfRange(temp, delimiterIndex + 1, response.getLength());

      headerStr = new String(header, 0, header.length);
      payloadStr = new String(payload, 0, payload.length);

      //System.out.println("++++++++++ Response ++++++++++++++++");
      //printByteArray(header);
      //System.out.println("Header : " + headerStr);
      //System.out.println("Payload : " + payloadStr);
    }
    return payloadStr;
  }




  private String addLeadingZeros(String value) {
    return ("0000" + value).substring(value.length());
  }

  private Integer convertToByte(String bitSequence){
    if(bitSequence != null && bitSequence.length() == 8){
      char[] seq = bitSequence.toCharArray();

      int bit0 = getIntValue(seq[0]);
      int bit1 = getIntValue(seq[1]);
      int bit2 = getIntValue(seq[2]);
      int bit3 = getIntValue(seq[3]);
      int bit4 = getIntValue(seq[4]);
      int bit5 = getIntValue(seq[5]);
      int bit6 = getIntValue(seq[6]);
      int bit7 = getIntValue(seq[7]);

      int sum = (64 * bit1) + (32 * bit2) + (16 * bit3) + (8 * bit4) + (4 * bit5) + (2 * bit6) + (1 * bit7);
      if(bit0 == 0){
        return new Integer(sum);
      } else {
        return new Integer (sum - 128);
      }
    }
    return null;
  }

  private int getIntValue(char bitValue) {
    if (bitValue == '0') {
      return 0;
    } else if (bitValue == '1') {
      return 1;
    }
    return -1;
  }

  /*
  Print the hexadecimal values for a given byte array.
  * */
  private void printByteArray(byte[] array){
    for(byte b : array){
      //String hexaDecimal = String.format("%02X", b);
      String hexaDecimal = "".format("0x%x", b);
      System.out.print( hexaDecimal + " ");

    }
    System.out.println("");
  }

  public void printTest(){

    int[] arr = new int[7];
    arr[0] = 12;
    arr[1] = 4;
    arr[2] = 16;
    arr[3] = 4;
    arr[4] = 20;
    arr[5] = 4;
    arr[6] = 8;

    for(int x : arr){
      String temp = Integer.toBinaryString(x);
      System.out.println(temp);
    }
  }


}
