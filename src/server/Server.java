package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import Logging.Logger;
import model.Coordinate;
import model.SolarModel;

public class Server {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static final String API_KEY = "&key=AIzaSyBOCq2HZ0vn_B1xRloHA4pml98zLfphQjI";

    private static final String API_COORDINATE_URI = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String API_SOLAR_URI = "https://solar.googleapis.com/v1/buildingInsights:findClosest?";
    
    private static final String API_QUALITY = "&requiredQuality=LOW";

    private static String calculate(SolarModel solarmodel) {
    
        Coordinate coordinates = calculateCoordinate(solarmodel);
        String response = "";
        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(API_SOLAR_URI + coordinates.toString()+ API_QUALITY + API_KEY ))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
            response = postResponse.body();

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }   

        return response; 
    }

    private static Coordinate calculateCoordinate(SolarModel solarmodel) {
        Coordinate c = new Coordinate();
        String response = "";
        try {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(API_COORDINATE_URI + solarmodel.toString() + API_KEY))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
            response = postResponse.body();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        JSONObject json = new JSONObject(response);
        JSONArray array = json.getJSONArray("results");
        json = array.getJSONObject(0);
        json = json.getJSONObject("geometry");
        json = json.getJSONObject("location");
        
        c.setLatitude(json.getDouble("lat"));
        c.setLongitude(json.getDouble("lng"));
        return c;

    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);

        channel.basicQos(1);

        System.out.println("Warte auf Client Anfragen");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                    .correlationId(delivery.getProperties().getCorrelationId()).build();

            String response = "";
            try {

                SolarModel message = null;

                try (ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
                        ObjectInputStream ois = new ObjectInputStream(bis)) {
                    message = (SolarModel) ois.readObject();

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(message);
                response = calculate(message);
                Logger.log(message.getLogString() + response);

            } catch (RuntimeException e) {
                System.out.println(e);
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
        }));
    }
}
