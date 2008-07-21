package android.client;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Classe di supporto per formattare come lista i dati dell'utente
 * Questa classe viene usata come parser a fronte di un comando di tipo GETUSERDATA.
 * 
 * @author Nicolas Tagliani
 * @author Vincenzo Frascino
 *
 */
public class ContactUser {

	private String username;
	private String position;
	private String mobile;
	private String home;
	private String work;
	private String mail;
	private String IM;
	
	/**
	 * Costruttore che accetta la stringa con tutti i campi dell'utente
	 * 
	 * @param parseMe la stringa separata da $ contenente tutte le informazioni dell'utente in questo formato: nome_utente$posizione_geografica$numero_cellulare$numero_lavoro$numero_casa$email$istant_messenger
	 */
	public ContactUser(String parseMe){
		StringTokenizer tok = new StringTokenizer(parseMe, "$");
		this.username = tok.nextToken();
		this.position = tok.nextToken();
		this.mobile = tok.nextToken();
		this.work = tok.nextToken();
		this.home = tok.nextToken();
		this.mail = tok.nextToken();
		this.IM = tok.nextToken();
		
	}
	
	/**
	 * Ritorna i dati dell'utente sotto forma di lista
	 * 
	 * @return una lista contenente username, numero cellulare, numero casa, numero lavoro, email, istant messenger e posizione corrente in questo 
	 */
	public List<String> toList(){
		List<String> ret = new LinkedList<String>();
		ret.add(username);
		ret.add(mobile);
		ret.add(home);
		ret.add(work);
		ret.add(mail);
		ret.add(IM);
		ret.add(position);
		return ret;
	}
	
}
