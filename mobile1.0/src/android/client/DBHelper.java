package android.client;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Classe di supporto per il salvataggio delle posizioni preferite dall'utente.
 * Incapsula l'accesso al database MyContacts.db
 * 
 * @author Nicolas Tagliani
 * @author Vincenzo Frascino
 *
 */
public class DBHelper {

	private static String LOGGER = "android.client.DBHelper";

	/**
	 * Classe per la formattazione dei dati memorizzati nel database delle posizioni
	 * 
	 * @author Nicolas Tagliani
	 * @author Vincenzo Frascino
	 *
	 */
	class Location {
		/**
		 * L'id della riga del database
		 */
		public long _id;
		
		/**
		 * L'indirizzo in forma testuale delle coordinate geografiche
		 */
		public String address;
		
		/**
		 * Latitudine della posizione
		 */
		public int latitude;
		
		/**
		 * Longitudine della posizione
		 */
		public int longitude;
		
		/**
		 * Modo preferito per essere contattati associato alla posizione
		 */
		public String preferred;
		
		/**
		 * Flag che indica se si tratta del metodo di contatto di default
		 */
		public boolean isDefault;
	}

	private static final String DATABASE_CREATE =
		"create table locations (_id integer primary key autoincrement, "
		+ "latitude integer, longitude integer, preferred text not null, address text, defaultFlag integer);";

	private static final String DATABASE_NAME = "MyContacts.db";

	private static final String DATABASE_TABLE = "locations";

	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase db;

	/**
	 * Costruttore della classe DBHelper.
	 * Effettua la connessione al database o lo crea se questo non dovesse essere mai stato usato
	 * 
	 * @param ctx Il Context associato all'operazione
	 */
	public DBHelper(Context ctx) {

		try {
			db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		} catch (Exception e) {
			try {
				
				db.execSQL(DATABASE_CREATE);


			} catch (Exception e1) {
				Log.e(LOGGER, e1.toString());
				db = null;
			}
		}
	}

	/**
	 * Chiude la connessione al database
	 *
	 */
	public void close() {
		db.close();
	}

	/**
	 * Elimina dal database la Location l.
	 * 
	 * @param l La Location da cancellare. Non e' necessario inserire una DBHelper.Location completa, bastano latitudine, longitudine e metodo di contatto preferito
	 */
	public void deleteRow(Location l) {
		Log.i(LOGGER, "Sto per cancellare: "+l.latitude+" e "+l.longitude);
		db.delete(DATABASE_TABLE, "latitude=" + l.latitude+" AND longitude="+l.longitude+" AND preferred ='"+l.preferred+"'", null);
	}

	/**
	 * Inserisce una nuova Location nel database
	 * 
	 * @param p Coordinate del punto sulla mappa
	 * @param address Indirizzo associato al punto in forma testuale
	 * @param type Modo preferito per essere contattati associato al punto p.
	 * 
	 * @return true se ha successo false altrimenti
	 */
	public boolean addLocation(GeoPoint p, String address, String type) {

		String selection[] = new String[] {
				"_id"
		};
		Cursor c = db.query(DATABASE_TABLE, selection, 
				"latitude="+p.getLatitudeE6() +" AND longitude="+p.getLongitudeE6(), null, null, null, null);
		if (c.getCount() == 0) {
			ContentValues initialValues = new ContentValues();
			initialValues.put("address", address);
			initialValues.put("latitude", p.getLatitudeE6());
			initialValues.put("longitude", p.getLongitudeE6());
			initialValues.put("preferred", type);
			initialValues.put("defaultFlag", 0);
			if (db.insert(DATABASE_TABLE, null, initialValues) != -1){
				return true;
			}
		}
		return false;

	}
	
	/**
	 * Imposta il modo di default per essere contattati a type
	 * 
	 * @param type Il modo preferito per essere contattati
	 * 
	 * @return true se ha successo, false altrimenti
	 */
	public boolean setDefaultLocation(String type){
		String selection[] = new String[] {
				"preferred"
		};
		ContentValues pref = new ContentValues();
		pref.put("defaultFlag", 1);
		pref.put("preferred", type);
		
		Cursor c = db.query(DATABASE_TABLE, selection, "defaultFlag=1", null, null, null, null);
		if (c.getCount() > 0) {			
			db.update(DATABASE_TABLE, pref, "defaultFlag=1", null);
		}
		else {
			if (db.insert(DATABASE_TABLE, null, pref) != -1){
				return true;
			}
		}
		return false;
	}

	/**
	 * Ottiene il modo di default per essere contattati 
	 * 
	 * @return Una stringa che rappresenta il modo di default per essere contatati
	 */
	public String getDefault(){

		Cursor c = db.query(true, DATABASE_TABLE, new String[] {
		"preferred"},"defaultFlag=1", null, null, null, null,null);
		if (c.moveToFirst()) {
			return c.getString(0);
		} 
		return "MOBILE";
	}
	
	/**
	 * Ottiene tutte le Location inserite nel database sotto forma di lista
	 * 
	 * @return La lista delle location inserite nel database
	 */
	public List<Location> fetchAllRows() {
		List<Location> list = new LinkedList<Location>();
		Location row;

		Cursor c = db.query(true, DATABASE_TABLE, new String[] {
				"_id",  "latitude", "longitude","preferred","address", "defaultFlag"},"defaultFlag=0", null, null, null, null,null);
		while (c.moveToNext()) {
			row = new Location();
			row._id = c.getLong(0);
			row.latitude = c.getInt(1);
			row.longitude = c.getInt(2);
			row.preferred = c.getString(3);
			row.address = c.getString(4);
			if (c.getInt(5) == 1){
				row.isDefault = true;
			}
			else row.isDefault = false;
			list.add(row);
		} 
		return list;

	}


}
