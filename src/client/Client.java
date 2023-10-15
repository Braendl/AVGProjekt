package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import View.ClientUI;
import model.SolarModel;

public class Client implements AutoCloseable {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private SolarModel solarModel;

    public Client() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
        this.solarModel = new SolarModel();

        ClientUI ui = new ClientUI(this);

    }

    public static void main(String[] argv) {

        try {
            Client client = new Client();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public void setCity(String city) {
        solarModel.setCity(city);
    }

    public void setCountry(String country) {
        solarModel.setCountry(country);
    }

    public void setHouseNumber(String houseNumber) {
        solarModel.setHouseNumber(houseNumber);
    }

    public void setPowerSolar(String powerSolar) {
        solarModel.setPowerSolar(Double.parseDouble(powerSolar));
    }

    public void setStreet(String street) {
        solarModel.setStreet(street);
    }

    public boolean isSolarSet() {
        return !solarModel.getCity().isBlank() && !solarModel.getCountry().isBlank()
                && !solarModel.getHouseNumber().isBlank() && !solarModel.getStreet().isBlank()
                && solarModel.getPowerSolar() != 0;
    }

    public String call() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        if (!isSolarSet())
            throw new IOException();

        byte[] sendData;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(solarModel);
            sendData = bos.toByteArray();
        }

        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, sendData);

        final CompletableFuture<String> response = new CompletableFuture<>();

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.complete(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.get();
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}
