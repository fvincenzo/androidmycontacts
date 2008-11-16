package android.server;

import java.util.*;

/**
 * Classe per la gestione dei dati di ogni singolo utente
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class User  {

	private String uname;
	/**
	 * @uml.property  name="pwd"
	 */
	private String pwd;
	/**
	 * @uml.property  name="mobile"
	 */
	private String mobile;
	/**
	 * @uml.property  name="work"
	 */
	private String work;
	/**
	 * @uml.property  name="home"
	 */
	private String home;
	/**
	 * @uml.property  name="mail"
	 */
	private String mail;
	/**
	 * @uml.property  name="im"
	 */
	private String im;
	/**
	 * @uml.property  name="geo"
	 */
	private String geo;

	//Questa Stringa può assumere i valori HOME WORK MAIL IM o MOBILE, di default per ogni utente è settata ad MOBILE
	/**
	 * @uml.property  name="preferredMode"
	 */
	private String preferredMode;

	/**
	 * @uml.property  name="friends"
	 */
	private Set<String> friends = new HashSet<String>();
	/**
	 * @uml.property  name="pendings"
	 */
	private Set<String> pendings = new HashSet<String>();
	private Object pendingsLock = new Object();
	private Object friendsLock = new Object();
	private Database db;

	/**
	 * Costruttore della classe User
	 * 
	 * @param username L'username dell'utente
	 * @param password La password dell'utente
	 * @param mobile Il numero di cellulare dell'utente
	 * @param home Il numero di casa dell'utente
	 * @param work Il numero del lavoro dell'utente
	 * @param mail L'indirizzo email dell'utente
	 * @param im Un recapito di istant messenger dell'utente
	 * @param lastPosition La posizione geografica dell'utente nel formato @latitudine,longitudine
	 * @param preferred Il metodo di contatto preferito dall'utente a scelta tra: MOBILE, HOME, WORK, MAIL, IM
	 */
	public User(String username,String password,String mobile,String home,String work,String mail,String im,String lastPosition, String preferred) {

		this.uname = username;
		this.pwd = password;
		this.home = home;
		this.mobile = mobile;
		this.work = work;
		this.mail = mail;
		this.im = im;
		this.geo = lastPosition;

		this.preferredMode = preferred;

	}
	
	/**
	 * Metodo per il caricamento da file dei dati relativi a pending friends e amici.
	 * I files che prova a leggere sono files contententi liste di utenti separate da a capo.
	 * La lista degli amici e' contenuta in nomeutente.sfrn mentre quella degli amici da accettare o meno si
	 * trova in: nomeutente.spnd
	 *
	 */
	public void load(Database db) {

		//Assegno il controllo sul database
		this.db = db;
		
		//Pulisco le liste se mi trovo in un ciclo diverso dal primo, 
		//mi serve per non avere duplicati dopo il primo lancio di load
		if(!friends.isEmpty()) friends.clear();
		if(!pendings.isEmpty()) pendings.clear();
		
		try
		{
			
			// Eseguo una query sul database. La tabella si chiama friends.
			Vector<String[]> v = db.eseguiQuery("SELECT friend FROM friends WHERE user='"+getUser()+"';");
			
			// Salvo i risultati in memoria locale:
			int i = 0;
			while (i < v.size()) {
				String[] friend = (String[]) v.elementAt(i);
				friends.add(friend[0]);
				i++;
			}
			
		} catch (Exception e) {
		}
		try {
			
			// Eseguo una query sul database. La tabella si chiama pendings.
			Vector<String[]> v = db.eseguiQuery("SELECT pending FROM pendings WHERE user='"+getUser()+"';");
			
			// Salvo i risultati in memoria locale:
			int i = 0;
			while (i < v.size()) {
				String[] pending = (String[]) v.elementAt(i);
				friends.add(pending[0]);
				i++;
			}

		}
		catch (Exception e){

		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			if ( ((User)obj).uname.equals(uname))
				return true;

		}
		return false;
	}

	/**
	 * Aggiunge un amico alla lista degli amici e salva il cambiamento sul file nomeutente.sfrn
	 * 
	 * @param f il nickname dell'amico da aggiungere alla propria lista
	 */
	public void addFriend(String f) {
		synchronized (friendsLock) {

			friends.add(f);

			try {
				
				String query = "INSERT INTO friends(user,friend) VALUES ('"+getUser()+"','"+f+"');";
				boolean b = db.eseguiAggiornamento(query);
				if(b==true) {
					System.out.println(query);
					removePenginds(f);
				}
					
			}
			catch (Exception e){
			}

		}

	}
	
	/**
	 * Aggiunge un utente alla lista dei pending friends e salva il cambiamento su file nomeutente.spnd
	 * 
	 * @param pen il nickname dell'amico da aggiungere alla propria lista
	 */
	public void addPendings(String pen) {
		synchronized (pendingsLock) {

			pendings.add(pen);

			try {
				
				String query = "INSERT INTO pendings(user,pending) VALUES ('"+getUser()+"','"+pen+"');";
				boolean b = db.eseguiAggiornamento(query);
				if(b==true) System.out.println(query);			
			
			} catch (Exception e){

			}
		}
	}

	/**
	 * Rimuove un utente dalla lista dei pending friends salvando la lista corrente sul file nomeutente.spnd
	 * 
	 * @param pen il nickname dell'amico da rimuovere dalla propria lista
	 */
	public void removePenginds(String pen) {
		synchronized (pendingsLock) {

			try {
				pendings.remove(pen);
				
				String query = "DELETE FROM pendings WHERE user='"+getUser()+"',pending='"+pen+"';";
				boolean b = db.eseguiAggiornamento(query);
				if(b==true) System.out.println(query);	
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ottiene la lista degli amici come insieme di Stringhe
	 * @return  la lista degli amici
	 * @uml.property  name="friends"
	 */
	public Set<String> getFriends(){
		return friends;
	}
	
	/**
	 * Ottiene la lista dei pendingfriends come insieme di Stringhe
	 * @return  la lista dei pendingfriends
	 * @uml.property  name="pendings"
	 */
	public Set<String> getPendings(){
		return pendings;
	}

	@Override
	public String toString() {

		String user = this.uname+"$"+this.geo+"$"+this.mobile+"$"+this.work+"$"+this.home+"$"+this.mail+"$"+this.im;
		return user;

	}

	/**
	 * Metodo richiamato dallo usermanager per salvare i dati dell'utente sul file degli utenti
	 * 
	 * @return Un DBHelper formattata contenente tutti i dati dell'utente
	 */
	public DBHelper saveMe(){
		
		DBHelper db = new DBHelper(uname,pwd,mobile,work,home,mail,im,geo,preferredMode);
		return db;	


	}

	/**
	 * Stringa utilizzata dai client per ottenere i parametri dell'utente da poter inserire in rubrica.
	 * 
	 * @return una stringa nella forma: username$posizione_geografica$numero_cellulare$numero_lavoro$numero_casa$indirizzo_mail$istant_messenger
	 */
	public String getUserInfo() {

		String user = this.uname+"$"+this.geo+"$"+this.mobile+"$"+this.work+"$"+this.home+"$"+this.mail+"$"+this.im;
		return user;

	}

	/**
	 * Getter del nome utente
	 * 
	 * @return il nomeutente
	 */
	public String getUser() {
		return uname;
	}

	/**
	 * Getter della password
	 * @return  la password
	 * @uml.property  name="pwd"
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * Getter della posizione geografica
	 * @return  la posizione geografica nella forma @latitudine,longitudine
	 * @uml.property  name="geo"
	 */
	public String getGeo() {
		return geo;
	}

	/**
	 * Stringa utilizzata dai client per ottenere i parametri dell'utente da poter modificare.
	 * 
	 * @return una stringa nella forma: username$numero_cellulare$numero_lavoro$numero_casa$indirizzo_mail$istant_messenger
	 */
	public String getPersonal(){
		return uname+"$"+mobile+"$"+work+"$"+home+"$"+mail+"$"+im;
	}

	/**
	 * Getter del numero di cellulare
	 * @return  il numero di cellulare
	 * @uml.property  name="mobile"
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * Getter del numero del lavoro
	 * @return  il numero del lavoro
	 * @uml.property  name="work"
	 */
	public String getWork() {
		return work;
	}

	/**
	 * Getter del numero di casa
	 * @return  il numero di casa
	 * @uml.property  name="home"
	 */
	public String getHome() {
		return home;
	}

	/**
	 * Getter dell'indirizzo email
	 * @return  l'indirizzo email
	 * @uml.property  name="mail"
	 */
	public String getMail() {
		return mail;
	}
	
	/**
	 * Getter del contatto per istant messenger
	 * @return  il contatto per l'istant messenger
	 * @uml.property  name="im"
	 */
	public String getIm() {
		return im;
	}

	/**
	 * Getter del modo preferito per essere contattati
	 * @return  il modo preferito con cui essere contattati a scelta tra: MOBILE, HOME, WORK, MAIL, IM
	 * @uml.property  name="preferredMode"
	 */
	public String getPreferredMode() {
		return preferredMode;
	}

	/**
	 * Setter per il modo preferito con cui essere contattati
	 * @param preferredMode  il modo preferito con cui essere contattati a scelta tra: MOBILE, HOME, WORK, MAIL, IM
	 * @uml.property  name="preferredMode"
	 */
	public void setPreferredMode(String preferredMode) {
		this.preferredMode = preferredMode;
	}
	
	/**
	 * Setter per la posizione geografica
	 * @param geo  la posizione geografica nel formato  @latitudine,longitudine
	 * @uml.property  name="geo"
	 */
	public void setGeo(String geo) {
		this.geo = geo;
	}

	/**
	 * Setter per la password
	 * @param pwd  la password
	 * @uml.property  name="pwd"
	 */
	public void setPwd(String pwd){
		this.pwd = pwd;
	}
	
	/**
	 * Setter per il numero di cellulare
	 * @param mobile  il numero di cellulare
	 * @uml.property  name="mobile"
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * Setter per il numero del lavoro
	 * @param work  il numero del lavoro
	 * @uml.property  name="work"
	 */
	public void setWork(String work) {
		this.work = work;
	}

	/**
	 * Setter per il numero di casa
	 * @param home  il numero di casa
	 * @uml.property  name="home"
	 */
	public void setHome(String home) {
		this.home = home;
	}

	/**
	 * Setter per l'indirizzo email
	 * @param mail  l'indirizzo email
	 * @uml.property  name="mail"
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	/**
	 * Setter per l'istant messenger
	 * @param im  l'istant messenger
	 * @uml.property  name="im"
	 */
	public void setIm(String im) {
		this.im = im;
	}
}
