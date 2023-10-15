package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Die Klasse SolarModel repräsentiert ein Datenmodell für Solarenergie.
 * Sie speichert Informationen über den Standort und die Solarstromerzeugung.
 */
public class SolarModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String country;
    private String city;
    private String street;
    private String houseNumber;
    private double powerSolar;

    /**
     * Gibt das Land des Solarstandorts zurück.
     * @return Der Name des Landes.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Setzt das Land des Solarstandorts.
     * @param land Der Landesname, der gesetzt werden soll.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gibt die Stadt des Solarstandorts zurück.
     * @return Der Name der Stadt.
     */
    public String getCity() {
        return city;
    }

    /**
     * Setzt die Stadt des Solarstandorts.
     * @param stadt Der Name der Stadt, der gesetzt werden soll.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gibt die Straße des Solarstandorts zurück.
     * @return Der Straßenname.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Setzt die Straße des Solarstandorts.
     * @param straße Der Straßenname, der gesetzt werden soll.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gibt die Hausnummer des Solarstandorts zurück.
     * @return Die Hausnummer.
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * Setzt die Hausnummer des Solarstandorts.
     * @param hausnummer Die Hausnummer, die gesetzt werden soll.
     */
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * Gibt die Solarstromerzeugung in Kilowatt zurück.
     * @return Die Solarstromerzeugung in Kilowatt.
     */
    public double getPowerSolar() {
        return powerSolar;
    }

    /**
     * Setzt die Solarstromerzeugung in Kilowatt.
     * @param solarLeistung Die Solarstromerzeugung in Kilowatt, die gesetzt werden soll.
     */
    public void setPowerSolar(double powerSolar) {
        this.powerSolar = powerSolar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SolarModel [country=" + country + ", city=" + city + ", street=" + street + ", houseNumber="
                + houseNumber + ", powerSolar=" + powerSolar + "]";
    }

    /**
     * Eigene Methode zur Serialisierung, um den Objektzustand in ein ObjectOutputStream zu schreiben.
     * @param oos Der ObjectOutputStream, in den das Objekt geschrieben wird.
     * @throws IOException Wenn ein Ein-/Ausgabefehler auftritt.
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
     * Eigene Methode zur Deserialisierung, um den Objektzustand aus einem ObjectInputStream zu lesen.
     * @param ois Der ObjectInputStream, aus dem das Objekt gelesen wird.
     * @throws ClassNotFoundException Wenn die Klasse eines serialisierten Objekts nicht gefunden werden kann.
     * @throws IOException Wenn ein Ein-/Ausgabefehler auftritt.
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
