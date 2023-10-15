package src.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import model.SolarModel;

/**
 * Die `Server`-Klasse stellt einen RabbitMQ-Server dar, der auf Client-Anfragen wartet und
 * Berechnungen basierend auf den empfangenen Anfragen durchführt.
 */
public class Server {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    /**
     * Führt eine Testberechnung für das gegebene SolarModel durch.
     *
     * @param solarmodel Das SolarModel-Objekt, für das die Berechnung durchgeführt werden soll.
     * @return Das Ergebnis der Berechnung als Zeichenkette.
     */
    private static String calculate(SolarModel solarmodel) {

        return "Test Berechnung";
    }

    /**
     * Der Einstiegspunkt des Server-Programms. Der Server wartet auf eingehende Client-Anfragen und führt
     * Berechnungen basierend auf den empfangenen Anfragen durch.
     *
     * @param argv Die Befehlszeilenargumente, die bei der Programmausführung übergeben werden.
     * @throws Exception wenn ein Fehler während des Serverbetriebs auftritt.
     */
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
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

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
                
            } catch (RuntimeException e) {
                System.out.println(" [.] " + e);
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));
    }
}
