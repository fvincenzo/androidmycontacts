package android.client;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Classe per effettuare il parsing delle coordinate geografiche ottenute da yahoo sotto forma di XML.
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class YahooGeocodeHandler extends DefaultHandler {
	/**
	 * @uml.property  name="latitude"
	 */
	private String latitude = "";
    /**
	 * @uml.property  name="longitude"
	 */
    private String longitude = "";

    private boolean isLatitude = false;
    private boolean isLongitude = false;

    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts) throws SAXException {
        if (localName.equals("Latitude")) {
            isLatitude = true;
        } else if (localName.equals("Longitude")) {
            isLongitude = true;
        }
    }

    @Override
    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        if (localName.equals("Latitude")) {
            isLatitude = false;
        } else if (localName.equals("Longitude")) {
            isLongitude = false;
        }
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        if (isLatitude) {
            latitude = new String(chars, i, i1);
        } else if (isLongitude) {
            longitude = new String(chars, i, i1);
        }
    }

    /**
	 * La latitudine sotto forma di String
	 * @return  la latitudine
	 * @uml.property  name="latitude"
	 */
    public String getLatitude() {
        return latitude;
    }

    /**
	 * La longitudine sotto forma di String
	 * @return  la longitudine
	 * @uml.property  name="longitude"
	 */
    public String getLongitude() {
        return longitude;
    }
    
    /**
     * La latitudine sotto forma di long
     * 
     * @return la latitudine
     */
    public long getLatitudeAsLong() {
    	if (latitude!=null && latitude.length()>0) {
    		return (long)(Double.parseDouble(latitude)*1000000);
    	} else {
    		return -1;
    	}
    }
    
    /**
     * La longitudine sotto forma di long
     * 
     * @return la longitudine
     */
    public long getLongitudeAsLong() {
    	if (longitude!=null && longitude.length()>0) {
    		return (long)(Double.parseDouble(longitude)*1000000);
    	} else {
    		return -1;
    	}
    }
}