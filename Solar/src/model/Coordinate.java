package model;

/**
 * Diese Klasse repräsentiert geografische Koordinaten, bestehend aus Breitengrad (Latitude) und Längengrad (Longitude).
 */
public class Coordinate {
    
    private double latitude;
    private double longitude;

    /**
     * Gibt den Breitengrad (Latitude) der Koordinate zurück.
     * @return Der Breitengrad der Koordinate.
     */
    public double getLatitude() {
        return latitude;
    }
     /**
     * Legt den Breitengrad (Latitude) der Koordinate fest.
     * @param latitude Der neue Breitengrad der Koordinate.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    /**
     * Gibt den Längengrad (Longitude) der Koordinate zurück.
     * @return Der Längengrad der Koordinate.
     */
    public double getLongitude() {
        return longitude;
    }
    /**
     * Legt den Längengrad (Longitude) der Koordinate fest.
     * @param longitude Der neue Längengrad der Koordinate.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    /**
     * Gibt eine Zeichenfolge dar, die die Koordinaten im Format "location.latitude=x&location.longitude=y" repräsentiert.
     * @return Eine Zeichenfolge, die die Koordinaten repräsentiert.
     */
    @Override
    public String toString() {
        return "location.latitude=" + latitude + "&location.longitude=" + longitude + "";
    }
    

}
