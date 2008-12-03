package android.client;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import android.location.Location;
import android.location.LocationManager;
import android.os.DeadObjectException;
import android.util.Log;
import java.lang.Math;

/**
 * Thread che effettua in controllo della propria posizione a intervalli regolari ed aggiorna  le proprie informazioni quando necessario
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class GPSThread extends Thread {

	/**
	 * @uml.property  name="servInt"
	 * @uml.associationEnd  
	 */
	private ServiceInterface.Stub servInt =  null;

	private boolean run = true;	

	private LocationManager myLocationManager = null;

	private Location myLocation = null; 

	/**
	 * @uml.property  name="db"
	 * @uml.associationEnd  
	 */
	private DBHelper db;

	private long MINIMUM_TIME_BETWEEN_UPDATE = Settings.GPS_UPDATE_RATE*1000;

	/**
	 * Costruttore del GPSThread
	 * I parametri che riceve hanno tutti bisogno di un context associato e quindi devono provenire dall'esterno
	 * 
	 * @param s Riferimento allo Stub del servizio che implementa tutte le operazioni del protocollo
	 * @param loc riferimento a un Location manager
	 * @param db riferimento al DBHelper
	 */
	public GPSThread(ServiceInterface.Stub s,LocationManager loc,DBHelper db){
		super();
		servInt = s;

		//Setup Location Manager
		this.myLocationManager=loc;

		//Setup Database
		this.db=db;
	}


	@Override
	public void run() {


		String Position = null;
		String Precpos = null;

		super.run();
		while (run){
			try {
				if(myLocationManager!=null) {
					double apos = 0;
					double pos = 1000000; 
					
					//Istanzio il DB
					List<DBHelper.Location> location = db.fetchAllRows();

					//Prendo la mia posizione gps dal locationa Manager
					this.myLocation = (Location)myLocationManager.getCurrentLocation("gps");

					DecimalFormat df = new DecimalFormat("#00.000000");

					String lat_str = df.format(this.myLocation.getLatitude());
					String lng_str = df.format(this.myLocation.getLongitude());

					double lat_flo = Double.parseDouble(lat_str);
					double lng_flo = Double.parseDouble(lng_str);

					Thread.sleep(MINIMUM_TIME_BETWEEN_UPDATE);

					Iterator<DBHelper.Location> i = location.iterator();

					while(i.hasNext()) {

						DBHelper.Location l = (DBHelper.Location) i.next();
						
						Log.i("GPSThread","Eseguo il confronto con "+l.latitude+ " " + l.longitude);

						apos = disgeod(((double)l.latitude/1000000),((double)l.longitude/1000000),lat_flo,lng_flo);

						Log.i("GPSThread","posizione corrente: "+ lat_str + " " + lng_str+" distanza: "+apos+"da "+l.preferred);

						if(apos<pos) {

							Log.i("GPSThread","Siamo più vicini posizione corrente: "+ lat_str + " " + lng_str+" distanza: "+apos+"da "+l.preferred);

							pos = apos;
							Position = l.preferred;

						}

					}
					Log.i("GPSThread", "minima distanza riscontrata: " +pos+" da: "+Position);

					if(pos > ((double)1.5)){
						Log.i("GPSThread", "Troppo lontano, imposto la posizione di default");
						Position = db.getDefault();
					}
					
					if(!Position.equals(Precpos)) {

						Precpos = Position;
						servInt.setpreferred(Position);
						servInt.updatePosition(lat_flo, lng_flo);

					}

					//Log.v("GPSThread","posizione corrente: "+ pos +" "+ Position);

				} else {
					System.out.println("GPSThread.run():nullpointerexception");
				}

			} catch (InterruptedException e) {

			} catch (DeadObjectException e) {

			} 

		}
	}

	//Calcola la distanza tra due coordinate geografiche tenendo conto della sfericità terrestre
	private double disgeod (double latA, double lonA, double latB, double lonB) {

		/* Definisce le costanti e le variabili */
		double R = 6371;
		double pigreco = 3.1415927;
		double lat_alfa, lat_beta;
		double lon_alfa, lon_beta;
		double fi;
		double p, d;
		/* Converte i gradi in radianti */
		lat_alfa = pigreco * latA / 180;
		lat_beta = pigreco * latB / 180;
		lon_alfa = pigreco * lonA / 180;
		lon_beta = pigreco * lonB / 180;
		/* Calcola l'angolo compreso fi */
		fi = Math.abs(lon_alfa - lon_beta);
		/* Calcola il terzo lato del triangolo sferico */
		p = Math.acos(Math.sin(lat_beta) * Math.sin(lat_alfa) + Math.cos(lat_beta) * Math.cos(lat_alfa) * Math.cos(fi));
		/* Calcola la distanza sulla superficie terrestre R = ~6371 km */
		d = p * R;
		return d;
	}


	public void quit(){
		db.close();
		run = false;
	}

}
