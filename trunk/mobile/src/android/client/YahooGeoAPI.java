package android.client;

import java.io.IOException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Classe utilizzata per ottenere le coordinate geografiche tramite il servizio di yahoo
 * 
 * @author Nicolas Tagliani
 * @author Vincenzo Frascino
 *
 */
public class YahooGeoAPI {

	
	private static final String APPID = "MyContactsClient";
	private static final String YAHOO_GEO_API_URL = "http://local.yahooapis.com/MapsService/V1/geocode?appid=";
	private static HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
	
	/**
	 * Metodo che ritorna le coordinate geografiche corrispondendi all'indirizzo inserito sotto forma di XML
	 * 
	 * @param location L'indirizzo per cui stiamo cercando le coordinate geografiche
	 * @return Il testo XML contenente anche le coordinate geografiche dell'indirizzo
	 * @throws IOException Se non e' possibile accedere al servizio di yahoo
	 */
	public static String getGeoCode(String location) throws IOException {
		StringBuffer url = new StringBuffer(YAHOO_GEO_API_URL);
		url.append(APPID).append("&location=").append(location.replaceAll(" ", "+"));
		
		HttpURL httpURL = new HttpURL(url.toString());
		HostConfiguration host = new HostConfiguration();
		host.setHost(httpURL.getHost(), httpURL.getPort());
		HttpConnection connection = connectionManager.getConnection(host);
		connection.open();
		GetMethod get = new GetMethod(url.toString());
		get.execute(new HttpState(), connection);
		String response = get.getResponseBodyAsString();
		connection.close();
		return response;
	}
}
