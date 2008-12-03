package android.client;

public class Settings {
	
	/**
	 * Tipo di dato utilizzato in Contacts.ContactsMethods nel campo kind per identificare i contatti appartenenti al network MyContacts
	 */
	public static final int MY_CONTACTS_KIND = 500;
	
	/**
	 * Indirizzo a cui il client puo' trovare un server
	 */
	public static final String SERVER_ADDR = "10.0.2.2";
	
	/**
	 * Tempo di refresh in secondi per il segnale gps
	 */
	public static final long GPS_UPDATE_RATE = 5;
	
	/**
	 * Tempo di refresh in secondi per tutte le operazioni cicliche che vengono effettuate dal client
	 */
	public static final int CLYCLIC_UPDATE_RATE = 120;

}
