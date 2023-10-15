package src.client;

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

import model.SolarModel;

/**
 * Die `Client`-Klasse stellt eine Verbindung zu einem RabbitMQ-Server her und ermöglicht es,
 * Anfragen an den Server zu senden und Antworten zu empfangen.
 */
public class Client implements AutoCloseable {
    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    
    /**
     * Konstruktor für die `Client`-Klasse. Stellt eine Verbindung zum RabbitMQ-Server her.
     *
     * @throws IOException wenn ein Verbindungsfehler auftritt.
     * @throws TimeoutException wenn eine Zeitüberschreitung auftritt.
     */
    public Client() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    /**
     * Die `main`-Methode ist der Einstiegspunkt des Client-Programms.
     *
     * @param argv Die Befehlszeilenargumente, die bei der Ausführung des Programms übergeben werden.
     */
    public static void main(String[] argv) {
        try (Client client = new Client()) {
           
               SolarModel solarModel = new SolarModel();
               solarModel.setCity("testCity");
               solarModel.setCountry("testCountry");
               solarModel.setHouseNumber("3A");
               solarModel.setPowerSolar(2000);
               solarModel.setStreet("Lolipopweg");
           
                String response = client.call(solarModel);
                System.out.println(response);
            
        } catch (IOException | TimeoutException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sendet eine Anfrage an den Server und empfängt die Antwort.
     *
     * @param message Das SolarModel-Objekt, das die Anfrage enthält.
     * @return Die Antwort des Servers als Zeichenkette.
     * @throws IOException wenn ein Fehler beim Senden der Anfrage auftritt.
     * @throws InterruptedException wenn der Thread während des Wartens auf eine Antwort unterbrochen wird.
     * @throws ExecutionException wenn ein Fehler bei der Verarbeitung der Antwort auftritt.
     */
    public String call(SolarModel message) throws IOException, InterruptedException, ExecutionException {
        byte[] sendData;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
               oos.writeObject(message);
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
     *
     * @throws IOException wenn ein Fehler beim Schließen der Verbindung auftritt.
     */
    public void close() throws IOException {
        connection.close();
    }
}
