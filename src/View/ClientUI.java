package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import client.Client;

public class ClientUI extends JFrame {

    private static int width = 1000, height = 800;
    Client client;
    JTextArea textArea;

    public ClientUI(Client client) {
        this.client = client;

        setTitle("AvG");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        JTextField tCity, tCountry, tHouseNumber, tStreet, tPowerSolar;
        tCity = new JTextField("Stadt");
        tCity.setBounds(50, 50, 200, 30);
        tCountry = new JTextField("Land");
        tCountry.setBounds(50, 150, 200, 30);
        tHouseNumber = new JTextField("Hausnummer");
        tHouseNumber.setBounds(50, 250, 200, 30);
        tStreet = new JTextField("Straße");
        tStreet.setBounds(50, 350, 200, 30);
        tPowerSolar = new JTextField("Leistung der Solaranlage");
        tPowerSolar.setBounds(50, 450, 200, 30);

        textArea = new JTextArea();
        textArea.setLineWrap(true); 
        textArea.setWrapStyleWord(true); 
        textArea.setEditable(false); 

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(300, 100, 400, 600);
    
        add(scrollPane);
        add(tCity);
        add(tCountry);
        add(tHouseNumber);
        add(tStreet);
        add(tPowerSolar);

        JButton send = new JButton("send");
        send.setBounds(50, 500, 100, 50);
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    client.setCity(tCity.getText().strip());
                    client.setCountry(tCountry.getText().strip());
                    client.setHouseNumber(tHouseNumber.getText().strip());
                    client.setStreet(tStreet.getText().strip());
                    client.setPowerSolar(tPowerSolar.getText().strip());

                    setResult(client.call());
                } catch (IOException | InterruptedException | ExecutionException | TimeoutException | NumberFormatException e1) {
                    e1.printStackTrace();

                }
            }
        });

        add(send);
        setVisible(true);
    }
    
    public void setResult(String s) {
        textArea.setText(s);
    }
}
