package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Diese Klasse repräsentiert ein SolarModel, das Informationen über eine Solaranlage enthält.
 * Es ist serialisierbar und kann in Objektströmen verwendet werden.
 */
public class SolarModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String country;
    private String city;
    private String street;
    private String houseNumber;
    private double powerSolar;

    /**
     * Gibt das Land zurück, in dem die Solaranlage sich befindet.
     * @return Das Land der Solaranlage.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Legt das Land fest, in dem die Solaranlage sich befindet.
     * @param country Das neue Land der Solaranlage.
     */
    public void setCountry(String country) {
        this.country = country;
    }

     /**
     * Gibt die Stadt zurück, in der die Solaranlage sich befindet.
     * @return Die Stadt der Solaranlage.
     */
    public String getCity() {
        return city;
    }

    /**
     * Legt die Stadt fest, in der die Solaranlage sich befindet.
     * @param city Die neue Stadt der Solaranlage.
     */
    public void setCity(String city) {
        this.city = city;
    }

     /**
     * Gibt die Straße zurück, auf der die Solaranlage sich befindet.
     * @return Die Straße der Solaranlage.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Legt die Straße fest, auf der die Solaranlage sich befindet.
     * @param street Die neue Straße der Solaranlage.
     */
    public void setStreet(String street) {
        this.street = street;
    }

     /**
     * Gibt die Hausnummer der Solaranlage zurück.
     * @return Die Hausnummer der Solaranlage.
     */
    public String getHouseNumber() {
        return houseNumber;
    }

     /**
     * Legt die Hausnummer der Solaranlage fest.
     * @param houseNumber Die neue Hausnummer der Solaranlage.
     */
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * Gibt die Leistung der Solaranlage zurück.
     * @return Die Leistung der Solaranlage.
     */
    public double getPowerSolar() {
        return powerSolar;
    }

     /**
     * Legt die Leistung der Solaranlage fest.
     * @param powerSolar Die neue Leistung der Solaranlage.
     */
    public void setPowerSolar(double powerSolar) {
        this.powerSolar = powerSolar;
    }

    /**
     * Gibt eine Zeichenfolge dar, die das SolarModel im Format "Land+Stadt+Straße+Hausnummer+Leistung" repräsentiert.
     * @return Eine Zeichenfolge, die das SolarModel repräsentiert.
     */
    @Override
    public String toString() {
        return "" + country + "+" + city + "+" + street + "+" + houseNumber + "+" + powerSolar + "";
    }

    /**
     * Gibt eine formatierte Zeichenfolge zurück, die die Informationen des SolarModels für Protokollzwecke enthält.
     * @return Eine Zeichenfolge mit formatierten Informationen.
     */
    public String getLogString() {
        return "Land: " + country + "\nStadt: " + city + "\nStraße: " + street + "\nHausnummer: " + houseNumber
                + "\nLeistung der Solaranlage: " + powerSolar + "\n";
    }

    /**
     * Benutzerdefinierte Methode zum Schreiben des Objekts in einen Objektausgabestrom.
     * @param oos Der Objektausgabestrom.
     * @throws IOException Wenn ein Fehler beim Schreiben des Objekts auftritt.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(country);
        oos.writeObject(city);
        oos.writeObject(street);
        oos.writeObject(houseNumber);
        oos.writeObject(powerSolar);
    }

    /**
     * Benutzerdefinierte Methode zum Lesen des Objekts aus einem Objekteingabestrom.
     * @param ois Der Objekteingabestrom.
     * @throws ClassNotFoundException Wenn die Klasse des gelesenen Objekts nicht gefunden wird.
     * @throws IOException Wenn ein Fehler beim Lesen des Objekts auftritt.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.country = (String) ois.readObject();
        this.city = (String) ois.readObject();
        this.street = (String) ois.readObject();
        this.houseNumber = (String) ois.readObject();
        this.powerSolar = (Double) ois.readObject();
    }

}
