package android.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Classe di tipo singleton per la gestione dei dati degli utenti e il salvataggio degli stessi sui files.
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class UserManager {

	/**
	 * @uml.property  name="users"
	 */
	private Map<String ,User> users = new HashMap<String, User>();
	private Hashtable<String, SocketServer> usersocket = new Hashtable<String, SocketServer>();
	
	/**
	 * @uml.property  name="u"
	 * @uml.associationEnd  
	 */
	private static UserManager u = null;

	private UserManager(){	

		FileReader usersFis = null;
		BufferedReader usersIn = null;
		try
		{
			usersFis = new FileReader(new File("users.slist"));
			usersIn = new BufferedReader(usersFis);
			String newUser = usersIn.readLine();
			while (newUser != null){
			User u = new User(newUser, usersIn.readLine(),usersIn.readLine(),usersIn.readLine(),usersIn.readLine(),usersIn.readLine(),usersIn.readLine(),usersIn.readLine(), usersIn.readLine());
				users.put(newUser, u);
				newUser = usersIn.readLine();
			}
			usersFis.close();
			usersIn.close();
		}
		catch(IOException ex)
		{
			System.out.println("No userfile found. Creating a new one...");

		}
	
		for(User u : users.values()){
			u.load();
		}

	}

	/**
	 * Metodo statico per ottenere l'istanaz della classe UserManager.
	 * 
	 * @return L'unico oggetto di tipo UserManager nel sistema
	 */
	public static UserManager getHinstance(){
		if (u == null) u = new UserManager();
		return u;

	}

	/**
	 * Metodo per aggiungere un nuovo utente al sistema in fase di registrazione
	 * 
	 * @param u L'utente da aggiungere
	 * @return true se l'aggiunta e il salvataggio su file ha avuto successo false altrimenti
	 */
	public boolean addUser(User u){
		if (!users.containsKey(u.getUser())){
			users.put(u.getUser(),u);
			commit();
			return true;
		}
		return false;
	}
	
	/**
	 * Metodo per ottenere un utente dato il suo nickname
	 * 
	 * @param uname il nickname dell'utente da ottenere
	 * @return L'User relativo all'utente
	 */
	public User getUser(String uname){
		return users.get(uname);
	}

	/**
	 * Metodo per impostare un utente come connesso. In questo modo ogni nickname sara' connesso al sistema in un solo thread
	 * 
	 * @param uname Il nickname dell'utente connesso
	 * @param ss Il thread associato alla connessione
	 */
	public void setConnected(String uname,SocketServer ss){
		this.usersocket.put(uname, ss);
		
	}
	
	/**
	 * Verifica se un utente e' gia' connesso al sistema
	 * 
	 * @param uname Il nickname dell'utente da controllare
	 * 
	 * @return true se presente false altrimenti
	 */
	public boolean unameConnected(String uname) {
		return this.usersocket.containsKey(uname);
	
	}
	
	/**
	 * Rimuove una connessione dal sistema
	 * 
	 * @param uname Il nickname dell'utente del quale si vuole chiudere la connessione
	 */
	public void removeUnameConnected(String uname) {
		SocketServer s = (SocketServer)this.usersocket.get(uname);
		s.quit();
		this.usersocket.remove(uname);
		
	}
	
	/**
	 * Ritorna tutto l'elenco degli utenti registrati al sistema sotto forma di Set<String>
	 * @return  l'elenco degli utenti registrati nel sistema
	 * @uml.property  name="users"
	 */
	public Set<String> getUsers() {
		return users.keySet();
	}
	
	/**
	 * Finalizza le modifiche agli utenti scrivendole su file. 
	 * Da invocare dopo ogni modifica a qualunque utente per rendere permanenti queste modifiche
	 * 
	 * @return true se ha avuto successo false altrimenti
	 */
	public boolean commit(){
		try{
			PrintWriter out_users = new PrintWriter(new FileOutputStream("users.slist"), true);
		
			for (User u : users.values()){
				out_users.print(u.saveMe());
				out_users.flush();
				}
			out_users.close();
			}catch (IOException e){
				e.printStackTrace();
				return false;
			}
			return true;

		
	}

}
