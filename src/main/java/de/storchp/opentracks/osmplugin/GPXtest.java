import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.time.temporal.ChronoField;


public class Main {

    private static final double R = 6371.01; // Earth's radius in km

    public static void main(String[] args) {
        try {
            File inputFile = new File("2024-03-02_08_28_20.654_2024-03-02T08_28-05.gpx");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("trkpt");
            Node prevNode = null; // To store the previous node for distance calculation

            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                    .appendPattern("XXX")
                    .toFormatter(Locale.US);

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    double lat = Double.parseDouble(eElement.getAttribute("lat"));
                    double lon = Double.parseDouble(eElement.getAttribute("lon"));
                    double ele = Double.parseDouble(eElement.getElementsByTagName("ele").item(0).getTextContent());
                    OffsetDateTime time = OffsetDateTime.parse(eElement.getElementsByTagName("time").item(0).getTextContent(), formatter);

                    if (prevNode != null) {
                        Element prevElement = (Element) prevNode;
                        double prevLat = Double.parseDouble(prevElement.getAttribute("lat"));
                        double prevLon = Double.parseDouble(prevElement.getAttribute("lon"));
                        double prevEle = Double.parseDouble(prevElement.getElementsByTagName("ele").item(0).getTextContent());
                        OffsetDateTime prevTime = OffsetDateTime.parse(prevElement.getElementsByTagName("time").item(0).getTextContent(), formatter);

                        double distance = calculateDistance(lat, lon, prevLat, prevLon); // in km
                        double timeDiff = (time.toEpochSecond() - prevTime.toEpochSecond()) / 3600.0; // Convert seconds to hours
                        double speed = distance / timeDiff; // km/h
                        double slope = (ele - prevEle) / (distance * 1000) * 100; // Percentage

                        System.out.println("Distance: " + distance + " km, Speed: " + speed + " km/h, Slope: " + slope + "%");
                    }

                    prevNode = nNode; // Update previous node
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}