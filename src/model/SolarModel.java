package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SolarModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String country;
    private String city;
    private String street;
    private String houseNumber;
    private double powerSolar;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public double getPowerSolar() {
        return powerSolar;
    }

    public void setPowerSolar(double powerSolar) {
        this.powerSolar = powerSolar;
    }

    @Override
    public String toString() {
        return "" + country + "+" + city + "+" + street + "+" + houseNumber + "+" + powerSolar + "";
    }

    public String getLogString() {
        return "Land: " + country + "\nStadt: " + city + "\nStraße: " + street + "\nHausnummer: " + houseNumber
                + "\nLeistung der Solaranlage: " + powerSolar + "\n";
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(country);
        oos.writeObject(city);
        oos.writeObject(street);
        oos.writeObject(houseNumber);
        oos.writeObject(powerSolar);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.country = (String) ois.readObject();
        this.city = (String) ois.readObject();
        this.street = (String) ois.readObject();
        this.houseNumber = (String) ois.readObject();
        this.powerSolar = (Double) ois.readObject();
    }

}
