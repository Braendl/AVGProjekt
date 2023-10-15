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

/**
 * Diese Klasse repräsentiert einen Client für die Solarproduktionsanfrage.
 * Der Client kann Anfragen an einen Server über RabbitMQ senden und auf Antworten warten.
 */
public class Client implements AutoCloseable {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private SolarModel solarModel;

    /**
     * Erstellt eine neue Instanz des Clients und stellt die Verbindung zu RabbitMQ her.
     * @throws IOException         Wenn ein Fehler bei der Verbindungsherstellung auftritt.
     * @throws TimeoutException    Wenn ein Timeout bei der Verbindungsherstellung auftritt.
     */
    public Client() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
        this.solarModel = new SolarModel();

        ClientUI ui = new ClientUI(this);

    }

    /**
     * Hauptmethode zum Starten des Clients.
     * @param argv Die Befehlszeilenargumente.
     */
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

    /**
     * Überprüft, ob alle erforderlichen SolarModel-Daten gesetzt sind.
     * @return true, wenn alle Daten gesetzt sind, andernfalls false.
     */
    public boolean isSolarSet() {
        return !solarModel.getCity().isBlank() && !solarModel.getCountry().isBlank()
                && !solarModel.getHouseNumber().isBlank() && !solarModel.getStreet().isBlank()
                && solarModel.getPowerSolar() != 0;
    }

    /**
     * Sendet eine Solarproduktionsanfrage an den Server und wartet auf die Antwort.
     * @return Die Antwort des Servers.
     * @throws IOException         Wenn ein Fehler beim Senden der Anfrage auftritt oder nicht alle Daten gesetzt sind.
     * @throws InterruptedException Wenn die Warteoperation unterbrochen wird.
     * @throws ExecutionException  Wenn ein Fehler bei der Ausführung auftritt.
     * @throws TimeoutException   Wenn ein Timeout bei der Anfrage auftritt.
     */
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

    /**
     * Schließt die Verbindung zum RabbitMQ-Server.
     * @throws IOException Wenn ein Fehler beim Schließen der Verbindung auftritt.
     */
    public void close() throws IOException {
        connection.close();
    }
}
