package android.client;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WSInvoke {

	private static final String SOAP_ACTION = "HelloYou";
	private static final String METHOD_NAME = "getHello";
	private static final String NAMESPACE = "urn:HelloYou";
	private static final String URL = "http://10.0.2.2/android/news.php";
	private Object resultRequestSOAP = null;
	private int result0 = 0;

	public int returnValue() {

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		// SoapObject
//		request.addProperty("firstname", "John");
//		request.addProperty("lastname", "Williams");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultRequestSOAP = envelope.getResponse();
			String[] results = (String[]) resultRequestSOAP;

			result0 = Integer.parseInt(results[0]);

		} catch (Exception aE) {
			aE.printStackTrace();
		}

		return result0;
	}

}
