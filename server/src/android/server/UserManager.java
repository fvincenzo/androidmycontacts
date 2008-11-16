package android.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Classe di tipo singleton per la gestione dei dati degli utenti e il
 * salvataggio degli stessi sui files.
 * 
 * @author Nicolas Tagliani
 * @author Vincenzo Frascino
 */
public class UserManager {

	/**
	 * @uml.property name="users"
	 */
	private Map<String, User> users = new HashMap<String, User>();
	private Hashtable<String, SocketServer> usersocket = new Hashtable<String, SocketServer>();

	/**
	 * @uml.property name="u"
	 * @uml.associationEnd
	 */
	private static UserManager u = null;
	private Database db;

	private UserManager() {

		//Leggo la figurazione del server da file (conf/config.ini)
		FileReader configFis = null;
		@SuppressWarnings("unused")
		BufferedReader configIn = null;
		try {
			configFis = new FileReader("conf/config.ini");
			configIn = new BufferedReader(configFis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String host = null;
		String user = null;
		String pwd = null;
		
		try {
			host = configIn.readLine();
			user = configIn.readLine();
			pwd = configIn.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			configIn.close();
			configFis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Inizializzo il db
		db = new Database(host, "news", user, pwd);
		if (!db.connetti()) {
			System.out.println("Errore durante la connessione.");
			System.out.println(db.getErrore());
			System.exit(0);
		}

		try {
			// Eseguo una query sul database. La tabella si chiama Tbl.
			Vector<String[]> v = db.eseguiQuery("SELECT * FROM users;");

			// Stampiamo i risultati:
			int i = 0;
			while (i < v.size()) {
				String[] record = (String[]) v.elementAt(i);
//				System.out.println("Record numero " + (i + 1));
				User u = new User(record[3], record[4], record[8], record[10],
						record[9], record[6], record[11], record[12],
						record[13]);
				users.put(record[3], u);
				i++;
			}
		} catch (Exception ex) {
			System.out.println("No database found. Creating a new one...");

		}

		for (User u : users.values()) {
			u.load(db);
		}

		//Avvio aggiornamento periodico
		RunTask upd = new RunTask();
		Thread tup = new Thread(upd);
		tup.start();
		
	}

	/**
	 * Richiamata periodicamente esegue l'update della lista locale
	 */
	public void update() {
		
		try {
			
			//Reload the list
			users.clear();
			
			// Eseguo una query sul database. La tabella si chiama Tbl.
			Vector<String[]> v = db.eseguiQuery("SELECT * FROM users;");

			// Stampiamo i risultati:
			int i = 0;
			while (i < v.size()) {
				String[] record = (String[]) v.elementAt(i);
//				System.out.println("Record numero " + (i + 1));
				User u = new User(record[3], record[4], record[8], record[10],
						record[9], record[6], record[11], record[12],
						record[13]);
				users.put(record[3], u);
				i++;
			}
		} catch (Exception ex) {
			System.out.println("No database found. Creating a new one...");

		}

		for (User u : users.values()) {
			u.load(db);
		}
		
	}
	
	/**
	 * Classe interna che si occupa dell'aggiornamento dei dati locali periodicamente
	 * @author Vincenzo Frascino
	 *
	 */
	class RunTask implements Runnable {
		  public void run() {
			update();
			try {
				//Aggiornamento avviene ogni 5 minuti
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
	}
	
	/**
	 * Metodo statico per ottenere l'istanaz della classe UserManager.
	 * 
	 * @return L'unico oggetto di tipo UserManager nel sistema
	 */
	public static UserManager getHinstance() {
		if (u == null)
			u = new UserManager();
		return u;

	}

	/**
	 * Metodo per aggiungere un nuovo utente al sistema in fase di registrazione
	 * 
	 * @param u
	 *            L'utente da aggiungere
	 * @return true se l'aggiunta e il salvataggio su file ha avuto successo
	 *         false altrimenti
	 */
	public boolean addUser(User u) {
		if (!users.containsKey(u.getUser())) {
			users.put(u.getUser(), u);
			commit();
			return true;
		}
		return false;
	}

	/**
	 * Metodo per ottenere un utente dato il suo nickname
	 * 
	 * @param uname
	 *            il nickname dell'utente da ottenere
	 * @return L'User relativo all'utente
	 */
	public User getUser(String uname) {
		return users.get(uname);
	}

	/**
	 * Metodo per impostare un utente come connesso. In questo modo ogni
	 * nickname sara' connesso al sistema in un solo thread
	 * 
	 * @param uname
	 *            Il nickname dell'utente connesso
	 * @param ss
	 *            Il thread associato alla connessione
	 */
	public void setConnected(String uname, SocketServer ss) {
		this.usersocket.put(uname, ss);

	}

	/**
	 * Verifica se un utente e' gia' connesso al sistema
	 * 
	 * @param uname
	 *            Il nickname dell'utente da controllare
	 * 
	 * @return true se presente false altrimenti
	 */
	public boolean unameConnected(String uname) {
		return this.usersocket.containsKey(uname);

	}

	/**
	 * Rimuove una connessione dal sistema
	 * 
	 * @param uname
	 *            Il nickname dell'utente del quale si vuole chiudere la
	 *            connessione
	 */
	public void removeUnameConnected(String uname) {
		SocketServer s = (SocketServer) this.usersocket.get(uname);
		s.quit();
		this.usersocket.remove(uname);

	}

	/**
	 * Ritorna tutto l'elenco degli utenti registrati al sistema sotto forma di
	 * Set<String>
	 * 
	 * @return l'elenco degli utenti registrati nel sistema
	 * @uml.property name="users"
	 */
	public Set<String> getUsers() {
		return users.keySet();
	}
	
	/**
	 * Ritorna la lista delle news ad un utente loggato
	 * @return lista news
	 */
	public String getNews() {
		
		String s="";
		
		// Eseguo una query sul database. La tabella si chiama notizie.
		String query = "SELECT * FROM notizie ORDER BY id DESC";
		Vector<String[]> v = db.eseguiQuery(query);

		// Stampiamo i risultati:
		int i = 0;
		while ( i<v.size() ) {
		   String[] record = (String[]) v.elementAt(i);
		   System.out.println("Record numero " + (i+1) );
		   for (int j=0; j<record.length; j++) {
		      s += record[j];
		      s += "$";
		   }
		   i++;
		}
		
		return s;
	}

	/**
	 * Finalizza le modifiche agli utenti scrivendole su file. Da invocare dopo
	 * ogni modifica a qualunque utente per rendere permanenti queste modifiche
	 * 
	 * @return true se ha avuto successo false altrimenti
	 */
	public boolean commit() {
		DBHelper dbh;
		String query;
		try {

			for (User u : users.values()) {
				dbh = u.saveMe();
				query = "UPDATE users SET password='" + dbh.getPwd()
						+ "', email='" + dbh.getMail() + "', mobile='"
						+ dbh.getMobile() + "', work='" + dbh.getWork()
						+ "', home='" + dbh.getHome() + "', im='" + dbh.getIm()
						+ "', geo='" + dbh.getGeo() + "', preferred='"
						+ dbh.getPreferredMode() + "' WHERE user='"
						+ dbh.getUname() + "';";
				// Eseguo un aggiornamento sul campo 'nomecampo' della tabella
				// users:
				if (!db.eseguiAggiornamento(query)) {
					System.out.println("Errore nell'aggiornamento!");
					System.out.println(db.getErrore());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

}
